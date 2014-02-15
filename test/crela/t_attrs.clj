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
    (attrs/->NodeFormAttr :with-nodes 'Foo nil []))
  => [:foos]

  (attrs/get-attr-names
    (attrs/->NodeFormAttr :with-attrs 'foo nil []))
  => [:foo]

  (attrs/get-attr-names
    (attrs/->NodeForm
      :with-nodes
      [(attrs/->NodeFormAttr :with-nodes 'Foo nil [])
       (attrs/->NodeFormAttr :with-nodes 'Bar nil [])]))
  => [:foos :bars]

  (attrs/get-attr-names
    [(attrs/->NodeFormAttr :with-nodes 'Foo nil [])
     (attrs/->NodeFormAttr :with-nodes 'Bar nil [])])
  => [:foos :bars])

(fact "destruct-form"

  (attrs/destruct-form
    '(with-fields foo nil))
  => (attrs/->NodeForm :with-fields
      [(attrs/->NodeFormAttr :with-fields 'foo nil [])])

  (attrs/destruct-form
    '(with-fields foo _))
  => (attrs/->NodeForm :with-fields
      [(attrs/->NodeFormAttr :with-fields 'foo nil [])])

  (attrs/destruct-form
    '(with-fields
      foo _
      bar :.body
      baz [:.body]
      fooz [[:.body]]
      more [first]
      morez [[:.body :.foo :bar] first rest]))
  => (attrs/->NodeForm :with-fields
      [(attrs/->NodeFormAttr :with-fields 'foo nil [])
       (attrs/->NodeFormAttr :with-fields 'bar [:.body] [])
       (attrs/->NodeFormAttr :with-fields 'baz [:.body] [])
       (attrs/->NodeFormAttr :with-fields 'fooz [:.body] [])
       (attrs/->NodeFormAttr :with-fields 'more nil [first])
       (attrs/->NodeFormAttr :with-fields 'morez [:.body :.foo :bar] [first rest])]))
