(ns test.siheom.siheom
  (:require [clojure.string :as string]
            [promesa.core :as p]
            [reitit.frontend.easy :as rfe]
            [test.siheom.util :as test-util]))

(defn render [react-element]
  {:run #(p/do (test-util/render-cp react-element))
   :log "컴포넌트 렌더"})

(defn query [role name]
  {:role role
   :name name})

(defn query-from [role name from]
  {:role role
   :name name
   :from from})

(defn query-first [role name]
  {:first true
   :role  role
   :name  name})

(defn query-last [role name]
  {:last  true
   :role  role
   :name  name})

(defn get-element
  ([locator]
   (get-element locator false))
  ([locator optional?]
   (cond
     (:first locator)
     (first (test-util/query-all (:role locator) (:name locator)))
     (:last locator)
     (last (test-util/query-all (:role locator) (:name locator)))
     (:from locator)
     (let [from   (:from locator)
           parent (cond (:group from)
                        (test-util/query "group" (:group from))
                        (:radiogroup from)
                        (test-util/query "radiogroup" (:radiogroup from))
                        (:list from)
                        (test-util/query "list" (:list from)))]
       (test-util/query-within parent (:role locator) (:name locator) optional?))
     :else
     (test-util/query (:role locator) (:name locator) optional?))))


(defn get-elements [locator]
  (test-util/query-all (:role locator) (:name locator)))

(defn value?
  [locator expected]
  {:run (fn [] (p/do  (test-util/value? (get-element locator) expected)
                      nil))
   :log (str "값이 " expected " 인지?: " (:role locator) " " (:name locator))})


(defn count?
  "해당하는 요소의 개수가 기대하는 숫자와 같은지 검사하고, 아니면 에러를 던집니다.
  ```clojure
  (click! (query \"radio\" \"제외된 멤버\"))
  ;; 제외된 멤버는 1명
  (count? (query \"checkbox\" #\"멤버\") 1)

  ;; 전체 멤버는 4명
  (click! (query \"radio\" \"전체 멤버\"))
  (count? (query \"checkbox\" #\"멤버\") 4)
  ```
  "
  [locator expected]
  {:run (fn [] (p/do  (test-util/count? #(get-elements locator) expected)
                      nil))
   :log (str expected "개 인지?: " (:role locator) " " (:name locator))})

(defn visible?
  "요소가 보이는지 체크하고, 보이지 않으면 기다리고, 타임아웃이 지나면 에러를 던집니다.
    
    ```clojure
    (click! (query \"checkbox\" #\"김상현\"))
    ;; 멤버를 제외했어요 토스트가 뜨기를 기다립니다
    (visible? (query \"alert\" \"멤버를 제외했어요\"))
    ```
    "
  ([locator] (visible? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/visible? #(get-element locator (not expected)) expected)
                       nil))
    :log (str (if expected "보이는지?: " "안 보이는지?: ") (:role locator) " " (:name locator))}))

(defn pressed?
  ([locator]
   (pressed? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/pressed? (get-element locator) expected)
                       nil))
    :log (str (if expected "눌림?: " "눌리지 않음?: ") (:role locator) " " (:name locator))}))

(defn readonly?
  "input 같은 요소가 읽기 전용(readonly)인지 검사하고, 아니면 에러를 던집니다.
      
   ```clojure
   ;; <input readonly=\"true\" ... />

   (readonly? (query \"textbox\" #\"회사 이메일*\"))
   ```"
  ([locator expected]
   {:run (fn [] (p/do (test-util/readonly? (get-element locator) expected)
                      nil))
    :log (str "읽기 전용 " expected "? : " (:role locator) " " (:name locator))}))

(defn current?
  "요소가 현재(aria-current) 선택되거나 위치하는 요소인지 검사하고, 아니면 에러를 던집니다.
      
   ```clojure
   ;; <a aria-current=\"false\" ... />
   (current? (query \"link\" #\"김상현\") \"false\")
      
   (click! (query \"link\" #\"김상현\"))

   ;; <a aria-current=\"true\" ... />
   (current? (query \"link\" #\"김상현\") \"true\")
   ```"
  ([locator expected]
   {:run (fn [] (p/do (test-util/current? (get-element locator) expected)
                      nil))
    :log (str "현재 " expected "? : " (:role locator) " " (:name locator))}))

(defn have-accessible-name?
  "여러 요소들의 접근 가능한 이름이 기대하는 패턴이나 문자열 목록에 매칭되는지 검사하고, 아니면 에러를 던집니다. 개수와 순서도 동일해야 합니다.
  
    ```clojure
    (have-accessible-name? (query \"checkbox\" #\"멤버\")
                        [#\"김태희\" #\"김상현\" #\"김현영\" #\"정웅기\"])
  
    (type! (query \"combobox\" \"사람 및 이메일을 입력해주세요\") \"우\")
    (have-accessible-name? (query \"checkbox\" #\"멤버\")
                        [#\"정웅기\" #\"김태희\" #\"김상현\" #\"김현영\"])
     ```
     접근 가능한 이름은 aria-label이나 aria-labelledby, alt 등으로 설정할 수 있습니다.
     button이나 link role은 기본적으로 textContent를 접근 가능한 이름으로 가집니다.
     
     @see https://developer.mozilla.org/ko/docs/Glossary/Accessible_name
     "
  ([locator expected]
   {:run (fn [] (p/do (test-util/have-accessible-name? #(get-elements locator) expected)
                      nil))
    :log (str "라벨 " expected "? : " (:role locator) " " (:name locator))}))


(defn have-text-content?
  "여러 요소들의 textContent가 기대하는 패턴이나 문자열 목록에 매칭되는지 검사하고, 아니면 에러를 던집니다. 개수와 순서도 동일해야 합니다.
  
    ```clojure
    (have-text-content? (query \"checkbox\" #\"멤버\")
                        [#\"김태희\" #\"김상현\" #\"김현영\" #\"정웅기\"])
  
    (type! (query \"combobox\" \"사람 및 이메일을 입력해주세요\") \"우\")
    (have-text-content? (query \"checkbox\" #\"멤버\")
                        [#\"정웅기\" #\"김태희\" #\"김상현\" #\"김현영\"])
     ```"
  ([locator expected]
   {:run (fn [] (p/do (test-util/have-text-content? #(get-elements locator) expected)
                      nil))
    :log (str "텍스트 " expected "? : " (:role locator) " " (:name locator))}))

(defn disabled?
  ([locator]
   (disabled? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/disabled? (get-element locator) expected)
                       nil))
    :log (str (if expected "비활성화?: " "활성화?: ") (:role locator) " " (:name locator))}))

