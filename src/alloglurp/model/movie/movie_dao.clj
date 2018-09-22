(ns alloglurp.model.movie.movie-dao
  (:require [alloglurp.model.movie.movie-schema :as movie-schema]
            [alloglurp.service.session.session :refer [merge-params-session]]
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





(defn query-string-to-params [query-string]
  (if query-string
    (->> (str/split query-string #"&") 
         (map #(str/split % #"=")) 
         (map (fn [[k v]] [(keyword k) v])) 
         (into {}))
    {}))

(defn find-list-for-home [session params]
  (let [;; Parameters
        offset (Integer. (or (:offset params) 1))
        limit (Integer. (or (:limit params) 10))
        order :alloid
        asc :ASC
        genre (:genre params)]
    
    (def criteria-list `())
    (when (and genre (not= genre ""))
      (def genre-list (str/split genre #","))
      (def criteria-list `((korma.core/where (~'or ~@(map (fn [m]
                                                            {:genre m}) genre-list))))))
    ;; (korma.core/where (~'and {:pressEval "3,9"}))
    (let [total (enable-count)
          records (find-list offset limit order asc criteria-list)]
      {:total total
       :count (count records)
       :offset offset
       :limit limit
       :records records})))



(find-list-for-home {} {:offset 1 :limit 2 :genre "Action,Aventure"})
(find-list-for-home {} {:offset 1 :limit -1 :genre "Aventure"})
(find-list-for-home {} {:offset 1 :limit -1 :genre "Animation"})
(find-list-for-home {} {:offset 1 :limit 2 :genre "Animation"})
(find-list-for-home {} {:offset 2 :limit 2 :genre "Animation"})
(find-list-for-home {} {:offset 1 :limit -1})




(def aa "")
(def aa "b")
aa