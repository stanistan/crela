(ns crela.node-form.interface)

(defprotocol INodeForm
  (get-attr-names [form]))

(extend-protocol INodeForm

  clojure.lang.ISeq
  (get-attr-names [nodes]
    (mapcat get-attr-names nodes))

  clojure.lang.PersistentVector
  (get-attr-names [nodes]
    (get-attr-names (seq nodes))))
