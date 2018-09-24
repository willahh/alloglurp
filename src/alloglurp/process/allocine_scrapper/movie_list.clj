(ns alloglurp.process.allocine-scrapper.movie-list
  (:require [alloglurp.service.scrapper.scrapper-helper :as scrapper-helper]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

;; --------------- Browse through best movies pages
(def *current-pagination-number* (ref 0))

(defn- get-page-url []
  (if (= @*current-pagination-number* 0)
    "http://www.allocine.fr/film/meilleurs/"
    (str/join ["http://www.allocine.fr/" "film/meilleurs/" "?page=" @*current-pagination-number*])))

(defn get-next-page-url []
  (let [url (get-page-url)]
    (do (dosync (ref-set *current-pagination-number* (inc @*current-pagination-number*)))
        url)))

(defn- map-data [data]
  {:name (-> data :content first scrapper-helper/cleanup)
   :id (second (re-find #"/film/fichefilm_gen_cfilm=(\d+).html" (-> data :attrs :href)))
   :url (-> data :attrs :href)})

(defn get-movie-row [html-data]
  (let [data (-> html-data
                 (html/select [:.card-entity :.meta-title :a]))]
    (map map-data data)))



