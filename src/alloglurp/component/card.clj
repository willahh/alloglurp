(ns alloglurp.component.card
  (:require [clojure.string :as str]))

(defn card-html [context id title description image meta]
  [:a {:class "ui card" :data-id (str id) :href (str/join [context "/" id])}
   [:div {:class "image"}
    [:img {:src image }]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     meta]
    [:div {:class "description" :style "min-height: 52px;"}
     description]]])
