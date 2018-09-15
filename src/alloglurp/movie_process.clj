(ns alloglurp.movie-process
  (:require [alloglurp.allo-scrapper.movie :as allo-scrapper]
            [alloglurp.movie-dao :as movie-dao]
            [alloglurp.movie-schema :refer [map-allo-data-to-record]]))

(defn find-movie-data-from-query [query]
  (let [movie-id (allo-scrapper/find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (allo-scrapper/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))
