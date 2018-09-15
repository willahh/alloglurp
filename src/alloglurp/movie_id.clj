(ns alloglurp.movie-id
  (:require [korma.core :refer :all]
            [alloglurp.db :refer :all]
            [wlh.helper.db-helper :as db-helper]))

(def allo-movie-id-table-config
  (db-helper/get-table-config "allo_movie_id"
                              [{:name "id" :type "integer" :null false :primary true}
                               {:name "alloid" :type "integer"}
                               {:name "movieStatusId" :type "integer"}
                               {:name "name" :type "text"}
                               {:name "url" :type "text"}]))

(defentity allo-movie-id
  (table :allo_movie_id)
  (database alloglurp)
  (entity-fields :id :alloid :movieStatusId :name :url))

(try (db-helper/create-table alloglurp.db/db
                             (:name allo-movie-id-table-config)
                             (:jdbc-columns allo-movie-id-table-config))
     (catch Exception e "Table already exist"))

(defn map-data [data]
  {:alloid (:id data)
   :name (:name data)
   :url (:url data)})

(defn insert! [record]
  "Insert a record into database."
  (insert allo-movie-id
          (values record)))

(defn insert-html-rows-to-data-base [movie-html-rows]
  (for [row movie-html-rows]
    (let [record (map-data row)]
      (insert! record))))

(defn insert-html-row-to-data-base [movie-html-row]
  (let [record (map-data movie-html-row)]
    (insert! record)))

(defn find-next-pending []
  (first (select allo-movie-id
                 (where {:movieStatusId 1}))))

(defn update-movie-status-id [alloid movieStatusId]
  (update allo-movie-id
          (set-fields {:movieStatusId movieStatusId})
          (where {:alloid [= alloid]})))

(defn get-pending-count []
  (count (select allo-movie-id
                 (where {:movieStatusId [= 1]}))))

