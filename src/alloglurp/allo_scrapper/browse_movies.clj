(ns alloglurp.allo-scrapper.browse-movies
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str])
  (:use [clj-webdriver.taxi]
        [clj-webdriver.driver :only [init-driver]]))

(import 'org.openqa.selenium.phantomjs.PhantomJSDriver
        'org.openqa.selenium.remote.DesiredCapabilities)

