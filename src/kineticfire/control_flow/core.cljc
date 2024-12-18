;; (c) Copyright 2023-2024 KineticFire. All rights reserved.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;     http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.


;; KineticFire Labs: https://labs.kineticfire.com/
;;	   Project site:  https://github.com/kineticfire-labs/clojure-control-flow/


(ns kineticfire.control-flow.core
  (:gen-class))


;; (macroexpand '(-> data (assoc :a 1) (assoc :b 2)))
;; (assoc (assoc data :a 1) :b 2)

;; todo
(defmacro stop->
  "Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `stop-fn`: if 'true', then returns the current result else inserts the first form as the second
  item in second form, and so on until no forms remain."
  [x stop-fn & forms]

  (if forms
    (let [original-val x]
      (loop [threaded nil
             form-result (gensym)
             forms forms]
        (println "LOOP ---------")
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-form-result (if (= (count forms) 1)
                                   original-val
                                   (gensym))
                prev-form-result (if (seq? remaining-forms)
                                   prev-form-result
                                   original-val)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   form-result)
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~prev-form-result ~@(next form)) (meta form))
                                                   (list form prev-form-result))]
                               `(let [~form-result ~threaded-form]
                                  (if false
                                    ~form-result
                                    ~return-for-ok)))]
            (println "threaded-let: " threaded-let)
            (println "prev-form-result: " prev-form-result)
            (println "form-result: " form-result)
            (recur threaded-let prev-form-result remaining-forms))
          threaded)))
    x)


  ;; (~stop-fn ~next-val)



  ;(let [original-val x]
  ;  (loop [threaded nil
  ;         forms forms]
  ;    (println "\n\n LOOP ----------------------------")
  ;    (if forms
  ;      (let [form (last forms)
  ;            remaining-forms (butlast forms)
  ;
  ;            ;threaded-let (let [next-val (gensym)
  ;            ;                   threaded-form (if (seq? form)
  ;            ;                                   (with-meta `(~(first form) ~val ~@(next form)) (meta form))
  ;            ;                                   (list form val))]
  ;            ;               `(let [~next-val ~threaded-form]
  ;            ;                  (if (~stop-fn ~next-val)
  ;            ;                    ~next-val
  ;            ;                    ~next-val)))
  ;
  ;            threaded-let (let [prev-val (if (seq? remaining-forms)
  ;                                          (gensym)
  ;                                          original-val)
  ;                               next-val (gensym)
  ;                               return-for-ok (if (seq? remaining-forms)
  ;                                               threaded
  ;                                               next-val)
  ;                               threaded-form (if (seq? form)
  ;                                               (with-meta `(~(first form) ~prev-val ~@(next form)) (meta form))
  ;                                               (list form prev-val))]
  ;                           `(let [~next-val ~threaded-form]
  ;                              (if (~stop-fn ~next-val)
  ;                                ~next-val
  ;                                ~return-for-ok)))]
  ;
  ;        (println "remaining-forms: " remaining-forms)
  ;        (println "seq " (seq? remaining-forms))
  ;        (println "threaded-let: " threaded-let)
  ;
  ;        (recur threaded-let remaining-forms))
  ;      threaded)))



  ;(loop [x x
  ;       forms forms]
  ;  (if forms
  ;    (let [form (first forms)
  ;          threaded (if (seq? form)
  ;                     (with-meta `(~(first form) ~x ~@(next form)) (meta form))
  ;                     (list form x))
  ;          ]
  ;      (recur threaded (next forms)))
  ;    (do
  ;      (println x)
  ;      x)))
  )


;(let [val (do-something val y z)]
;  (if-not (:success val)
;    "err"
;    (let [val (another val y z)]
;      (if-not (:success val)
;        "err"
;        a))))





;;; todo
;(defmacro stop->>)
;
;;; todo
;(defmacro as-stop->)
;
;;; todo
;(defmacro cont->)
;
;;; todo
;(defmacro cont->>)
;
;;; todo
;(defmacro as-cont->)


