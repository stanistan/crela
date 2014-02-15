(ns crela.t-attrs
  (:use midje.sweet)
  (require [crela.attrs :as attrs]))

(fact "get-attr-name has correct dispatch"
  (attrs/get-attr-name :foo "bar") => :bar
  (attrs/get-attr-name :with-attrs 'foo) => :foo
  (attrs/get-attr-name :with-nodes 'foo) => :foos
  (attrs/get-attr-name :with-nodes 'Foo) => :foos)

(fact "get-attr-names works for different types"

  ;; for a node
  (attrs/get-attr-names
    (attrs/->CrawlNodeAttr :with-nodes 'Foo nil []))
  => [:foos]

  (attrs/get-attr-names
    (attrs/->CrawlNodeAttr :with-attrs 'foo nil []))
  => [:foo]

  (attrs/get-attr-names
    (attrs/->CrawlNode
      :with-nodes
      [(attrs/->CrawlNodeAttr :with-nodes 'Foo nil [])
       (attrs/->CrawlNodeAttr :with-nodes 'Bar nil [])]))
  => [:foos :bars]

  (attrs/get-attr-names
    [(attrs/->CrawlNodeAttr :with-nodes 'Foo nil [])
     (attrs/->CrawlNodeAttr :with-nodes 'Bar nil [])])
  => [:foos :bars])

(fact "destruct-form"

  (attrs/destruct-form
    '(with-fields foo nil))
  => (attrs/->CrawlNode :with-fields
      [(attrs/->CrawlNodeAttr :with-fields 'foo nil [])])

  (attrs/destruct-form
    '(with-fields foo _))
  => (attrs/->CrawlNode :with-fields
      [(attrs/->CrawlNodeAttr :with-fields 'foo nil [])])

  (attrs/destruct-form
    '(with-fields
      foo _
      bar :.body
      baz [:.body]
      fooz [[:.body]]
      more [first]
      morez [[:.body :.foo :bar] first rest]))
  => (attrs/->CrawlNode :with-fields
      [(attrs/->CrawlNodeAttr :with-fields 'foo nil [])
       (attrs/->CrawlNodeAttr :with-fields 'bar [:.body] [])
       (attrs/->CrawlNodeAttr :with-fields 'baz [:.body] [])
       (attrs/->CrawlNodeAttr :with-fields 'fooz [:.body] [])
       (attrs/->CrawlNodeAttr :with-fields 'more nil [first])
       (attrs/->CrawlNodeAttr :with-fields 'morez [:.body :.foo :bar] [first rest])]))
