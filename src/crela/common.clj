(ns crela.common
  (:require [net.cgrand.enlive-html :as html]))

(defn get-link-href
  [el]
  (get-in el [:attrs :href]))

(defn get-content
  [el]
  (-> el :content first))

(defn get-page-title
  [el]
  (-> (html/select el [:title]) first get-content))

(defn get-image-src
  [el]
  (get-in el [:attrs :src]))
