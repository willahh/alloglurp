(ns alloglurp.process.movie.movie
  (:require [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.model.movie.movie-schema :refer [map-allo-data-to-record]]
            [alloglurp.process.allocine-scrapper.movie-detail :as movie-detail]))

(defn find-movie-data-from-query [query]
  (let [movie-id (movie-detail/find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (movie-detail/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))
