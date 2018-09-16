(ns alloglurp.front.home
  (:require [alloglurp.front.main :as main]
            [hiccup.core :as core]))

(defn get-html [session params]
  (main/front-page-html-wrapper
   session params
   [:div
    [:div "html"]
    [:div
     (pr-str params)]
    [:div
     (pr-str session)]]))