(ns alloglurp.movie-dao
  (:require [clojure.java.jdbc :as jdbc]
            [alloglurp.db :as db]
            [alloglurp.movie-schema :as movie-schema]
            [wlh.helper.db-helper :as db-helper])
  (:use korma.db
        korma.core))

(defn insert! [record]
  "Insert a record into database."
  (insert movie-schema/allo-movie
          (values record)))
