(ns alloglurp.route
  (:use compojure.core)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [alloglurp.front.home :as home]
            [alloglurp.api.api :as api]))

;; (use 'ring.middleware.resource
;;      'ring.middleware.defaults
;;      'ring.util.response
;;      'ring.middleware.session.cookie
;;      'ring.middleware.session
;;      'ring.middleware.flash
;;      'ring.middleware.content-type
;;      'ring.middleware.not-modified
;;      'ring.middleware.params
;;      'ring.middleware.keyword-params)

(def site-routes
  (wrap-defaults
   (routes
    
    (context "/site" []
             (GET "/" [params session] (home/get-html session params))
             (context "/movie" []
                      (GET "/" [] "aa")
                      (GET "/:alloid" [alloid] "bb"))))
   (assoc-in site-defaults [:security :anti-forgery] false)))

(defroutes main-route
  ;; (route/resources "/")
  site-routes
  api/api-routes
  (route/not-found "Not Found"))

(def app
  (routes main-route))


