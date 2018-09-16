(ns alloglurp.component.card)

(defn card-html [id title description image meta]
  [:div {:class "ui card" :data-id (str id)}
   [:div {:class "image"}
    [:img {:src image :style "max-width: 120px;"}]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     meta]
    [:div {:class "description" :style "min-height: 52px;"}
     description]]])

;; (defn cards-html [records]
;;   [:div {:class "ui six link cards"}
;;    (for [record records]          
;;      (card-html (:image record) 
;;                 (:title record)
;;                 (:meta record)
;;                 (str (:id record))))])