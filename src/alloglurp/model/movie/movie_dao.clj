(ns alloglurp.model.movie.movie-dao
  (:require [alloglurp.model.movie.movie-schema :as movie-schema]
            [korma.core :refer :all]))

(defn insert! [record]
  "Insert a record into database."
  (insert movie-schema/allo-movie
          (values record)))

(defn find-list []
  (select movie-schema/allo-movie))