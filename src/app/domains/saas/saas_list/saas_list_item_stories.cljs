(ns app.domains.saas.saas-list.saas-list-item-stories 
  (:require [app.domains.saas.saas-list.saas-list-item :refer [saas-list-item]]
            [uix.core :refer [$]]))

(def logo-payment-info-saas
  {:id "ef4cdc19-a393-478f-8caf-9d1ab7f7a82e"
   :name "Notion"
   :logo-url "https://assets-dev.smply.app/saas-logos/9-IepQ3Rrt.png"
   :last-paid-at (js/Date. "2024-06-27")})

(def no-payment-info-saas
  {:id "c54e160e-feb2-4545-b0a4-a2ed7f70e6d2"
   :name "Slack"
   :logo-url "https://assets-global.website-files.com/621c8d7ad9e04933c4e51ffb/65eba5ffa14998827c92cc01_slack-octothorpe.png"})

(def no-logo-saas
  {:id "a983512a-4daf-45b0-892d-d7e8399d982b"
   :name "GitHub"
   :last-paid-at (js/Date. "2024-06-15")})

(def name-only-saas
  {:id "469413ca-1c22-480a-8adc-7f6b00d17324"
   :name "Zoom"})

(def saas-list-item-stories
  {"logo-payment-info-saas" ($ saas-list-item {:saas logo-payment-info-saas})
   "no-payment-info-saas" ($ saas-list-item {:saas no-payment-info-saas})
   "no-logo-saas" ($ saas-list-item {:saas no-logo-saas})
   "name-only-saas" ($ saas-list-item {:saas name-only-saas})})