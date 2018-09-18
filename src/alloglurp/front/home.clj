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
   :image (str/join ["../" (movie/get-image-path-from-alloid (:alloid movie-record))])
   :meta (:genre movie-record)})

(defn- card-list-html [records]
  [:div {:class "ui stackable six column grid"}
   (let [html-records (map map-movie-record-to-card-record records)]
     (map (fn [html-record]
            [:div {:class "column"}
             (apply card/card-html (map #(second %) html-record))])
          html-records))])

(defn- filter-html [page-params]
  [:form.ui.form {:id "filter-fom"}
   [:script
    "function on_filter_change(event) {
         console.log('onchange', event);
         //document.querySelector('input[name="genre"]').value;
         document.getElementById('filter-fom').submit();
     };"]
   [:div.field
    [:label
     "First Name"]
    [:div.ui.multiple.dropdown
     [:input
      {:name "genre", :type "hidden" :value (:genre page-params) :onchange "on_filter_change(event);"}]
     [:i.filter.icon]

     (let [genre-list (str/split (:genre page-params) #",")]
       (for [genre genre-list]
         [:a.ui.label.transition.visible
          {:data-value "Animation", :style "display: inline-block !important;"}
          [:div.ui.red.empty.circular.label]
          [:span
           genre]
          [:i.delete.icon]]))
     
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



;; TODO wip
(defn get-pagination-offset [page limit count]
  "Get pagination offset from page number, limit and table rows count."
  (* (- page 1) limit))

(defn pagination-html [path page offset limit total]
  (when (> total limit)
    (let [page-count (int (Math/ceil (float (/ total limit))))
          visible-count 3
          start-list (max 1 (min (int (- page-count 3)) (max 1 (- page 1))))
          end-list (max (+ visible-count 1) (min page-count (min (+ page (- visible-count 1)) (+ page page-count))))]
      [:div.ui.pagination.menu.tiny {:style "text-align: center;"}
       (when (> page (- visible-count 1)) 
         [:a {:class "item" :href (str path "?page=" 1)} 1])
       (when (> page visible-count)
         [:span {:class "item" } "..."])
       (for [i (range start-list end-list)]
         (let [curr i]
           [:a {:class (str "item" (when (= page curr) " active")) :href (str path "?page=" curr)} curr]))
       (when (< page (+ 1 (- page-count visible-count))) 
         [:span {:class "item" } "..."])
       [:a {:class (str "item" (when (= page end-list) " active")) :href (str path "?page=" page-count)} page-count]])))










(defn get-html [request]
  (let [{session :session params :params} request
        genre (:genre params)
        page-params (merge-params-session (:context request) params session)]
    (main/front-page-html-wrapper
     session params
     [:div {:style "padding-top: 20px;"}
      (debug-html (:context request) session params page-params)
      (filter-html page-params)
      
      (let [path ""
            page 1
            limit 10
            count 100
            offset (get-pagination-offset page limit count)]
        (pagination-html path page offset limit count))
      
      (if genre
        [:div
         (card-list-html (movie-dao/find-list 0 3 :alloid :ASC {:genre genre}))]
        [:div
         (card-list-html (movie-dao/find-list 0 10 :alloid :ASC))])])))


