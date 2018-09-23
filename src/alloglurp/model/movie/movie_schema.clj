(ns alloglurp.model.movie.movie-schema
  (:use [korma.core])
  (:require alloglurp.service.db.db
            [wlh.helper.db-helper :as db-helper]))

(def allo-movie-table-config
  (db-helper/get-table-config "allo_movie"
                              [{:name "id" :type "integer" :null false :primary true}
                               {:name "alloid" :type "integer"}
                               {:name "createDate" :type "datetime" :null false}
                               {:name "updateDate" :type "datetime" :null false}
                               {:name "title" :type "text"}
                               {:name "description" :type "text"}
                               {:name "director" :type "text"}
                               {:name "genre" :type "text"}
                               {:name "imageUrl" :type "text"}
                               {:name "thumb" :type "text"}
                               {:name "pressEval" :type "text"}
                               {:name "specEval" :type "text"}]))

;; (db-helper/drop-table db (:name allo-movie-table-config))
;; (db-helper/create-table db (:name allo-movie-table-config) (:jdbc-columns allo-movie-table-config))

(defentity allo-movie
  (table :allo_movie)
  (database alloglurp.service.db.db/alloglurp)
  (entity-fields :id :alloid :createDate :updateDate :createDate :title :description :genre :imageUrl :thumb :pressEval :specEval))

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