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
;;	   Project site: https://github.com/kineticfire-labs/clojure-control-flow/


(ns kineticfire.control-flow.core
  (:gen-class))


(defmacro continue->
  "A macro to thread first:  continue if the evaluation function returns 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `continue-fn`, which takes exactly one argument:  the output from the evaluation of the current
  form.  If the `continue-fn` returns 'false', then returns the current result (and does not continue evaluating the
  forms) else if 'true' then continues evaluating the forms by inserting the first form as the second item in second
  form, and so on until no forms remain.  The `continue-fn` is not called if there are no forms or on the result from
  the evaluation of the last form."
  [x continue-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
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
                               (if first-iteration
                                 `(let [~form-result ~threaded-form]
                                    ~return-for-ok)
                                 `(let [~form-result ~threaded-form]
                                    (if (~continue-fn ~form-result)
                                      ~return-for-ok
                                      ~form-result))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    x))


(defmacro continue->>
  "A macro to thread last:  continue if the evaluation function returns 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `continue-fn`, which takes exactly one argument:  the output from the evaluation of the current
  form.  If the `continue-fn` returns 'true', then returns the current result (and stops evaluating the forms) else if
  'false' then continues evaluating the forms by inserting the first form as the last item in second form, and so on
  until no forms remain.  The `continue-fn` is not called if there are no forms or on the result from the evaluation of
  the last form."
  [x continue-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
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
                                                   (with-meta `(~(first form) ~@(next form) ~prev-form-result) (meta form))
                                                   (list form prev-form-result))]
                               (if first-iteration
                                 `(let [~form-result ~threaded-form]
                                    ~return-for-ok)
                                 `(let [~form-result ~threaded-form]
                                    (if (~continue-fn ~form-result)
                                      ~return-for-ok
                                      ~form-result))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    x))


(defmacro continue-as->
  "A macro to thread in an arbitrary position:  continue if the evaluation function returns 'true'.

  Threads the expression `expr` through the forms `forms`. Binds `name` to `expr` in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `continue-fn`, which takes exactly one argument:  the output from the evaluation of the current
  form.  If the `continue-fn` returns 'false', then returns the current result (and does not continue evaluating the
  forms) else if 'true' then continues evaluating the forms by binding `name` to the result from the first form, and so
  on until no forms remain.  The `continue-fn` is not called if there are no forms or on the result from the evaluation
  of the last form."
  [expr name continue-fn & forms]
  (if forms
    (let [original-val expr]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-form-result (if (seq? remaining-forms)
                                   (gensym)
                                   original-val)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   form-result)
                                   ]
                               (if first-iteration
                                 `(let [~name ~prev-form-result
                                        ~form-result ~form]
                                    ~return-for-ok)
                                 `(let [~name ~prev-form-result
                                        ~form-result ~form]
                                    (if (~continue-fn ~form-result)
                                      ~return-for-ok
                                      ~form-result))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    expr))


(defmacro continue-mod->
  "A macro to thread first:  modify the result with the evaluation function, and continue if it indicates 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  Passes the result of the `continue-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `continue-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':continue' to 'false' to not pass the result to next from and return the result
  else 'true' to continue and pass the item ':data' as the second item as input to the next form.  And so on until there
  are no more forms.  The `continue-mod-fn` is not called if there are no forms; it is always called on the result from
  the evaluation of the last form."
  [x continue-mod-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             continue-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                sub (if (seq? remaining-forms)
                      `(~:data ~prev-stop-mod-fn-result)
                      prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~continue-mod-fn-result))
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~sub ~@(next form)) (meta form))
                                                   (list form prev-stop-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~form-result ~threaded-form
                                      ~continue-mod-fn-result (~continue-mod-fn ~form-result)]
                                  (if (~:continue ~continue-mod-fn-result)
                                    ~return-for-ok
                                    (~:data ~continue-mod-fn-result))))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    x))


