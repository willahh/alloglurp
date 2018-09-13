(ns alloglurp.test
  (:require [alloglurp.core :as core]))


(core/get-incoming)
(core/get-bill-movies)
(core/get-week-movies)
(core/get-movie-detail "253927")
(core/get-movie-detail "250824")
(core/get-movie-detail "256044")
(core/get-movie-detail "18457")


(do (let [movie-id (core/find-movie-id-from-query "las vegas parano")]
      (Thread/sleep 2000)
      (core/get-movie-detail movie-id)))

(do (let [movie-id (core/find-movie-id-from-query "Inception")]
      (Thread/sleep 2000)
      (core/get-movie-detail movie-id)))

(do (let [movie-id (core/find-movie-id-from-query "12 hommes en colère")]
      (Thread/sleep 2000)
      (core/get-movie-detail movie-id)))