(ns crela.url
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn fetch
  [url & opts]
  (let [opts (apply hash-map opts)]
    (client/get url opts)))

(defn read-html-string
  [html-string]
  (html/html-resource (java.io.StringReader. html-string)))

(defn read-html-response
  [http-response]
  (read-html-string (:body http-response)))

(defn fetch-html
  [url & opts]
  (read-html-response (apply fetch url opts)))