(defmacro continue-mod->>
  "A macro to thread last:  modify the result with the evaluation function, and continue if it indicates 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
  of it if it is not a list already.  Passes the result of the `continue-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `continue-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':continue' to 'false' to not pass the result to next from and return the result
  else 'true' to continue and pass the item ':data' as the last item as input to the next form.  And so on until there
  are no more forms.  The `continue-mod-fn` is not called if there are no forms; it is always called on the result from
  the evaluation of the last form."
  [x continue-mod-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             continue-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                sub (if (seq? remaining-forms)
                      `(~:data ~prev-stop-mod-fn-result)
                      prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~continue-mod-fn-result))
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~@(next form) ~sub) (meta form))
                                                   (list form prev-stop-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~form-result ~threaded-form
                                      ~continue-mod-fn-result (~continue-mod-fn ~form-result)]
                                  (if (~:continue ~continue-mod-fn-result)
                                    ~return-for-ok
                                    (~:data ~continue-mod-fn-result))))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    x))


(defmacro continue-mod-as->
  "A macro to thread in an arbitrary position:  modify the result with the evaluation function, and continue if it
  indicates 'true'.

  Threads the expression `expr` through the forms `forms`. Binds `name` to `expr` in the first form, making a list of it
  if it is not a list already.  Passes the result of the `continue-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `continue-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':continue' to 'false' to not pass the result to next from and return the result
  else 'true' to continue and pass the item ':data' to the next form by binding `name` to the result from the first form.
  And so on until there are no more forms.  The `continue-mod-fn` is not called if there are no forms; it is always
  called on the result from the evaluation of the last form."
  [expr name continue-mod-fn & forms]
  (if forms
    (let [original-val expr]
      (loop [threaded nil
             continue-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                prev-val (if (seq? remaining-forms)
                           `(~:data ~prev-stop-mod-fn-result)
                           prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~continue-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~name ~prev-val
                                      ~form-result ~form
                                      ~continue-mod-fn-result (~continue-mod-fn ~form-result)]
                                  (if (~:continue ~continue-mod-fn-result)
                                    ~return-for-ok
                                    (~:data ~continue-mod-fn-result))))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    expr))


(defmacro stop->
  "A macro to thread first:  stop if the evaluation function returns 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
  If the `stop-fn` returns 'true', then returns the current result (and stops evaluating the forms) else if 'false'
  then continues evaluating the forms by inserting the first form as the second item in second form, and so on until no
  forms remain.  The `stop-fn` is not called if there are no forms or on the result from the evaluation of the last
  form."
  [x stop-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
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
                               (if first-iteration
                                 `(let [~form-result ~threaded-form]
                                    ~return-for-ok)
                                 `(let [~form-result ~threaded-form]
                                    (if (~stop-fn ~form-result)
                                      ~form-result
                                      ~return-for-ok))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    x))


(defmacro stop->>
  "A macro to thread last:  stop if the evaluation function returns 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
  If the `stop-fn` returns 'true', then returns the current result (and stops evaluating the forms) else if 'false'
  then continues evaluating the forms by inserting the first form as the last item in second form, and so on until no
  forms remain.  The `stop-fn` is not called if there are no forms or on the result from the evaluation of the last
  form."
  [x stop-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
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
                                                   (with-meta `(~(first form) ~@(next form) ~prev-form-result) (meta form))
                                                   (list form prev-form-result))]
                               (if first-iteration
                                 `(let [~form-result ~threaded-form]
                                    ~return-for-ok)
                                 `(let [~form-result ~threaded-form]
                                    (if (~stop-fn ~form-result)
                                      ~form-result
                                      ~return-for-ok))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    x))


(defmacro stop-as->
  "A macro to thread in an arbitrary position:  stop if the evaluation function returns 'true'.

  Threads the expression `expr` through the forms `forms`. Binds `name` to `expr` in the first form, making a list
  of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
  then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
  If the `stop-fn` returns 'true', then returns the current result (and does not continue evaluating the forms) else if
  'false' then continues evaluating the forms by binding `name` to the result for the second form, and so on until no
  forms remain.  The `stop-fn` is not called if there are no forms or on the result from the evaluation of the last
  form."
  [expr name stop-fn & forms]
  (if forms
    (let [original-val expr]
      (loop [threaded nil
             first-iteration true
             form-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-form-result (if (seq? remaining-forms)
                                   (gensym)
                                   original-val)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   form-result)]
                               (if first-iteration
                                 `(let [~name ~prev-form-result
                                        ~form-result ~form]
                                    ~return-for-ok)
                                 `(let [~name ~prev-form-result
                                        ~form-result ~form]
                                    (if (~stop-fn ~form-result)
                                      ~form-result
                                      ~return-for-ok))))]
            (recur threaded-let false prev-form-result remaining-forms))
          threaded)))
    expr))


(defmacro stop-mod->
  "A macro to thread first:  modify the result with the evaluation function, and stop if it indicates 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already.  Passes the result of the `stop-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `stop-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':stop' to 'true' to not pass the result to next from and return the result else
  'false' to continue and pass the item ':data' as the second item as input to the next form.  And so on until there
  are no more forms.  The `stop-mod-fn` is not called if there are no forms; it is always called on the result from the
  evaluation of the last form."
  [x stop-mod-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             stop-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                sub (if (seq? remaining-forms)
                      `(~:data ~prev-stop-mod-fn-result)
                      prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~stop-mod-fn-result))
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~sub ~@(next form)) (meta form))
                                                   (list form prev-stop-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~form-result ~threaded-form
                                      ~stop-mod-fn-result (~stop-mod-fn ~form-result)]
                                  (if (~:stop ~stop-mod-fn-result)
                                    (~:data ~stop-mod-fn-result)
                                    ~return-for-ok)))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    x))


