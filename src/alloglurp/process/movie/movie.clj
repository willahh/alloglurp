(ns alloglurp.process.movie.movie
  (:require [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.model.movie.movie-schema :as movie-schema
             :refer [map-allo-data-to-record]]
            [alloglurp.model.movieid.movieid-dao :as movieid-dao]
            [alloglurp.model.movieid.movieid-status-constant :as movieid-status-constant]
            [alloglurp.process.allocine-scrapper.movie-detail :as movie-detail]
            [alloglurp.process.allocine-scrapper.movie-list :as movie-list]
            [alloglurp.service.scrapper.scrapper-helper :as scrapper-helper :refer :all]
            [clj-http.client :as client]
            [clojure.string :as str]
            [korma.core :refer :all]
            [net.cgrand.enlive-html :as html]))

(defn find-movie-id-from-query [query]
  (let [url (str/join ["http://www.allocine.fr/recherche/?q=" (str/replace query " " "+")])
        movie (html/html-snippet
               (get-html-from-phantomjs-memoize url))
        found-url (->
                   (filter (fn [m]
                             (and (:href m)
                                  (re-find #"/film/fichefilm_gen_cfilm=(\w+).html" (:href m))))
                           (let [links (map #(:attrs %) (-> movie
                                                            (html/select [:.rubric :.totalwidth :a])))]
                             links))
                   distinct first :href)]
    (when-let [id (re-find #"/film/fichefilm_gen_cfilm=(\d+).html" found-url)]
      (second id))))

(defn find-movie-data-from-query [query]
  (let [movie-id (find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (movie-detail/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))

(defn get-image-path-from-alloid [alloid]
  (str/join ["asset/image/" alloid "_thumb" ".jpg"]))

(defn get-image-path-from-alloid2 [image-name alloid]
  (str/join ["resources/public/asset/image/" image-name]))

(defn create-thumb-image-from-alloid [alloid]
  (let [image-name (str/join [alloid "_thumb" ".jpg"])
        image-path (get-image-path-from-alloid2 image-name alloid)
        file (clojure.java.io/as-file image-path)]
    (when-not (.exists file)
      (clojure.java.io/copy
       (:body (client/get (:imageUrl (movie-dao/find-by-alloid alloid)) {:as :stream}))
       (java.io.File. image-path))
      (update movie-schema/allo-movie
              (set-fields {:thumb image-name})
              (where {:alloid alloid})))
    image-name))

(defn create-thumb-image-from-db-movie-table []
  (let [movie-record-list (select movie-schema/allo-movie
                                  (where (or {:thumb ""} {:thumb nil})))]
    (letfn ((process-movie-record-without-thumb [alloid]
              (let [image-name (create-thumb-image-from-alloid alloid)]
                (update movie-schema/allo-movie
                        (set-fields {:thumb image-name})
                        (where {:alloid alloid})))))
      (for [movie-record movie-record-list]
        (do (process-movie-record-without-thumb (:alloid movie-record))
            (Thread/sleep 1000))))))

(defn find-movie-from-alloid-fetch-content-and-insert [alloid]
  (let [random-sleep-ms (+ (rand-int (- 7001 1000)) 1500)]
    (do
      (movieid-dao/update-movie-status-id alloid movieid-status-constant/in-progress)
      (Thread/sleep random-sleep-ms)
      (let [movie-html-row (movie-detail/get-movie-detail alloid)
            movie-record (movie-schema/map-allo-data-to-record movie-html-row)]
        (movieid-dao/update-movie-status-id alloid movieid-status-constant/insert)
        (movie-dao/insert! movie-record)
        (create-thumb-image-from-alloid alloid)
        (movieid-dao/update-movie-status-id alloid movieid-status-constant/finish)))))

(defn get-next-html-movie-row-and-insert-to-database []
  (let [movie-rows (movie-list/get-movie-row (html/html-snippet
                                              (scrapper-helper/get-html-from-phantomjs-memoize
                                               (movie-list/get-next-page-url))))]
    (alloglurp.model.movieid.movieid-dao/insert-html-rows-to-data-base movie-rows)))

(defn find-next-pending-movie-retrieve-content-and-insert []
  (let [movie-id-record (alloglurp.model.movieid.movieid-dao/find-next-pending)
        alloid (:alloid movie-id-record)]
    (find-movie-from-alloid-fetch-content-and-insert alloid)))

(defn proceed-by-10 []
  "Proceed item by "
  (do
    (println "proceed-by-10")
    (dotimes [i 10]
      (find-next-pending-movie-retrieve-content-and-insert))
    "Finish"))

;; (find-movie-from-alloid-fetch-content-and-insert (find-movie-id-from-query "Captain marvel"))


