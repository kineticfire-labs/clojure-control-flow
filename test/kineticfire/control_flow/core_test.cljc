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


;; KineticFire Labs: https://labs.kineticfire.com
;;	   project site: https://github.com/kineticfire-labs/clojure-control-flow/


(ns kineticfire.control-flow.core-test
  (:require [clojure.test                :refer :all]))


;
;(defn perform-symmetric-difference-of-sets-test
;  [set1 set2 expected]
;  (let [v (kf-set/symmetric-difference set1 set2)]
;    (is (set? v))
;    (is (= v expected))))
;
;
;(deftest symmetric-difference-of-sets-test
;  (testing "empty sets"
;    (perform-symmetric-difference-of-sets-test #{} #{} #{}))
;  (testing "set1 empty, set2 not empty"
;    (perform-symmetric-difference-of-sets-test #{1} #{} #{1}))
;  (testing "set1 not empty, set2 empty"
;    (perform-symmetric-difference-of-sets-test #{} #{1} #{1}))
;  (testing "no diff, 1 element"
;    (perform-symmetric-difference-of-sets-test #{1} #{1} #{}))
;  (testing "no diff, multiple elements"
;    (perform-symmetric-difference-of-sets-test #{1 3 5 7} #{7 1 5 3} #{}))
;  (testing "diff, 1 element each"
;    (perform-symmetric-difference-of-sets-test #{1} #{2} #{1 2}))
;  (testing "diff, 2 elements each"
;    (perform-symmetric-difference-of-sets-test #{1 2} #{3 4} #{1 2 3 4}))
;  (testing "diff, 2 elements each with 2 in common"
;    (perform-symmetric-difference-of-sets-test #{1 7 2 8} #{8 3 4 7} #{1 2 3 4})))
