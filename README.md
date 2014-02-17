# crela

[![Build Status](https://travis-ci.org/stanistan/crela.png?branch=master)](https://travis-ci.org/stanistan/crela)

A Clojure library designed to crawl the internet in a relational fashion.

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

;; We can also grab comments and things out of here
;; this is the actual item page.
(def-crawl-node HNArticle
  (with-fields
    page-title    get-page-title
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

(defn qualify-hn-link
  [url]
  (str "https://news.ycombinator.com/" url))

;; This defines another node
(def-crawl-node HNPage
  (with-nodes
    [HNArticle => articles] [[:.subtext :a]
                             #(map get-link-href %)
                             #(filter comments-link? %)
                             #(map qualify-hn-link %)]))

(def front-page (scrape HNPage "https://news.ycombinator.com"))
;; =>
;; #user.HNPage{:articles
;;   #<Delay@36b7f115: :not-delivered>
;;   #<Delay@6634b5c3: :not-delivered>
;;   #<Delay@e8949a1: :not-delivered>
;;   ....
;;   #<Delay@5740f07b: :not-delivered>
;;  ,
;;  :url "https://news.ycombinator.com

(deref (first (:articles front-page)))
;; =>
;; #user.HNArticle{.....}
```

Without the alias `HNArticle => articles`, the key would default to `hnarticles`.

## Todo

- Options for Async
- A better delayed type (with some semantics for reading/writing)

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
