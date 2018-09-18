(ns alloglurp.route
  (:require [alloglurp.api.api :as api]
            [alloglurp.front.home :as home]
            [alloglurp.service.session.session :refer [wrap-site-route]]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.session :refer [wrap-session]]))

(def site-routes
  (wrap-defaults
   (routes
    (context "/site/b/c" []
             (GET "/" request
                  (pr-str request)))
    
    (context "/site" []
             (GET "/" request
                  (pr-str request))

             (context "/test/list" []
                      (GET "/" request
                           (-> (home/get-html request)
                               (wrap-site-route request))))

             (context "/movie" []
                      (GET "/" request
                           (-> (home/get-html request)
                               (wrap-site-route request)))
                      (GET "/:alloid" request
                           (-> (home/get-html request)
                               (wrap-site-route request))))

             (GET "/test" request
                  (-> (home/get-html request)
                      (wrap-site-route request)))
             ))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defroutes main-route
  ;; (route/resources "/")
  site-routes
  api/api-routes
  
  (route/not-found "Not Found"))

(def app
  (-> (routes main-route)
      (wrap-session)))


