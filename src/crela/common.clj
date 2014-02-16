(ns crela.common
  (:require [net.cgrand.enlive-html :as html]))

(defn get-link-href
  [el]
  (get-in el [:attrs :href]))

(defn get-page-title
  [el]
  (-> (html/select el [:title])
      first
      :content
      first))

(defn get-image-src
  [el]
  (get-in el [:attrs :src]))
