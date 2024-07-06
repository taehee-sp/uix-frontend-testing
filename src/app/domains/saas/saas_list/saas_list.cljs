(ns app.domains.saas.saas-list.saas-list 
  (:require [app.domains.saas.saas-list.saas-list-item :refer [SaasListItem]]
            [uix.core :refer [$ defui]]))

(defui SaasList [{:keys [saas-list]}]
(let [result-list (->> saas-list
                       (sort-by :last-paid-at)
                       (reverse))]
    ($ :ul {}
     (for [saas result-list]
       ($ SaasListItem {:key (:id saas)
                        :saas saas})))))