(defmacro stop-mod->>
  "A macro to thread last:  modify the result with the evaluation function, and stop if it indicates 'true'.

  Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
  of it if it is not a list already.  Passes the result to the `stop-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `stop-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':stop' to 'true' to not pass the result to next from and return the result else
  'false' to continue and pass the item ':data' as the last item of input to the next form.  And so on until there are
  no more forms. The `stop-mod-fn` is not called if there are no forms; it is always called on the result from the
  evaluation of the last form."
  [x stop-mod-fn & forms]
  (if forms
    (let [original-val x]
      (loop [threaded nil
             stop-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                sub (if (seq? remaining-forms)
                      `(~:data ~prev-stop-mod-fn-result)
                      prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~stop-mod-fn-result))
                                   threaded-form (if (seq? form)
                                                   (with-meta `(~(first form) ~@(next form) ~sub) (meta form))
                                                   (list form prev-stop-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~form-result ~threaded-form
                                      ~stop-mod-fn-result (~stop-mod-fn ~form-result)]
                                  (if (~:stop ~stop-mod-fn-result)
                                    (~:data ~stop-mod-fn-result)
                                    ~return-for-ok)))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    x))


(defmacro stop-mod-as->
  "A macro to thread in an arbitrary position:  modify the result with the evaluation function, and stop if it
  indicates 'true'.

  Threads the expression `expr` through the forms `forms`. Binds `name` to `expr` in the first form, making a list of it
  if it is not a list already.  Passes the result of the `stop-mod-fn`, which takes exactly one argument:  the
  output from the evaluation of the current form.  The `stop-mod-fn` must return a map with key ':result' set to the
  data, either modified or not, and key ':stop' to 'false' to not pass the result to next from and return the result
  else 'true' to continue and pass the item ':data' to the next form by binding `name` to the result from the first form.
  And so on until there are no more forms.  The `stop-mod-fn` is not called if there are no forms; it is always
  called on the result from the evaluation of the last form."
  [expr name stop-mod-fn & forms]
  (if forms
    (let [original-val expr]
      (loop [threaded nil
             continue-mod-fn-result (gensym)
             forms forms]
        (if forms
          (let [form (last forms)
                remaining-forms (butlast forms)
                prev-stop-mod-fn-result (if (seq? remaining-forms)
                                          (gensym)
                                          original-val)
                prev-val (if (seq? remaining-forms)
                           `(~:data ~prev-stop-mod-fn-result)
                           prev-stop-mod-fn-result)
                threaded-let (let [return-for-ok (if threaded
                                                   threaded
                                                   `(~:data ~continue-mod-fn-result))
                                   form-result (gensym)]
                               `(let [~name ~prev-val
                                      ~form-result ~form
                                      ~continue-mod-fn-result (~stop-mod-fn ~form-result)]
                                  (if (~:stop ~continue-mod-fn-result)
                                    (~:data ~continue-mod-fn-result)
                                    ~return-for-ok)))]
            (recur threaded-let prev-stop-mod-fn-result remaining-forms))
          threaded)))
    expr))