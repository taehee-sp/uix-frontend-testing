(ns app.domains.saas.saas-list.saas-list-item 
  (:require [uix.core :refer [$ defui]]))

(defn format-date-year-month-day [date]
  (let [year (.getFullYear date)
        month (inc (.getMonth date))
        day (.getDate date)]
    (str year "년 " month "월 " day "일")))

(defui SaasListItem [{:keys [saas]}]
  ($ :li {}
     ($ :h3 {} (:name saas))
     (when (:last-paid-at saas)
       ($ :p {} (str (format-date-year-month-day (:last-paid-at saas)) " 결제")))))