(ns crela.node-form.parser)

(defprotocol Parser
  (parse [data html] "Returns a hash-map"))

(extend-protocol Parser

  clojure.lang.ISeq
  (parse [col html]
    (->> col
         (map #(parse % html))
         (reduce merge)))

  clojure.lang.PersistentVector
  (parse [col html]
    (parse (seq col) html)))
