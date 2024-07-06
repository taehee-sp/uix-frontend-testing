(ns test.smply.domains.saas.saas-list.saas-list-test
  (:require [app.domains.saas.saas-list.saas-list-stories :refer [saas-list-stories]]
            [test.siheom.siheom :refer [checked? click! have-text-content?
                                        query render run-siheom]]
            [test.vitest.support :refer [describe it]]))


(describe
 "SaasList"
 (it "이름만 있는 SaaS"
     (run-siheom
      (render (get saas-list-stories "여러 개인 경우"))

      (checked? (query "radio" "전체 5"))

      (have-text-content?
       (query "listitem" #"")
       ["Notion2024년 6월 27일 결제"
        "Asana2024년 6월 20일 결제"
        "GitHub2024년 6월 15일 결제"
        "Zoom"
        "Slack"])

      (click! (query "radio" "결제 내역 있는 SaaS 3"))

      (have-text-content?
       (query "listitem" #"")
       ["Notion2024년 6월 27일 결제"
        "Asana2024년 6월 20일 결제"
        "GitHub2024년 6월 15일 결제"]))))