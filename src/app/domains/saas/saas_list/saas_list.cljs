(ns app.domains.saas.saas-list.saas-list 
  (:require [app.domains.saas.saas-list.saas-list-item :refer [SaasListItem]]
            [uix.core :refer [$ defui use-state]]))

(defui SaasList [{:keys [saas-list]}]
  (let [[selected set-selected!] (use-state "all")
        saas-with-payment-list (filter :last-paid-at saas-list)
        result-list (->> (if (= selected "all")
                           saas-list
                           saas-with-payment-list)
                         (sort-by :last-paid-at)
                         (reverse))]
    ($ :div {}
       ($ :fieldset {}
          ($ :legend {} "필터")
          ($ :label {}
             (str "전체 " (count saas-list))
             ($ :input {:type "radio"
                        :value "all"
                        :checked (= selected "all")
                        :on-change #(set-selected! "all")}))
          ($ :label {}
             (str "결제 내역 있는 SaaS " (count saas-with-payment-list))
             ($ :input {:type "radio"
                        :value "with-payment"
                        :checked (= selected "with-payment")
                        :on-change #(set-selected! "with-payment")})))
       ($ :ul {}
          (for [saas result-list]
            ($ SaasListItem {:key (:id saas)
                             :saas saas}))))))