(ns crela.t-hn
  (:use midje.sweet
        [crela core common])
  (:require [crela.url :refer [read-html-response]]))

;; "Crawl" HN
;; This is pretty much the equivalent of what is in the readme

(def hn-response (read-string (slurp "resources/test/hn-data")))
(def hn-html (read-html-response hn-response))

(fact "the data exists"
  hn-html => truthy)

(def-crawl-node HNCommon
  (with-fields
    page-title get-page-title))

(fact "can scrape with HNCommon"
  (let [front-page (scrape HNCommon hn-html)]
    front-page => truthy
    (:page-title front-page) => "Hacker News"
    (str (class front-page)) => #"HNCommon"))

(def-crawl-node HNArticle
  (merges HNCommon)
  (with-fields
    article-title [[:.title :a] first get-content]
    article-link  [[:.title :a] first get-link-href]
    score         [[:.subtext :span] first get-content]
    submitter     [[:.subtext :a] first get-content]
    num-comments  [[:.subtext :a] second get-content]))

(defn qualified-link?
  [url]
  (re-find #"^http" url))

(defn comments-link?
  [url]
  (and (not (qualified-link? url)) (re-find #"^item" url)))

(defn paging-link?
  [el]
  (= "More" (-> el :content first)))

(defn qualify-hn-link
  [url]
  (str "https://news.ycombinator.com/" url))

;; - merges the page-title from HNCommon
;; - has a recursive relation (so one can potentially do pagination)
(def-crawl-node HNPage
  (merges HNCommon)
  (with-nodes
    [HNArticle => articles] [[:.subtext :a]
                             #(map get-link-href %)
                             #(filter comments-link? %)
                             #(map qualify-hn-link %)]
    [HNPage => next-page]   [[:.title :a]
                             #(filter paging-link? %)
                             #(map get-link-href %)
                             first
                             qualify-hn-link]))

(fact "NHPage works"
  (let [front-page (scrape HNPage hn-html)]
    (:page-title front-page) => "Hacker News"
    (str (class front-page)) => #"HNPage"
    (count (:articles front-page)) => 30
    (map :content (:articles front-page)) => (has every? delay?)
    (:content (:next-page front-page)) => delay?))
