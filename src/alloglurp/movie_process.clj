(ns alloglurp.movie-process
  (:require [alloglurp.allo-scrapper.movie :as allo-scrapper]
            [alloglurp.movie-dao :as movie-dao]))

(defn now []
  (.format (new java.text.SimpleDateFormat "yyyy-MM-dd hh:mm:ss")
           (java.util.Date.)))

(defn map-allo-data-to-record [allo-data]
  {:alloid (:movie-id allo-data)
   :createDate (now)
   :updateDate (now)
   :title (:movie-name allo-data)
   :description (:synopsis allo-data)
   :director "-"
   :genre (:genre allo-data)
   :imageUrl (:thumb-url allo-data)
   :pressEval (:press-eval allo-data)
   :specEval (:spectator-eval allo-data)})

(defn find-movie-data-from-query [query]
  (let [movie-id (allo-scrapper/find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (allo-scrapper/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))
