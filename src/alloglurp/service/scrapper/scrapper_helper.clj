(ns alloglurp.service.scrapper.scrapper-helper
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

(defn cleanup [str]
  "Removes excess spaces at the beginning and end of the chain, as well as line
breaks"
  (if str (-> (clojure.string/replace str #"\n" "")
              (clojure.string/replace #" +$" "")
              (clojure.string/replace #"^ +" ""))
      ""))

(defn open-browser []
  "Instanciate a new browser."
  (set-driver! (init-driver {:webdriver (PhantomJSDriver. (DesiredCapabilities.))})))

(defn get-html-from-phantomjs [url]
  (do
    (to url)
    (html "body")))

(def get-html-from-phantomjs-memoize (memoize get-html-from-phantomjs))

