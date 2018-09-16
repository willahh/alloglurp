(ns alloglurp.component.card)

(defn card-html [id title description image meta]
  [:div {:class "ui card" :data-id (str id)}
   [:div {:class "image"}
    [:img {:src image }]]
   [:div {:class "content"}
    [:div {:class "header"}
     title]
    [:div {:class "meta"}
     meta]
    [:div {:class "description" :style "min-height: 52px;"}
     description]]])
