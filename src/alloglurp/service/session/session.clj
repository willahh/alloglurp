(ns alloglurp.service.session.session
  (:require [clojure.string :as str]
            [ring.util.response :refer [content-type response]]))

(defn- prefixed-map [request-context params]
  ;; "Take a request context and a params map and return a map of of params with prefixed key.
  ;; e.g.
  ;; (prefixed-map \"group/list\" {:query \"My query\" :total 100})
  ;; =>
  ;; {:group/list/query \"My query\" :group/list/total 100}"
  (let [param-vector (into [] params)]
    (letfn [(join-key [v-key-value]
              (let [key (try (keyword (str/join [request-context (str/replace-first (str (first v-key-value)) ":" "/")]))
                             (catch Exception e "-"))
                    value (second v-key-value)]
                {key value}))]
      (apply merge (map join-key param-vector)))))

(defn- merge-param-and-session-with-prefixed-key [request]
  (let [{session :session
         params :params
         context :context} request
        prefix (str/replace-first context #"/" "")]
    (apply merge [(prefixed-map prefix params)
                  session])))

(defn wrap-site-route [handler request]
  "Wrape a route with a text/html content and session."
  (let [{session :session
         params :params
         context :context} request]
    (let [new-session (merge-param-and-session-with-prefixed-key request)]
      (-> (response handler)
          (content-type "text/html")
          (assoc :session new-session)))))

(defn merge-params-session [context params session]
  (letfn [(splitted [splitted-str]
            (if (> (count splitted-str) 1)
              (butlast splitted-str)
              splitted-str))

          (extract-context-from-key [kv-map]
            (let [s (str/split (str (first kv-map)) #"/")
                  key (last s)
                  ctx (splitted s)
                  kv-context (str/replace-first (str/join "/" ctx) ":" "/")

                  value (second kv-map)]
              (when (= kv-context context)
                [(keyword key) value])))]
    (let [context-session (into {} (filter identity (map extract-context-from-key (into [] session))))]
      (merge context-session params))))