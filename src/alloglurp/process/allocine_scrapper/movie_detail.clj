(ns alloglurp.process.allocine-scrapper.movie-detail 
  (:require [alloglurp.service.scrapper.scrapper-helper :refer :all]
            [clj-webdriver.driver :refer :all]
            [clj-webdriver.taxi :refer :all]
            [clojure.string :as str]
            [net.cgrand.enlive-html :as html]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(open-browser)

(defn- get-html-rows [url selector]
  (html/select
   (html/html-snippet (get-html-from-phantomjs-memoize url))
   selector))

;; ----------------- movie
(defn- movie-id [week-movies i]
  (if-let [id (re-find #"/seance/film-(\d+)"
                       (-> week-movies
                           (nth i)
                           (html/select [:.roller-item :a])
                           first :attrs :href))]
    (nth id 1)
    "-"))

(defn- movie-title [week-movies i]
  (-> week-movies
      (nth i)
      (html/select [:.thumbnail-link])
      first :attrs :title cleanup))

(defn- movie-author [week-movies i]
  (-> week-movies
      (nth i)
      (html/select [:.roller-item :.meta-description])
      first :content first cleanup))

(defn- movie-image-url [week-movies i]
  (-> week-movies
      (nth i)
      (html/select [:.roller-item :img])
      first :attrs :src))

(defn- movie-row [week-movies i]
  [:movie-id (movie-id week-movies i)
   :title (movie-title week-movies i)
   :author (movie-author week-movies i)
   :thumb-url (movie-image-url week-movies i)])


;; ----------------- Formated data
(defn get-week-movies []
  (def week-movies (get-html-rows "http://www.allocine.fr/" [:#roller-1 :.roller-item]))
  (map #(movie-row week-movies %) (range (count week-movies))))

(defn get-bill-movies []
  (def bill-movies (get-html-rows "http://www.allocine.fr/" [:#roller-2 :.roller-item]))
  (map #(movie-row bill-movies %) (range (count bill-movies))))


;; ----------------- Incoming
(def incoming-html-rows (get-html-rows "http://www.allocine.fr/" [:.list-movie-hp :.list-entity-item :a.list-entity-item-link]))
(defn- incoming-id [html-rows i]
  (if-let [id (re-find #"/film/fichefilm_gen_cfilm=(\d+).html"
                       (-> html-rows
                           (nth i)
                           :attrs :href))]
    (second id)
    "-"))

(defn- incoming-title [html-rows i]
  (-> html-rows
      (nth i)
      :content first cleanup))

(defn- incoming-url [html-rows i]
  (-> html-rows
      (nth i)
      :attrs :href))

(defn- incoming-row [html-rows i]
  [:movie-id (incoming-id html-rows i)
   :movie-title (incoming-title html-rows i)
   :movie-url (incoming-url html-rows i)])

(defn get-incoming []
  (map #(incoming-row incoming-html-rows %) (range (count incoming-html-rows))))



;; ----------------- Top week series
(def top-week-series (get-html-rows
                      "http://www.allocine.fr/"
                      [:.mdl-inside :.row :.card]))

(defn- top-week-series-id [html-rows i]
  (when-let [id (re-find #"/series/ficheserie_gen_cserie=(\d+).html"
                         (-> top-week-series
                             (nth 0)
                             (html/select [:.meta :a])
                             first :attrs :href))]
    (second id)))

(defn- top-week-series-title [html-rows i]
  (-> html-rows
      (nth i)
      (html/select [:.meta :a])
      first :attrs :title cleanup))

(defn- top-week-series-href [html-rows i]
  (-> html-rows
      (nth i)
      (html/select [:.meta :a])
      first :attrs :href))

(defn- top-week-series-description [html-rows i]
  (-> html-rows
      (nth i)
      (html/select [:.meta-description])
      first :content first cleanup))

(defn- top-week-series-thumb-url [html-rows i]
  (-> html-rows
      (nth i)
      (html/select [:img])
      first :attrs :data-src cleanup))

(defn- top-week-series-row [html-rows i]
  [:movie-id (top-week-series-id html-rows i)
   :title (top-week-series-title html-rows i)
   :url (top-week-series-href html-rows i)
   :description (top-week-series-description html-rows i)
   :thumb-url (top-week-series-thumb-url html-rows i)])

(defn get-top-week-series []
  (map #(top-week-series-row top-week-series %) (range (count top-week-series))))








;; --------------- Movie detail
(def movie-detail (html/html-snippet
                   (get-html-from-phantomjs-memoize
                    "http://www.allocine.fr/film/fichefilm_gen_cfilm=253927.html")))

(defn- movie-actors [movie-detail]
  (into [] (drop-last 2 (rest (filter #(string? %)
                                      (map #(first (:content %))
                                           (-> movie-detail
                                               (html/select [:.meta-body-item :a]))))))))

(defn- movie-genre [movie-detail]
  (-> movie-detail
      (html/select [:.meta-body-item (html/attr= :itemprop "genre")])
      first :content first cleanup))


;; (take)
;; (open-browser)
;; (let [url (str/join ["http://www.allocine.fr/film/fichefilm_gen_cfilm=" 235589 ".html"])
;;       ;; (html/html-snippet (get-html-from-phantomjs ))

;;       ]
;;   (to "http://www.google.com")
;;   (take-screenshot :file "a.png")
;;   url)

(defn get-movie-detail [movie-id]
  (let [url (str/join ["http://www.allocine.fr/film/fichefilm_gen_cfilm=" movie-id ".html"])
        movie-detail (html/html-snippet (get-html-from-phantomjs-memoize url))]
    {:movie-id movie-id
     :movie-name (try (-> movie-detail
                          (html/select [:.section-wrap :.titlebar-title])
                          first :content first cleanup)
                      (catch Exception e "-"))
     :release-date (try (-> movie-detail
                            (html/select [:.card-movie-overview :a.date])
                            first
                            :content first)
                        (catch Exception e "-"))
     :actors (movie-actors movie-detail)
     :genre (movie-genre movie-detail)
     :nationality (try (-> movie-detail
                           (html/select [:.card-movie-overview :.nationality])
                           first :content first cleanup)
                       (catch Exception e "-"))
     :press-eval (try (-> movie-detail
                          (html/select [:.stareval-note])
                          first :content first cleanup)
                      (catch Exception e "-"))
     :spectator-eval (try (-> movie-detail
                              (html/select [:.stareval-note])
                              (nth 1):content first cleanup)
                          (catch Exception e "-"))
     :friend-eval (try
                    (-> movie-detail
                        (html/select [:.stareval-note])
                        (nth 2):content first cleanup)
                    (catch Exception e "-"))
     :synopsis (try (-> movie-detail
                        (html/select [:.content-txt])
                        first :content first cleanup)
                    (catch Exception e "-"))
     :thumb-url (try (-> movie-detail
                         (html/select [:.card-movie-overview :.thumbnail-img])
                         first :attrs :src)
                     (catch Exception e "-"))}))



;; --------------- Search movie
(def movie-search (html/html-snippet
                   (get-html-from-phantomjs-memoize
                    "http://www.allocine.fr/recherche/?q=las+vegas+parano")))

(defn find-movie-id-from-query [query]
  (let [url (str/join ["http://www.allocine.fr/recherche/?q=" (str/replace query " " "+")])
        movie (html/html-snippet
               (get-html-from-phantomjs-memoize url))
        found-url (-> 
                   (filter (fn [m]
                             (and (:href m)
                                  (re-find #"/film/fichefilm_gen_cfilm=(\w+).html" (:href m))))
                           (let [links (map #(:attrs %) (-> movie
                                                            (html/select [:.rubric :.totalwidth :a])))]
                             links))
                   distinct first :href)]
    (when-let [id (re-find #"/film/fichefilm_gen_cfilm=(\d+).html" found-url)]
      (second id))))




