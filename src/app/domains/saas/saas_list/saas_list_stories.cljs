(ns app.domains.saas.saas-list.saas-list-stories 
  (:require [app.domains.saas.saas-list.saas-list :refer [SaasList]]
            [app.domains.saas.saas-list.saas-list-item-stories :refer [logo-payment-info-saas
                                                                       name-only-saas
                                                                       no-logo-saas
                                                                       no-payment-info-saas]]
            [uix.core :refer [$]]))

(def test-saas-list [logo-payment-info-saas
                     
                     no-payment-info-saas
                     
                     no-logo-saas
                     
                     name-only-saas 

                     {:id "80641d76-5a6a-42d0-84a0-495d7287ae27"
                      :name "Asana"
                      :logo-url "https://asana.com/favicon.ico"
                      :last-paid-at (js/Date. "2024-06-20")}])

(def saas-list-stories
  {"여러 개인 경우" ($ SaasList {:saas-list test-saas-list})})