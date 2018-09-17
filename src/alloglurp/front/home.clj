(ns alloglurp.front.home
  (:require [alloglurp.component.card :as card]
            [alloglurp.front.main :as main]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.process.movie.movie :as movie]
            [wlh.helper.string-helper :refer [ellipsis]]
            [clojure.string :as str]
            [alloglurp.service.session.session :refer [merge-params-session]]))

(defn map-movie-record-to-card-record [movie-record]
  {:id (:alloid movie-record)
   :title (:title movie-record)
   :description (ellipsis (:description movie-record) 10)
   :image (movie/get-image-path-from-alloid (:alloid movie-record))
   :meta (:genre movie-record)})

(defn- card-list-html [records]
  [:div {:class "ui stackable six column grid"}
   (let [html-records (map map-movie-record-to-card-record records)]
     (map (fn [html-record]
            [:div {:class "column"}
             (apply card/card-html (map #(second %) html-record))])
          html-records))])

(def genre '("Action" "Animation" "Aventure" "Biopic" "Comédie" "Comédie dramatique" "Comédie musicale" "Drame" "Epouvante-horreur" "Fantastique" "Guerre" "Historique" "Policier" "Péplum" "Romance" "Science fiction" "Thriller" "Western"))

(defn- filter-html []
  [:form.ui.form {:id "filter-fom"}
   [:script
    "function on_filter_change(event) {
         console.log('onchange', event);
         document.getElementById('filter-fom').submit();
     };"]
   [:div.field
    [:label
     "First Name"]
    [:div.ui.multiple.dropdown
     [:input
      {:name "genre", :type "hidden" :value "" :onchange "on_filter_change(event);"}]
     [:i.filter.icon]

     ;; [:a.ui.label.transition.visible
     ;;  {:data-value "Animation", :style "display: inline-block !important;"}
     ;;  [:div.ui.red.empty.circular.label]
     ;;  [:span
     ;;   "Animation"]
     ;;  [:i.delete.icon]]
     
     [:span.text
      "Filter Posts"]
     [:div.menu
      [:div.ui.icon.search.input
       [:i.search.icon]
       [:input
        {:placeholder "Search tags...", :type "text"}]]
      [:div.divider]
      [:div.header
       [:i.tags.icon]
       "\n      Tag Label\n    "]
      [:div.scrolling.menu
       (for [g genre]
         [:div.item {:data-value g}
          [:div.ui.red.empty.circular.label]
          [:span g]])
       ]]]]])

(defn debug-html [context session params page-params]
  [:div {:style "padding-top: 40px;"}
   [:div "context"
    [:div (pr-str context)]]
   [:div "params"
    [:div (pr-str params)]]
   [:div
    [:div "session"
     [:div (pr-str session)]]
    [:div "page-params"
     [:div (pr-str page-params)]]]])

(defn get-html [request]
  (let [{session :session params :params} request
        genre (:genre params)
        page-params (merge-params-session (:context request) params session)]
    (main/front-page-html-wrapper
     session params
     [:div {:style "padding-top: 20px;"}
      (debug-html (:context request) session params page-params)
      (filter-html)

      ;; [:div
      ;;  (for [genre '("Action" "Animation" "Aventure" "Biopic" "Comédie" "Comédie dramatique" "Comédie musicale" "Drame" "Epouvante-horreur" "Fantastique" "Guerre" "Historique" "Policier" "Péplum" "Romance" "Science fiction" "Thriller" "Western")]
      ;;    (do [:div [:h3 genre]
      ;;         (card-list-html (movie-dao/find-list 0 3 :alloid :ASC {:genre genre}))]))]
      
      (if genre
        [:div
         (card-list-html (movie-dao/find-list 0 3 :alloid :ASC {:genre genre}))]
        [:div
         (card-list-html (movie-dao/find-list 0 10 :alloid :ASC))])
      
      
      ])))


