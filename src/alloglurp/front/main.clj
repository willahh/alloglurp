(ns alloglurp.front.main
  (:require [hiccup.core :as core]))

(def nav [{:label "Home" :href "/"}
          {:label "Week" :info "Get last week movies" :href "/week"}
          {:label "Actors" :info "Show actors" :href "/actor"}
          {:label "Admin" :href "/admin" :info "Administration"}])

(defn head []
  [:head
   [:title "Alloglurp!"]
   [:link {:type "text/css", :href "/lib/material_icon.css" :rel "stylesheet"}]
   [:script {:type "text/javascript", :src "/lib/jquery-3.1.1.min.js"}]
   [:script {:type "text/javascript", :src "/semantic/dist/semantic.min.js"}]
   [:script {:type "text/javascript", :src "/js/main.js"}]
   [:link {:type "text/css", :href "/semantic/dist/semantic.min.css" :rel "stylesheet"}]
   [:link {:type "text/css", :href "/css/styles.css" :rel "stylesheet"}]])

(defn nav-html [nav]
  [:div {:class "ui large secondary inverted pointing menu"}
   [:a {:class "toc item"} "<i class=\"sidebar icon\"></i>"]
   (map (fn [row]
          [:a {:class "item" :href (:href row)} (:label row)]) nav)])

(defn header-html []
  [:div
   [:div.ui {:class "ui inverted menu"}
    [:div {:class "header item"} "Glurps!"]
    [:div {:class "ui container"}
     (nav-html nav)
     [:div {:class "right menu"}
      [:div {:class "item segment"}
       [:a {:class "ui inverted button"} "Log in"]
       [:a {:class "ui button primary"} "Sign up"]
       ]]]]])

(defn breadcrumb-html []
  [:div {:class "ui large breadcrumb"}
   [:a.section "Home"]
   [:i {:class "right chevron icon divider"}]
   [:a.section "Actor"]
   [:i {:class "right chevron icon divider"}]
   [:a.section "List"]])

(defn front-page-html-wrapper [session params html]
  "Html wrapper for all admin pages."
  (str "<!DOCTYPE html>\n"
       (core/html
        [:html
         (head)
         [:body
          [:div (header-html)]
          [:div {:class "ui container"}
           ;; (breadcrumb-html)
           html]]])))
