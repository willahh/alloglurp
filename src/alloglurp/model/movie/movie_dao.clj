(ns alloglurp.model.movie.movie-dao
  (:require [alloglurp.model.movie.movie-schema :as movie-schema]
            [korma.core :refer :all]))

(defn insert! [record]
  "Insert a record into database."
  (insert movie-schema/allo-movie
          (values record)))

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

  ([p-offset p-limit p-order p-asc p-where]
   (select movie-schema/allo-movie
           (offset p-offset)
           (limit p-limit)
           (order p-order p-asc)
           (where p-where))))

;; Some test
;; (find-list)
;; (find-list 1 3)
;; (find-list 1 3 :alloid :ASC)
;; (find-list 1 3 :alloid :ASC)
;; (find-list 1 3 :alloid :ASC {:genre "Aventure"})
;; (find-list -1 -1 :alloid :ASC {:genre "Aventure"})
;; (find-list -1 -1 :alloid :ASC)
(find-list -1 1 :alloid :ASC {:genre "Aventure"})

(defn find-by-alloid [alloid]
  (first (select movie-schema/allo-movie
                 (where {:alloid alloid}))))


(select movie-schema/allo-movie
        (limit 3)
        (offset 3))

