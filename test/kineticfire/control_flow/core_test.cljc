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
  (:require [clojure.test                  :refer :all]
            [kineticfire.control-flow.core :as cf]))



(deftest stop->test
  ;(testing "stop->: no forms"
  ;  (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
  ;                                true
  ;                                false))]
  ;    (is (map? v))
  ;    (is (= v {:z 26}))))
  ;(testing "stop->: one form, no stop"
  ;  (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
  ;                                true
  ;                                false)
  ;                     (assoc :a 1))]
  ;    (is (map? v))
  ;    (is (= v {:z 26 :a 1}))))
  ;(testing "stop->: three forms, no stop"
  ;  (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
  ;                                true
  ;                                false)
  ;                     (assoc :a 1)
  ;                     (assoc :b 2)
  ;                     (assoc :c 3))]
  ;    (is (map? v))
  ;    (is (= v {:z 26 :a 1 :b 2 :c 3}))))
  ;(testing "stop->: five forms, stop after 1st"
  ;  (let [v (cf/stop-> {:z 26} #(if (contains? % :a)
  ;                                true
  ;                                false)
  ;                     (assoc :a 1)
  ;                     (assoc :b 2)
  ;                     (assoc :c 3)
  ;                     (assoc :d 4)
  ;                     (assoc :e 5))]
  ;    (is (map? v))
  ;    (is (= v {:z 26 :a 1}))))
  (testing "stop->: five forms, stop after 2nd"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :b)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3)
                       (assoc :d 4)
                       (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2}))))
  ;(testing "stop->: five forms, stop after 3rd"
  ;  (let [v (cf/stop-> {:z 26} #(if (contains? % :c)
  ;                                true
  ;                                false)
  ;                     (assoc :a 1)
  ;                     (assoc :b 2)
  ;                     (assoc :c 3)
  ;                     (assoc :d 4)
  ;                     (assoc :e 5))]
  ;    (is (map? v))
  ;    (is (= v {:z 26 :a 1 :b 2 :c 3}))))

  )