(defn checked?
  "요소가 체크되었는지 검사하고, 아니면 에러를 던집니다. 두 번째 인자로 true를 넘겨도 동일합니다.
   
   ```html
   <input type=\"radio\" checked=\"true\" />
   ```
   
   ```clojure
   ;; checked가 true인지?
   (checked? (query \"radio\" \"실제 결제\"))
   (checked? (query \"radio\" \"실제 결제\") true)
   ```

   체크되어 있지 않은지를 검사하려면 두 번째 인자로 false를 넘깁니다.
   ```clojure
   (checked? (query \"radio\" \"실제 결제\") false)
   ```
  "
  ([locator]
   (checked? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/checked? (get-element locator) expected)
                       nil))
    :log (str (if expected "체크?: " "체크되지 않음?: ") (:role locator) " " (:name locator))}))


(defn expanded?
  "combobox나 accordion 등의 요소가 펼쳐져 있는지, 즉 aria-expanded가 true인지 검사하고, 아니면 에러를 던집니다. 두 번째 인자로 true를 넘겨도 동일합니다.
    
   ```clojure
   (expanded? (query \"combobox\" \"1개월 비용\"))
   (expanded? (query \"combobox\" \"1개월 비용\") true)
   ```

   닫혀있는지를 검사하려면 두 번째 인자로 false를 넘깁니다.
   ```clojure
   (expanded? (query \"combobox\" \"1개월 비용\") false)
   ```
  "
  ([locator]
   (expanded? locator true))
  ([locator expected]
   {:run (fn [] (p/do  (test-util/expanded? (get-element locator) expected)
                       nil))
    :log (str (if expected "펼쳐짐?: " "닫힘?: ") (:role locator) " " (:name locator))}))

(defn have-href?
  "주어진 link 요소가 해당 url을 가지고 있는지 확인합니다.
   ```clojure
   [link {:name :device-detail
          :path-params {:device-id \"oubcyxzlhapr-01\"}}]
   ;; ...
   (have-href (query \"link\" \"2402-C001\") \"/device-detail/oubcyxzlhapr-01\")
   ```
  "
  [locator expected]
  {:run (fn [] (p/do (test-util/have-href? (get-element locator) expected)
                     nil))
   :log (str "href=\"" expected "\" ?: " (:role locator) " " (:name locator))})

(defn click!
  "요소를 클릭합니다. hover 등 클릭을 하면서 일어나야 하는 이벤트도 같이 일어납니다.
   ```clojure
   (click! (query \"radio\" \"평균 비용\"))
   ```
  "
  [locator]
  {:run #(test-util/click! (fn [] (get-element locator)))
   :log (str "클릭한다!: " (:role locator) " " (:name locator))})

(defn hover!
  "요소를 호버합니다.
   ```clojure
   (hover! (query \"link\" \"자세히\"))
   ```
  "
  [locator]
  {:run #(test-util/hover! (fn [] (get-element locator)))
   :log (str "호버한다!: " (:role locator) " " (:name locator))})

(defn clear!
  "input이나 textarea 등에 입력된 값을 지웁니다
   ```clojure
   (clear! (query \"input\" \"이메일\"))
   ```
  "
  [locator]
  {:run #(test-util/clear! (get-element locator))
   :log (str "지운다!: " (:role locator) " " (:name locator))})

(defn type!
  "input이나 textarea 등의 요소에 텍스트를 입력합니다.
   ```clojure
   (type! (query \"input\" \"이메일\") \"admin@sherpas.team\")
   ```
  "
  [locator text]
  {:run #(test-util/type! (get-element locator) text)
   :log (str "\"" text "\"를 입력한다!: " (:role locator) " " (:name locator))})

(defn log!
  [text]
  {:run #(p/do)
   :log text})

(defn navigate!
  [name params]
  {:run #(p/do (rfe/navigate name params))
   :log (str "페이지를 이동한다!: " (rfe/href  name (:path-params params) (:query-params params)))})

(defn keyboard!
  "주어진 키를 칩니다
   ```clojure
   (keyboard! \"{Enter}\")
   ```
  "
  [text]
  {:run #(test-util/keyboard! text)
   :log (str "\"" text "\"를 입력한다!")})

(defn run-siheom [& lines]
  (let [logs     (atom [])
        dispatch (fn [{:keys [log run]}]
                   (swap! logs conj log)
                   (p/do (run)))]
    (-> (reduce (fn [promise line]
                  (if (p/promise? promise)
                    (p/then promise #(dispatch line))
                    (dispatch line))) nil lines)
        (p/catch (fn [error]
                   (let [max-length (apply max (map (fn [log] (.-length log)) @logs))]
                     (set! (.-message error) (str "\n" (string/join "\n" (map #(.padEnd % (inc max-length) " ") @logs)) " <- !!여기서 실패!!\n\n" (.-message error))))
                   (throw error))))))
