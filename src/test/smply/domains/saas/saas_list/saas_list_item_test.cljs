(ns test.smply.domains.saas.saas-list.saas-list-item-test 
  (:require [app.domains.saas.saas-list.saas-list-item-stories :refer [saas-list-item-stories]]
            [test.siheom.siheom :refer [query render run-siheom visible?]]
            [test.vitest.support :refer [describe it]]))


(describe
 "SaasListItem"
 (it "이름만 있는 SaaS"
     (run-siheom
      (render (get saas-list-item-stories "name-only-saas"))
      (visible? (query "heading" "Zoom"))
      (visible? (query "text" #"결제") false)))
 
  (it "로고와 결제내역도 있는 SaaS"
     (run-siheom
      (render (get saas-list-item-stories "logo-payment-info-saas"))
      (visible? (query "heading" "Notion"))
      (visible? (query "text" "2024년 6월 27일 결제") true))))