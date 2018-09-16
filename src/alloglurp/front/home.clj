(ns alloglurp.front.home
  (:require [alloglurp.front.main :as main]
            [hiccup.core :as core]
            [alloglurp.process.movie.movie :as movie]
            [alloglurp.component.card :as card]
            [alloglurp.model.movie.movie-dao :as movie-dao]))

(defn map-movie-record-to-card-record [movie-record]
  "Key order is important here."
  {:image (movie/get-image-path-from-alloid (:alloid movie-record))
   :title (:title movie-record)
   :meta (:description movie-record)
   :id (:alloid movie-record)})

(defn get-html [session params]
  (main/front-page-html-wrapper
   session params
   [:div
    [:div "html"]
    (let [records (take 5 (movie-dao/find-list))
          html-records (map map-movie-record-to-card-record records)]
      (map (fn [html-record]
             (card/card-html (:image html-record) (:title html-record) (:meta html-record) (:id html-record)))
           html-records))
    [:div
     (pr-str params)]
    [:div
     (pr-str session)]]))
