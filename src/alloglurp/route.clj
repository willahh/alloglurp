(ns alloglurp.route
  (:require [alloglurp.api.api :as api]
            [alloglurp.front.home :as home]
            [alloglurp.front.movie.detail :as movie-detail]
            [alloglurp.service.session.session :refer [wrap-site-route]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.session :refer [wrap-session]]))

(def site-routes
  (wrap-defaults
   (routes
    (context "/site" []
             (context "/movie" []
                      (GET "/" request
                           (-> (home/get-html request)
                               ))
                      (GET "/:alloid" request
                           (-> (movie-detail/get-html request)
                               (wrap-site-route request))))
             ))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defroutes main-route
  site-routes
  api/api-routes
  (route/not-found "Not Found"))

(def app
  (-> (routes main-route)
      (wrap-session)))


