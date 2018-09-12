(ns alloglurp.db
  (:require [alloglurp.core :as core]))

(core/get-incoming)
(core/get-bill-movies)
(core/get-week-movies)
(core/get-movie-detail "253927")
(core/get-movie-detail "250824")
(core/get-movie-detail "256044")

(-> (core/find-movie-id-from-query "las vegas parano")
    (core/get-movie-detail))

(-> (core/find-movie-id-from-query "12 hommes en colère")
    (core/get-movie-detail))

