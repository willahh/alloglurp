(ns alloglurp.front.movie.detail
  (:require [clojure.string :as str]
            [alloglurp.component.card :as card]
            [alloglurp.process.movie.movie :as movie]
            [alloglurp.front.main :as main]
            [alloglurp.model.movie.movie-dao :as movie-dao]))

(defn get-thumb-path [movie-record]
  (str/join ["../../" (movie/get-image-path-from-alloid (:alloid movie-record))]))

(defn get-html [request]
  (let [session (:session request)
        params (:params request)
        context (:context request)]
    (let [alloid (:alloid params)
          movie-record (movie-dao/find-by-alloid alloid)]
      (-> [:div {:style "padding-top: 20px;"}

           (card/card-html (:context request)
                           (:alloid movie-record)
                           (:title movie-record)
                           (:description movie-record)
                           (get-thumb-path movie-record)
                           (:genre movie-record))]
          (main/wrap-page-html request)))))
