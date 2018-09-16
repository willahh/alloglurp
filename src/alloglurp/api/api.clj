(ns alloglurp.api.api
  (:use compojure.core)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [alloglurp.model.movie.movie-dao :as movie-dao]))

(defroutes app-route
  (GET "/api/" [] "Hello dude")
  (GET "/api/movie/" [] (movie-dao/find-list)))

(def app
  (-> (handler/site app-route)
      (wrap-json-response)))
