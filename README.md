# crela

[![Build Status](https://travis-ci.org/stanistan/crela.png?branch=master)](https://travis-ci.org/stanistan/crela)

A Clojure library designed to crawl the internet in a relational fashion.

On [Clojars](https://clojars.org/crela)

Built on top of [enlive](https://github.com/cgrand/enlive)

## Usage

Let's say we wanted to grab some data from HN, the usual target.

#### Simple Example

```clj
(use '[crela core common])

(def-crawl-node HNPage
  (with-fields
    links [[:.title :a] #(map get-link-href %)]
    title get-page-title
    yc-icon-url [:img first get-image-src]))

(scrape HNPage "https://news.ycombinator.com")

;; =>
;; #user.HNPage{:links
;;  ("http://www.mymodernmet.com/profiles/blogs/3d-gifs"
;;   "http://insanecoding.blogspot.com/2014/02/http-308-incompetence-expected.html"
;;   ;; .....
;;   "http://www.geektime.com/2014/02/16/google-acquires-slicklogin/"
;;   "news2"),
;;  :title "Hacker News",
;;  :yc-icon-url "y18.gif",
;;  :url "https://news.ycombinator.com"}
```

#### More complex example

With `crawl-node`s, we can create relations.

```clj
(use '[crela core common])

;; We can merge nodes.
(def-crawl-node HNCommon
  (with-fields
    page-title get-page-title))

;; We can also grab comments and things out of here
;; this is the actual item page.
(def-crawl-node HNArticle
  (merges HNCommon)
  (with-fields
    article-title [[:.title :a] first get-content]
    article-link  [[:.title :a] first get-link-href]
    score         [[:.subtext :span] first get-content]
    submitter     [[:.subtext :a] first get-content]
    num-comments  [[:.subtext :a] second get-content]))

;; we can still do this
;; (scrape HNArticle "https://news.ycombinator.com/item?id=xxxxx")

;; Some helpers

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

(def front-page (scrape HNPage "https://news.ycombinator.com"))
;; =>
;; #user.HNPage{:articles
;;   #<Delay@36b7f115: :not-delivered>
;;   #<Delay@6634b5c3: :not-delivered>
;;   #<Delay@e8949a1: :not-delivered>
;;   ....
;;   #<Delay@5740f07b: :not-delivered>
;;  ,
;;  :page-title "Hacker News",
;;  :next-page #<Delay@63182c3d: :not-delivered>
;;  :url "https://news.ycombinator.com

(deref (first (:articles front-page)))
;; =>
;; #user.HNArticle{.....}

(let [second-page @(:next-page front-page)]
  @(first (:articles second-page)))
;; =>
;; #user.HNArticle{.....}

```

Without the alias `HNArticle => articles`, the key would default to `hnarticles`.

- publish versioned on clojars and add that to the readme (dep on tests)

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
