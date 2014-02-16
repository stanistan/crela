# crela

A Clojure library designed to crawl the internet in a relational fashion.

## Usage

Let's say we wanted to grab some data from HN, the usual target.

```clj
(use 'crela.core)
(use 'crela.common)

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

## Todo

- Make relations work
- add more options to the crawl node (modifiers?)

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
