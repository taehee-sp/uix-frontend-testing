(ns test.smply.hello-test 
  (:require [test.siheom.siheom :refer [query render run-siheom visible?]]
            [test.vitest.support :refer [describe it]]
            [uix.core :refer [$]]))

(describe
 "hello"
 (it "render hello world!"
     (run-siheom
      (render ($ :h1 "Hello World!"))
      (visible? (query "heading" "Hello World!")))))