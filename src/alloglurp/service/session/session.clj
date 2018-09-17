(ns alloglurp.service.session.session
  (:require [clojure.string :as str]
            [ring.util.response :refer [content-type response]]))

(defn- prefixed-map [context params]
  ;; "Take a context and a params map and return a map of of params with prefixed key.
  ;; e.g.
  ;; (prefixed-map \"group/list\" {:query \"My query\" :total 100})
  ;; =>
  ;; {:group/list/query \"My query\" :group/list/total 100}"
  (let [param-vector (into [] params)]
    (letfn [(join-key [v-key-value]
              (let [key (try (keyword (str/join [context (str/replace-first (str (first v-key-value)) ":" "/")]))
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

;; (def req {:params {:query "My query" :custom "Custom" :type "Type 1"}
;;           :session {:page/list/limit 25 :page/list/total 10}
;;           :context "/site/test"
;;           :ssl-client-cert nil,:protocol "HTTP/1.1",:cookies {"ring-session" {:value "78af0d54-b482-473f-b33a-edcd44dfeb7a"}},:remote-addr "0:0:0:0:0:0:0:1",:flash nil,:route-params {},:headers {"cookie" "ring-session=78af0d54-b482-473f-b33a-edcd44dfeb7a", "accept" "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", "cache-control" "max-age=0", "upgrade-insecure-requests" "1", "user-agent" "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:62.0) Gecko/20100101 Firefox/62.0", "connection" "keep-alive", "host" "localhost:3000", "accept-language" "fr-FR,en-US;q=0.8,en;q=0.5,fr;q=0.3", "accept-encoding" "gzip, deflate"},:server-port 3000,:content-length nil,:form-params {},:query-params {},:content-type nil,:path-info "/",:character-encoding nil,:uri "/site",:server-name "localhost",:query-string nil,:multipart-params {},:scheme :http,:request-method :get})

;; (wrap-site-route (home/get-html req) req)

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
                [(keyword key) value])))
          ]
    (let [context-session (into {} (filter identity (map extract-context-from-key (into [] session))))]
      (merge context-session params))))

;; (merge-params-session "/site"
;;                       {:genre "Animation" :yolo 1}
;;                       {:site/jore "jre", :site/test/list/a "a",:site/genre "Animation", :site/test/list/b "b", :site/test/list/c "c"})

;; (merge-params-session "/site/test/list"
;;                       {:genre "Animation" :yolo 1}
;;                       {:site/genre "Animation", :site/test/list/a "a", :site/test/list/b "b", :site/test/list/c "c"})

;; (merge-params-session "/site"
;;                       {:genre "Ani" :yolo 1}
;;                       {})