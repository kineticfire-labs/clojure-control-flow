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


(defmacro stop->
  "Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
  If the `stop-fn` returns 'true', then returns the current result (and stops evaluating the forms) else if 'false'
  then continues evaluating the forms by inserting the first form as the second item in second form, and so on until no
  forms remain.  The `stop-fn` is not called on the result from the evaluation of the last form."
  [x stop-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             form-result (gensym)
             forms forms]
        (println "\n\nLOOP----")
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-form-result (if (seq? remaining-forms)
                                   (gensym)
                                   original-val)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   form-result)
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~prev-form-result ~@(next form)) (meta form))
                                                   (list form prev-form-result))]
                               (if (seq? remaining-forms)
                                 `(let [~form-result ~threaded-form] ;; todo: don't add 'if' if last expression
                                    (if (~stop-fn ~form-result)
                                      ~form-result
                                      ~return-for-ok))
                                 `(let [~form-result ~threaded-form]
                                    (if (~stop-fn ~form-result)
                                      ~form-result
                                      ~return-for-ok))))]
            (println "threaded-let: " threaded-let)
            (recur threaded-let prev-form-result remaining-forms))
          threaded)))
    x))


;; todo: for REFERENCE
  ;; (~stop-fn ~next-val)
;(defmacro stop->
;  "Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
;  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
;  then evaluates the `stop-fn`: if 'true', then returns the current result else inserts the first form as the second
;  item in second form, and so on until no forms remain."
;  [x stop-fn & forms]
;
;  (if forms
;    (let [original-val x]
;      (loop [threaded nil
;             form-result (gensym)
;             forms forms]
;        (println "LOOP ---------")
;        (if forms
;          (let [form (last forms)
;                remaining-forms (butlast forms)
;                prev-form-result (if (= (count forms) 1)
;                                   original-val
;                                   (gensym))
;                prev-form-result (if (seq? remaining-forms)
;                                   prev-form-result
;                                   original-val)
;                threaded-let (let [return-for-ok (if threaded
;                                                   threaded
;                                                   form-result)
;                                   threaded-form (if (seq? form)
;                                                   (with-meta `(~(first form) ~prev-form-result ~@(next form)) (meta form))
;                                                   (list form prev-form-result))]
;                               `(let [~form-result ~threaded-form]
;                                  (if false
;                                    ~form-result
;                                    ~return-for-ok)))]
;            (println "threaded-let: " threaded-let)
;            (println "prev-form-result: " prev-form-result)
;            (println "form-result: " form-result)
;            (recur threaded-let prev-form-result remaining-forms))
;          threaded)))
;    x))






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


