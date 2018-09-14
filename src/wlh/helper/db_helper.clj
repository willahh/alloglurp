(ns wlh.helper.db-helper
  (:require [clojure.java.jdbc :as jdbc]))

(defn- merge-vector-of-map [v1 v2]
  "Merge two vectors of map.
  Note: this is bad implemented but i don't know how
  do better for now."
  (let [cnt (count v2)
        result (atom [])]
    (dotimes [i cnt]
      (let [m1 (when (< i (count v1)) (nth v1 i))
            m2 (nth v2 i)]
        (swap! result conj (merge m1 m2))))
    @result))

(defn- map-column-to-jdbc-column [col]
  (into []
        (flatten
         (map (fn [a]
                (let [k (first a)
                      v (first (rest a))]
                  (conj []
                        (case k
                          :name [(keyword v)]
                          :type [v]
                          :null (if-not v [:not :null])
                          :primary (when v [:primary :key])
                          :auto_increment (when v [:auto_increment])
                          :default (when v [:default v])
                          nil)))) col))))

(defn params-to-korma [params]
  (let [where-clause (list 'where {:fav "on"})
        order (when (:order params)
                (reverse (conj '()
                               'order
                               (keyword (:order params))
                               (keyword (if (= (:asc params) "1")
                                          :ASC :DESC)))))
        limit (when (:limit params)
                (reverse (conj () 'limit (:limit params))))
        offset (when (:offset params)
                 (reverse (conj () 'offset (:offset params))))]
    (reverse (remove #(= nil %) 
                     (conj '()
                           where-clause
                           order
                           limit
                           offset)))))

;; (defn create-table [db table]
;;   (let [name (:name table)
;;         columns (:jdbc-columns table)]
;;     (jdbc/db-do-commands db 
;;                          (clojure.string/lower-case 
;;                           (jdbc/create-table-ddl name columns
;;                                                  {:table-spec "ENGINE=InnoDB"
;;                                                   :entities clojure.string/upper-case})))))

(defn drop-table [db table]
  (jdbc/db-do-commands db (jdbc/drop-table-ddl table)))

(defn create-table [db table columns]
  (jdbc/execute! db (jdbc/create-table-ddl table columns)))

(defn get-table-config [name columns & {:keys [extend]}]
  (let [abstract-columns (when extend (:columns extend))
        abstract-jdbc-columns (when abstract-columns
                                (map map-column-to-jdbc-column abstract-columns))
        columns (merge-vector-of-map abstract-columns columns)
        target-jdbc-columns (map map-column-to-jdbc-column columns)        
        jdbc-columns (into []
                           (if abstract-jdbc-columns 
                             (apply merge abstract-jdbc-columns target-jdbc-columns)
                             target-jdbc-columns))] 
    
    {:name name
     :columns columns
     :jdbc-columns jdbc-columns
     :fields (into [] (map #(first %) jdbc-columns))}))


