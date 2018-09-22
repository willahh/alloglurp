(ns alloglurp.model.movie.movie-dao
  (:require [alloglurp.model.movie.movie-schema :as movie-schema]
            [korma.core :refer :all]
            [clojure.string :as str]))

(defn insert! [record]
  "Insert a record into database."
  (insert movie-schema/allo-movie
          (values record)))

(defn find-by-alloid [alloid]
  (first (select movie-schema/allo-movie
                 (where {:alloid alloid}))))

(defn enable-count []
  (count (select movie-schema/allo-movie)))

(defn find-list
  ([]
   (select movie-schema/allo-movie))

  ([p-offset p-limit]
   (select movie-schema/allo-movie
           (offset p-offset)
           (limit p-limit)))
  
  ([p-offset p-limit p-order p-asc]
   (select movie-schema/allo-movie
           (offset p-offset)
           (limit p-limit)
           (order p-order p-asc)))

  ([p-offset p-limit p-order p-asc p-korma-criteria]
   (let [a# p-korma-criteria
         limit p-limit
         offset p-offset
         sch `(korma.core/select* movie-schema/allo-movie)
         b# (conj a# `(offset ~p-offset) `(limit ~p-limit) sch `->)]
     (korma.core/exec (eval b#)))))