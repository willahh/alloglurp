(ns alloglurp.api.api
  (:use compojure.core)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.process.movie.movie :as movie]
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
           (context "/scrap" []
                    (GET "/" [query]
                         (movie/find-movie-from-alloid-fetch-content-and-insert (movie/find-movie-id-from-query query))))
           (context "/movie" []
                    (GET "/" request
                         (let [session (:session request)
                               params (query-string-to-params (:query-string request))]
                           (generate-string (movie-dao/find-list-for-home session params)))))))

(def api-routes
  (-> (handler/api app-routes)
      (wrap-json-body)
      (wrap-json-response)))





