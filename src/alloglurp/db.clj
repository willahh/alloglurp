(ns alloglurp.db
  (:require [korma.db :refer :all]))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "resources/databases/allocine.db"})

(defdb alloglurp db)
