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

(defmacro ->|
  "Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
  of it if it is not a list already. If there are no more forms, then returns the result.  If there are more forms, then
  evaluates the `stop-condition`: if 'true', then returns the current result else inserts the first form as the second
  item in second form, and so on until no forms remain."
  [x stop-condition & forms]
  (loop [x     x
         forms forms]
    (if forms
      (let [forms (first forms)])
      x))
  )


