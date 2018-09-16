(ns alloglurp.component.card)

(defn card-html [image title meta id]
  [:div {:class "card" :data-id (str id)}
   [:div {:class "image"}
    [:img {:src image :style "max-width: 120px;"}]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     meta]]])

(defn cards-html [records]
  [:div {:class "ui six link cards"}
   (for [record records]          
     (card-html (:image record) 
                (:title record)
                (:meta record)
                (str (:id record))))])