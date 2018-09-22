(defproject alloglurp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [enlive "1.1.6"]
                 [clj-webdriver/clj-webdriver "0.6.0"]
                 [com.github.detro.ghostdriver/phantomjsdriver "1.0.3"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [sqlitejdbc "0.5.6"]
                 [korma "0.4.3"]
                 [compojure "1.6.1"]
                 [ring "1.6.3"]
                 [cheshire "5.8.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-defaults "0.3.1"]
                 [clj-http "3.9.1"]
                 [hiccup "1.0.5"]
                 [cheshire "5.8.1"]]
  :plugins [[lein-ring "0.7.3"]]
  :ring {:handler alloglurp.route/app})

