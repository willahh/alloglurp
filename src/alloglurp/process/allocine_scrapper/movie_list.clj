(ns alloglurp.process.allocine-scrapper.movie-list
  (:require [alloglurp.model.movie.movie-dao :as movie-dao]
            [alloglurp.model.movie.movie-schema :as movie-schema]
            [alloglurp.model.movieid.movieid-dao :as movieid-dao]
            [alloglurp.model.movieid.movieid-status-constant :as movieid-status-constant]
            [alloglurp.process.allocine-scrapper.movie-detail :as movie-detail]
            [alloglurp.service.scrapper.scrapper-helper :as scrapper-helper]
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

(defn- get-next-page-url []
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

;; Proceed data
(defn get-next-html-movie-row-and-insert-to-database []
  (let [movie-rows (get-movie-row (html/html-snippet
                                   (scrapper-helper/get-html-from-phantomjs-memoize
                                    (get-next-page-url))))]
    (alloglurp.model.movieid.movieid-dao/insert-html-rows-to-data-base movie-rows)))

(defn find-next-pending-movie-retrieve-content-and-insert []
  (let [random-sleep-ms (+ (rand-int (- 7001 1000)) 1500)
        movie-id-record (alloglurp.model.movieid.movieid-dao/find-next-pending)
        alloid (:alloid movie-id-record)]
    (do
      (movieid-dao/update-movie-status-id alloid movieid-status-constant/in-progress)
      (Thread/sleep random-sleep-ms)
      (when movie-id-record
        (do
          (let [movie-html-row (movie-detail/get-movie-detail alloid)
                movie-record (movie-schema/map-allo-data-to-record movie-html-row)]
            (movieid-dao/update-movie-status-id alloid movieid-status-constant/insert)
            (movie-dao/insert! movie-record)
            (movieid-dao/update-movie-status-id alloid movieid-status-constant/finish)))))))

(defn proceed-by-10 []
  "Proceed item by "
  (do
    (println "proceed-by-10")
    (dotimes [i 10]
      (find-next-pending-movie-retrieve-content-and-insert))
    "Finish"))
