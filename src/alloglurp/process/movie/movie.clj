(ns alloglurp.process.movie.movie
  (:require [clj-http.client :as client]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.model.movie.movie-schema :refer [map-allo-data-to-record]]
            [alloglurp.process.allocine-scrapper.movie-detail :as movie-detail]
            [clojure.string :as str]))

(defn find-movie-data-from-query [query]
  (let [movie-id (movie-detail/find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (movie-detail/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))

(defn get-image-path-from-alloid [alloid]
  (str/join ["asset/image/" alloid "_thumb" ".jpg"]))

(defn create-thumb-image-from-alloid [alloid]
  (let [image-name ((get-image-path-from-alloid alloid))
        file (clojure.java.io/as-file image-name)]
    (when-not (.exists file)
      (clojure.java.io/copy
       (:body (client/get (:imageUrl (movie-dao/find-by-alloid alloid)) {:as :stream}))
       (java.io.File. image-name)))))

(defn create-thumb-image-from-db-movie-table []
  (let [alloid-list (map :alloid (movie-dao/find-list))]
    (map (fn [alloid]
           (Thread/sleep 1000)
           (create-thumb-image-from-alloid alloid)) alloid-list)))




