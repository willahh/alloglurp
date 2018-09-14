(ns alloglurp.db
  (:require [clojure.java.jdbc :as jdbc])
  (:use [korma.core]))

;; (defdb db (sqlite3 {:db "resources/databases/allocine.db"}))

;; Schema
(defentity glu-movie
  (table :glu_movie)
  (database glurps)
  (entity-fields :name :group_id :create_date :update_date :active :fav))

