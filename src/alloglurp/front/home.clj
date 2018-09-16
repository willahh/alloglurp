(ns alloglurp.front.home
  (:require [alloglurp.front.main :as main]
            [hiccup.core :as core]
            [alloglurp.process.movie.movie :as movie]
            [alloglurp.component.card :as card]
            [alloglurp.model.movie.movie-dao :as movie-dao]
            [clojure.string :as str]))

(defn ellipsis [str cnt]
  (if (> (count str) cnt)
    (str/join " " (conj (into [] (take cnt (clojure.string/split
                                            str
                                            #" "))) "..."))
    str))

(defn map-movie-record-to-card-record [movie-record]
  {:id (:alloid movie-record)
   :title (:title movie-record)
   :description (ellipsis (:description movie-record) 10)
   :image (movie/get-image-path-from-alloid (:alloid movie-record))
   :meta (:genre movie-record)})

(defn card-list [records]
  [:div {:class "ui stackable four column grid"}
   (let [html-records (map map-movie-record-to-card-record records)]
     (map (fn [html-record]
            [:div {:class "column"}
             (apply card/card-html (map #(second %) html-record))])
          html-records))])

(defn get-html [session params]
  (main/front-page-html-wrapper
   session params
   [:div {:style "padding-top: 20px;"}
    (for [genre '("Action" "Animation" "Aventure" "Biopic" "Comédie" "Comédie dramatique" "Comédie musicale" "Drame" "Epouvante-horreur" "Fantastique" "Guerre" "Historique" "Policier" "Péplum" "Romance" "Science fiction" "Thriller" "Western")]
      (do [:div [:h3 genre]
           (card-list (movie-dao/find-list 0 3 :alloid :ASC {:genre genre}))])) 
    [:div
     (pr-str params)]
    [:div
     (pr-str session)]]))

(sort (into #{} (map :genre (movie-dao/find-list))))
