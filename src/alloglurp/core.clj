(ns alloglurp.core
  (:require [net.cgrand.enlive-html :as html])
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(defn cleanup [str]
  "Removes excess spaces at the beginning and end of the chain, as well as line
breaks"
  (-> (clojure.string/replace str #"\n" "")
      (clojure.string/replace #" +$" "")
      (clojure.string/replace #"^ +" "")))

(defn get-html-from-phantomjs [url]
  (do
    (set-driver! (init-driver {:webdriver (PhantomJSDriver. (DesiredCapabilities.))}))
    (to url)
    (html "body")))

(def get-html-from-phantomjs-memoize (memoize get-html-from-phantomjs))

(defn get-html-rows [url selector]
  (html/select
       (html/html-snippet (get-html-from-phantomjs-memoize url))
       selector))

(defn movie-id [week-movies i]
  (if-let [id (re-find #"/seance/film-(\d+)" (-> week-movies
                                                (nth i)
                                                (html/select [:.roller-item :a])
                                                first :attrs :href))]
  (nth id 1)
  "-"))

(defn movie-title [week-movies i]
  (-> week-movies
      (nth i)
      (html/select [:.thumbnail-link])
      first :attrs :title cleanup))

(defn movie-author [week-movies i]
  (-> week-movies
     (nth i)
     (html/select [:.roller-item :.meta-description])
     first :content first cleanup))

(defn movie-author [week-movies i]
  (-> week-movies
     (nth i)
     (html/select [:.roller-item :.meta-description])
     first :content first cleanup))

(defn movie-image-url [week-movies i]
  (-> week-movies
     (nth i)
     (html/select [:.roller-item :img])
     first :attrs :src))

(defn movie-row [week-movies i]
  [:movie-id (movie-id week-movies i)
   :title (movie-title week-movies i)
   :author (movie-author week-movies i)
   :thumb-url (movie-image-url week-movies i)])



;; Get formatted data
(def week-movies (get-html-rows "http://www.allocine.fr/" [:#roller-1 :.roller-item]))
(defn get-week-movies []
  (map #(movie-row week-movies %) (range (count week-movies))))

(def bill-movies (get-html-rows "http://www.allocine.fr/" [:#roller-2 :.roller-item]))
(defn bill-get-bill-movies []
  (map #(movie-row bill-movies %) (range (count bill-movies))))


;; Tests
(get-week-movies)
(bill-get-bill-movies)
