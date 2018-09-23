(ns alloglurp.process.movie.movie
  (:require [korma.core :refer :all]
            [clj-http.client :as client]
            [alloglurp.model.movie.movie-schema :as movie-schema]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.model.movieid.movieid-dao :as movieid-dao]
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

;; (defn create-thumb-image-from-db-movie-table []
;;   (let [alloid-list (map :alloid (movie-dao/find-list))]
;;     (map (fn [alloid]
;;            (Thread/sleep 1000)
;;            (create-thumb-image-from-alloid alloid)) alloid-list)))



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

