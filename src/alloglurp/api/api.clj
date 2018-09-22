(ns alloglurp.api.api
  (:use compojure.core)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [clojure.string :as str]
            [cheshire.core :refer :all]))

;;   (route/not-found "Not Found"))
(defn query-string-to-params [query-string]
  (if query-string
    (->> (str/split query-string #"&") 
         (map #(str/split % #"=")) 
         (map (fn [[k v]] [(keyword k) v])) 
         (into {}))
    {}))

(defroutes app-routes
  (context "/api" []
           (context "/movie" []
                    (GET "/" request
                         (let [session (:session request)
                               params (query-string-to-params (:query-string request))
                               
                               offset (Integer. (or (:offset params) 1))
                               limit (Integer. (or (:limit params) 10))
                               order :alloid
                               asc :ASC
                               
                               count (movie-dao/enable-count)
                               records (doall (movie-dao/find-list
                                               offset limit order asc))]
                           (generate-string {:count count
                                             :records records}))))))

(def api-routes
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-response)))





