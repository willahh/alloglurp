(ns alloglurp.movie-schema
  (:require [clojure.java.jdbc :as jdbc]
            [alloglurp.db :as db]
            [wlh.helper.db-helper :as db-helper])
  (:use korma.db
        korma.core))

(defdb alloglurp db/db)
(def allo-movie-table-config
  (db-helper/get-table-config "allo_movie" [{:name "id" :type "integer" :null false :primary true}
                                            {:name "alloid" :type "integer"}
                                            {:name "createDate" :type "datetime" :null false}
                                            {:name "updateDate" :type "datetime" :null false}
                                            {:name "title" :type "text"}
                                            {:name "description" :type "text"}
                                            {:name "director" :type "text"}
                                            {:name "genre" :type "text"}
                                            {:name "imageUrl" :type "text"}
                                            {:name "pressEval" :type "text"}
                                            {:name "specEval" :type "text"}]))

;; (db-helper/drop-table db (:name allo-movie-table-config))
;; (db-helper/create-table db (:name allo-movie-table-config) (:jdbc-columns allo-movie-table-config))

(defentity allo-movie
  (table :allo_movie)
  (database alloglurp)
  (entity-fields :id :alloid :createDate :updateDate :createDate :title :description :genre :imageUrl :pressEval :specEval))





