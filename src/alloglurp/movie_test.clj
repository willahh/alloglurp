(ns alloglurp.movie-test
  (:require [alloglurp.movie-dao :as movie-dao]
            [alloglurp.core :as core]))

;; (core/get-incoming)
;; (core/get-bill-movies)
;; (core/get-week-movies)
;; (core/get-movie-detail "253927")
;; (core/get-movie-detail "256044")
;; (core/get-movie-detail "18457")


;; (do (let [movie-id (core/find-movie-id-from-query "Las vegas parano")]
;;       (Thread/sleep 1000)
;;       (core/get-movie-detail movie-id)))

;; (do (let [movie-id (core/find-movie-id-from-query "Batman")]
;;       (Thread/sleep 1000)
;;       (core/get-movie-detail movie-id)))

;; (do (let [movie-id (core/find-movie-id-from-query "Little miss")]
;;       (Thread/sleep 1000)
;;       (core/get-movie-detail movie-id)))

(do (let [movie-id (core/find-movie-id-from-query "Batman")]
      (Thread/sleep 1000)
      (core/get-movie-detail movie-id)))

(movie-dao/insert! {:alloid "1afsdf" :createDate "2018-05-18 21:20:20" :updateDate "2018-05-18 21:20:20"})

