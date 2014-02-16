(defproject crela "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enlive "1.1.5"]
                 [clj-http "0.7.8"]]
  :profiles {:dev {:dependencies [[midje "1.6.2"]]
                   :plugins [[lein-midje "3.1.1"]]}})
