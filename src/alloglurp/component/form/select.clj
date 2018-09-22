(ns alloglurp.component.form.select)

(defn select-html [field-name selected-value options & conf]
  (let [selected-option (into {} (filter #(= (:value %) selected-value) options))]
    [:div {:class "ui selection dropdown" :style "min-width: 1em;"}
     [:input {:type "hidden" :name field-name :value (:value selected-option)}]
     [:i {:class "dropdown icon"}]
     [:div {:class "text"} (:label selected-option)]
     [:div {:class "menu" :style "min-width: 20em;"}
      (for [option options]
        [:div (conj {:class "item" :data-value (:name option) :data-name (:name option)}
                    ;; (when (= (:name option) (:value option))
                    ;;   {:selected "true"})
                    (when (:on-click option) 
                      {:onclick (:on-click option)}))
         (when (:icon option) 
           (:icon option))
         (:label option)
         ])]]))