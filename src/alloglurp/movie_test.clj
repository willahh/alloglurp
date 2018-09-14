(ns alloglurp.movie-test
  (:require [alloglurp.movie-dao :as movie-dao]
            [alloglurp.core :as core]))

;; (core/get-incoming)
;; (core/get-bill-movies)
;; (core/get-week-movies)
;; (core/get-movie-detail "253927")
;; (core/get-movie-detail "256044")
;; (core/get-movie-detail "18457")

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
  (let [movie-id (core/find-movie-id-from-query query)]
    (Thread/sleep 1000)
    (core/get-movie-detail movie-id)))

(defn find-movie-and-insert [query]
  (let [allo-data (find-movie-data-from-query query)
        record (map-allo-data-to-record allo-data)]
    (movie-dao/insert! record)))

;; (find-movie-and-insert "Batman")
;; (find-movie-and-insert "Inception")
;; (find-movie-and-insert "Las vegas parano")
;; (find-movie-and-insert "Les dents de la mer")
(find-movie-and-insert "Forest gump")




