(ns wlh.helper.string-helper
  (:require [clojure.string :as str]))

(defn ellipsis [str cnt]
  (if (> (count str) cnt)
    (str/join " " (conj (into [] (take cnt (clojure.string/split
                                            str
                                            #" "))) "..."))
    str))
