(ns alloglurp.api.api
  (:use compojure.core)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [alloglurp.model.movie.movie-dao :as movie-dao]))

;; (defroutes api-route


;;   (route/not-found "Not Found"))

(def api-routes
  (wrap-json-response
   (context "/api" []
            (GET "/" [] "/api")
            (context "/movie" []
                     (GET "/" [] (movie-dao/find-list))
                     (GET "/:alloid" [alloid] (movie-dao/find-by-alloid alloid))))))
;; (response-dao/find-by-alloid 4966)
