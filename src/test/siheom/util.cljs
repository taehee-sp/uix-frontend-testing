(ns test.siheom.util
  (:require ["@testing-library/react" :refer [render screen waitFor within]]
            ["@testing-library/user-event" :default user-event]
            ["accname" :refer [getAccessibleName]]
            [promesa.core :as p]))

(defn wait [duration]
  (new js/Promise (fn [resolve] (js/setTimeout resolve duration))))

(defn render-cp [element]
  (let [el element]
    (render el)
    (wait 10)))

(defn query-within
  ([parent role name]
   (query-within parent role name false))
  ([parent role name optional?]
   (let [p (within parent)]
     (if (= role "text")
       (if optional?
         (.queryByText p name)
         (.getByText p name))
       (if optional?
         (.queryByRole p role #js{:name name})
         (.getByRole p role #js{:name name}))))))

(defn query
  ([role name]
   (query role name false))
  ([role name optional?]
   (if (= role "text")
     (if optional?
       (.queryByText screen name)
       (.getByText screen name))
     (if optional?
       (.queryByRole screen role #js{:name name})
       (.getByRole screen role #js{:name name})))))

(defn query-all [role name]
  (.queryAllByRole screen role #js{:name name}))

(def fallback-element (js/document.createElement "div"))

(defn visible?
  [get-element expected]
  (waitFor (if expected
             #(get-element)
             (fn []
               (-> (js/expect (or (get-element) fallback-element))
                   (.-not)
                   (.toBeVisible))))
           #js{:timeout 1000}))
(defn click!
  [get-element]
  (p/do (visible? get-element true) ;; 보일 때까지 기다린다
        (-> (.click user-event (get-element))
            (.then (fn [] (wait 10))))
        nil))

(defn hover!
  [get-element]
  (p/do (visible? get-element true) ;; 보일 때까지 기다린다
        (-> (.hover user-event (get-element))
            (.then (fn [] (wait 10))))
        nil))

(defn clear!
  "input이나 textarea 등의 요소를 지웁니다."
  [element]
  (-> (.clear user-event element)
      (.then (fn [] (wait 10)))))

(defn keyboard!
  "주어진 키를 누릅니다."
  [key]
  (let [promise (.keyboard user-event key)]
    promise))

(defn type!
  "input이나 textarea 등의 요소에 텍스트를 입력합니다."
  [element text]
  (-> (.type user-event element text)
      (.then (fn [] (wait 10)))))


(defn value?
  [element expected]
  (waitFor #(if (nil? (.-value element))
              (-> (js/expect element)
                  (.toHaveAttribute "value" expected))
              (-> (js/expect element)
                  (.toHaveValue expected)))))

(defn count?
  [get-elements expected]
  (waitFor #(-> (js/expect (get-elements))
                (.toHaveLength expected))))

(defn checked?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toBeChecked))
              (-> (js/expect element)
                  (.-not)
                  (.toBeChecked)))))

(defn disabled?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toBeDisabled))
              (-> (js/expect element)
                  (.-not)
                  (.toBeDisabled)))))

(defn current?
  [element expected]
  (waitFor #(-> (js/expect element)
                (.toHaveAttribute "aria-current" expected))))

(defn pressed?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toHaveAttribute "aria-pressed" "true"))
              (-> (js/expect element)
                  (.-not)
                  (.toHaveAttribute  "aria-pressed" "true")))))

(defn readonly?
  [element expected]
  (waitFor #(-> (js/expect element)
                (.toHaveAttribute "readonly" expected))))

(defn expanded?
  [element expected]
  (waitFor #(if expected
              (-> (js/expect element)
                  (.toHaveAttribute "aria-expanded" "true"))
              (-> (js/expect element)
                  (.-not)
                  (.toHaveAttribute  "aria-expanded" "true")))))

(defn have-href?
  [element expected]
  (waitFor #(-> (js/expect element)
                (.toHaveAttribute "href" expected))))

(defn have-accessible-name?
  [get-elements expected]
  (waitFor (fn []
             (let [actual (vec (map getAccessibleName
                                    (get-elements)))]
               (assert (= (count actual) (count expected))
                       (str "(not= \n  " actual " \n  " expected " )"))
               (assert (every? (fn [[b a]]
                                 (if (regexp? b)
                                   (some? (re-find b a))
                                   (= a b))) (map vector expected actual))
                       (str "(not= \n  " actual " \n  " expected " )"))))))


(defn have-text-content?
  [get-elements expected]
  (waitFor (fn []
             (let [actual (vec (map #(.-textContent %)
                                    (get-elements)))]
               (assert (= (count actual) (count expected))
                       (str "(not= \n  " actual " \n  " expected " )"))
               (assert (every? (fn [[b a]]
                                 (if (regexp? b)
                                   (some? (re-find b a))
                                   (= a b))) (map vector expected actual))
                       (str "(not= \n  " actual " \n  " expected " )"))))))

(defn assert-equal [a b]
  (.toBe (js/expect a) b))