(ns crela.full-crawl)

(defn- already-scraped?
  [scraped-urls url]
  (@scraped-urls url))

(defn- mark-as-scraped
  [scraped-urls url]
  (swap! scraped-urls conj url))

(defn- handle-scraped-data
  [type data scrape-config scraped-urls]
  (when-let [{:keys [continue on-scrape]} (get scrape-config type)]
    (do
      (mark-as-scraped scraped-urls (:url data))
      (when on-scrape (on-scrape data))
      (doseq [k continue]
        (let [nodes (get data k)
              nodes (if (sequential? nodes) nodes [nodes])]
          (doseq [node nodes]
            (when-not (already-scraped? scraped-urls (:url node))
              (handle-scraped-data
                (eval (:type node))
                @(:content node)
                scrape-config
                scraped-urls))))))))

(defn get-crawler
  [scrape] ;; Inject the scrape function.
  (fn [crawl-node-name to-crawl config & opts]
    (let [opts (apply hash-map opts)
          urls (atom (or (:scraped-urls opts) #{}))]
      (do
        (handle-scraped-data
          crawl-node-name
          (apply scrape crawl-node-name to-crawl opts)
          config
          urls)
        urls))))
