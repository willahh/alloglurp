(ns alloglurp.allo-scrapper.browse-movies
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [alloglurp.movie-id]
            [alloglurp.allo-scrapper.movie]
            [alloglurp.movie-schema]
            [alloglurp.movie-status-constant]
            [alloglurp.movie-dao])
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]]
        [alloglurp.allo-scrapper.scrapper-helper]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)


;; --------------- Browse through best movies pages
(def *current-pagination-number* (ref 0))

(defn- get-page-url []
  (if (= @*current-pagination-number* 0)
    "http://www.allocine.fr/film/meilleurs/"
    (str/join ["http://www.allocine.fr/" "film/meilleurs/" "?page=" @*current-pagination-number*])))

(defn- get-next-page-url []
  (let [url (get-page-url)]
    (do (dosync (ref-set *current-pagination-number* (inc @*current-pagination-number*)))
        url)))

(defn- map-data [data]
  {:name (-> data :content first cleanup)
   :id (second (re-find #"/film/fichefilm_gen_cfilm=(\d+).html" (-> data :attrs :href)))
   :url (-> data :attrs :href)})

(defn get-movie-row [html-data]
  (let [data (-> html-data
                 (html/select [:.card-entity :.meta-title :a]))]
   (map map-data data)))

;; Proceed data
(defn get-next-html-movie-row-and-insert-to-database []
  (let [movie-rows (get-movie-row (html/html-snippet
                                   (get-html-from-phantomjs-memoize
                                    (get-next-page-url))))]
    (alloglurp.movie-id/insert-html-rows-to-data-base movie-rows)))

(defn find-next-pending-movie-retrieve-content-and-insert []
  (let [random-sleep-ms (+ (rand-int (- 7001 1000)) 1500)
        movie-id-record (alloglurp.movie-id/find-next-pending)
        alloid (:alloid movie-id-record)]
    (do
      (alloglurp.movie-id/update-movie-status-id alloid alloglurp.movie-status-constant/in-progress)
      (Thread/sleep random-sleep-ms)
      (when movie-id-record
        (do
          (let [movie-html-row (alloglurp.allo-scrapper.movie/get-movie-detail alloid)
                movie-record (alloglurp.movie-schema/map-allo-data-to-record movie-html-row)]
            (alloglurp.movie-id/update-movie-status-id alloid alloglurp.movie-status-constant/insert)
            (alloglurp.movie-dao/insert! movie-record)
            (alloglurp.movie-id/update-movie-status-id alloid alloglurp.movie-status-constant/finish)))))))

(defn proceed-by-10 []
  "Proceed item by "
  (do
    (println "proceed-by-10")
    (dotimes [i 10]
      (find-next-pending-movie-retrieve-content-and-insert))
    "Finish"))

;; Wait 5 minutes then proceed 10 items (with random sleep intervals)
;; (when (> (alloglurp.movie-id/get-pending-count) 0)
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (Thread/sleep (* 1000 60 5))
;;   (proceed-by-10)
;;   (println "end"))



