(ns alloglurp.db
  (:require [clojure.java.jdbc :as jdbc]
            [wlh.helper.db-helper :as db-helper])
  (:use korma.db
        korma.core))

(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname "resources/databases/allocine.db"})
