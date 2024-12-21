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
            [clojure.string                :as str]
            [kineticfire.control-flow.core :as cf]))



(defn assoc-map-last
  "Performs 'assoc', but puts the map last.  For testing."
  [key val map]
  (assoc map key val))


(defn assoc-map-middle
  "Performs 'assoc', but puts the map in the middle.  For testing."
  [key map val]
  (assoc map key val))


(deftest continue->test
  (testing "continue->: no forms"
    (let [v (cf/continue-> {:z 26} #(if (contains? % :z)
                                      true
                                      false))]
      (is (map? v))
      (is (= v {:z 26}))))
  (testing "continue->: one form, continue"
    (let [v (cf/continue-> {:z 26} #(if (contains? % :z)
                                      true
                                      false)
                           (assoc :a 1))]
      (is (map? v))
      (is (= v {:z 26 :a 1}))))
  (testing "continue->: two forms, continue"
    (let [v (cf/continue-> {:z 26} #(if (contains? % :z)
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2}))))
  (testing "continue->: three forms, continue"
    (let [v (cf/continue-> {:z 26} #(if (contains? % :z)
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3}))))
  (testing "continue->: five forms, continue fails on 1st"
    (let [v (cf/continue-> {:z 26} #(if (not (contains? % :a))
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1}))))
  (testing "continue->: five forms, continue fails on 2nd"
    (let [v (cf/continue-> {:z 26} #(if (not (contains? % :b))
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2}))))
  (testing "continue->: five forms, continue fails on 3rd"
    (let [v (cf/continue-> {:z 26} #(if (not (contains? % :c))
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3}))))
  (testing "continue->: five forms, continue fails on 4th"
    (let [v (cf/continue-> {:z 26} #(if (not (contains? % :d))
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4}))))
  (testing "continue->: five forms, continue to 5th (would fail, but not eval)"
    (let [v (cf/continue-> {:z 26} #(if (not (contains? % :e))
                                      true
                                      false)
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4 :e 5})))))


(deftest continue->>test
  (testing "continue->>: no forms, continue"
    (let [v (cf/continue->> "xyz" #(if (str/includes? % "z")
                                 true
                                 false))]
      (is (string? v))
      (is (= v "xyz"))))
  (testing "continue->>: one form, continue"
    (let [v (cf/continue->> "xyz" #(if (str/includes? % "z")
                                 true
                                 false)
                        (str "w"))]
      (is (string? v))
      (is (= v "wxyz"))))
  (testing "continue->>: two forms, continue"
    (let [v (cf/continue->> "xyz" #(if (str/includes? % "z")
                                 true
                                 false)
                        (str "w")
                        (str "v"))]
      (is (string? v))
      (is (= v "vwxyz"))))
  (testing "continue->>: three forms, continue"
    (let [v (cf/continue->> "xyz" #(if (str/includes? % "z")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u"))]
      (is (string? v))
      (is (= v "uvwxyz"))))
  (testing "continue->>: five forms, continue fails on 1st"
    (let [v (cf/continue->> "xyz" #(if (not (str/includes? % "w"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "wxyz"))))
  (testing "continue->>: five forms, continue fails on 2nd"
    (let [v (cf/continue->> "xyz" #(if (not (str/includes? % "v"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "vwxyz"))))
  (testing "continue->>: five forms, continue fails on 3rd"
    (let [v (cf/continue->> "xyz" #(if (not (str/includes? % "u"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "uvwxyz"))))
  (testing "continue->>: five forms, continue fails on 4th"
    (let [v (cf/continue->> "xyz" #(if (not (str/includes? % "t"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "tuvwxyz"))))
  (testing "continue->>: five forms, continue to 5th (would have failed)"
    (let [v (cf/continue->> "xyz" #(if (not (str/includes? % "s"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "stuvwxyz")))))


(deftest continue-as->test
  (testing "continue-as->: no forms"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "continue-as->: one form, first arg, continue"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false)
                              (assoc x :a 1))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "continue-as->: one form, middle arg, continue"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false)
                              (assoc-map-middle :a x 1))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "continue-as->: one form, last arg, continue"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false)
                              (assoc-map-last :a 1 x))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "continue-as->: two forms, continue"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc x :b 2))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2}))))
  (testing "continue-as->: three forms, varying placement, continue"
    (let [v (cf/continue-as-> {:z 0} x #(if (contains? % :z)
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3}))))
  (testing "continue-as->: five forms, varying placement, fail on 1st"
    (let [v (cf/continue-as-> {:z 0} x #(if (not (contains? % :a))
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "continue-as->: five forms, varying placement, fail on 2nd"
    (let [v (cf/continue-as-> {:z 0} x #(if (not (contains? % :b))
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2}))))
  (testing "continue-as->: five forms, varying placement, fail on 3rd"
    (let [v (cf/continue-as-> {:z 0} x #(if (not (contains? % :c))
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3}))))
  (testing "continue-as->: five forms, varying placement, fail on 4th"
    (let [v (cf/continue-as-> {:z 0} x #(if (not (contains? % :d))
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3 :d 4}))))
  (testing "continue-as->: five forms, varying placement, fail on 5th"
    (let [v (cf/continue-as-> {:z 0} x #(if (not (contains? % :e))
                                          true
                                          false)
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3 :d 4 :e 5})))))


(deftest continue-mod->test
  (testing "continue-mod->: no forms, would continue (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod-> {:z 0} #(if (contains? % :z)
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "continue-mod->: no forms, wouldn't continue (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod-> {:z 0} #(if (contains? % :a)
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "continue-mod->: one form, cont"
    (let [v (cf/continue-mod-> {:z 0} #(if (contains? % :z)
                                         {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                         {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "continue-mod->: two forms, cont"
    (let [v (cf/continue-mod-> {:z 0} #(if (contains? % :z)
                                         {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                         {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "cont"}))))
  (testing "continue-mod->: three forms, cont"
    (let [v (cf/continue-mod-> {:z 0} #(if (contains? % :z)
                                         {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                         {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3))]
      (is (map? v))
      (is (= v {:z 3 :a 1 :b 2 :c 3 :fn "cont"}))))
  (testing "continue-mod->: five forms, stop after 1st"
    (let [v (cf/continue-mod-> {:z 0} #(if (not (contains? % :a))
                                         {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                         {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3)
                               (assoc :d 4)
                               (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "stop"}))))
  (testing "continue-mod->: five forms, stop after 2nd"
    (let [v (cf/continue-mod-> {:z 0} #(if (not (contains? % :b))
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3)
                               (assoc :d 4)
                               (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "stop"}))))
  (testing "continue-mod->: five forms, stop after 3rd"
    (let [v (cf/continue-mod-> {:z 0} #(if (not (contains? % :c))
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3)
                               (assoc :d 4)
                               (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 3 :a 1 :b 2 :c 3 :fn "stop"}))))
  (testing "continue-mod->: five forms, stop after 4th"
    (let [v (cf/continue-mod-> {:z 0} #(if (not (contains? % :d))
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3)
                               (assoc :d 4)
                               (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 4 :a 1 :b 2 :c 3 :d 4 :fn "stop"}))))
  (testing "continue-mod->: five forms, stop after 5th"
    (let [v (cf/continue-mod-> {:z 0} #(if (not (contains? % :e))
                                          {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                          {:continue false :data (update (assoc % :fn "stop") :z inc)})
                               (assoc :a 1)
                               (assoc :b 2)
                               (assoc :c 3)
                               (assoc :d 4)
                               (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 5 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "stop"})))))


(deftest continue-mod->>-test
  (testing "continue-mod->>: no forms, would have stopped (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod->> "xyz" #(if (str/includes? % "a")
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)}))]
      (is (string? v))
      (is (= v "xyz"))))
  (testing "continue-mod->>: no forms, would not have stopped (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod->> "xyz" #(if (str/includes? % "z")
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)}))]
      (is (string? v))
      (is (= v "xyz"))))
  (testing "continue-mod->>: one form, continue"
    (let [v (cf/continue-mod->> "xyz" #(if (str/includes? % "z")
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w"))]
      (is (string? v))
      (is (= v "+wxyz"))))
  (testing "continue-mod->>: two forms, continue"
    (let [v (cf/continue-mod->> "xyz" #(if (str/includes? % "z")
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v"))]
      (is (string? v))
      (is (= v "+v+wxyz"))))
  (testing "continue-mod->>: three forms, continue"
    (let [v (cf/continue-mod->> "xyz" #(if (str/includes? % "z")
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u"))]
      (is (string? v))
      (is (= v "+u+v+wxyz"))))
  (testing "continue-mod->>: five forms, stop on 1st"
    (let [v (cf/continue-mod->> "xyz" #(if (not (str/includes? % "w"))
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u")
                                (str "t")
                                (str "s"))]
      (is (string? v))
      (is (= v "-wxyz"))))
  (testing "continue-mod->>: five forms, stop on 2nd"
    (let [v (cf/continue-mod->> "xyz" #(if (not (str/includes? % "v"))
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u")
                                (str "t")
                                (str "s"))]
      (is (string? v))
      (is (= v "-v+wxyz"))))
  (testing "continue-mod->>: five forms, stop on 3rd"
    (let [v (cf/continue-mod->> "xyz" #(if (not (str/includes? % "u"))
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u")
                                (str "t")
                                (str "s"))]
      (is (string? v))
      (is (= v "-u+v+wxyz"))))
  (testing "continue-mod->>: five forms, stop on 4th"
    (let [v (cf/continue-mod->> "xyz" #(if (not (str/includes? % "t"))
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u")
                                (str "t")
                                (str "s"))]
      (is (string? v))
      (is (= v "-t+u+v+wxyz"))))
  (testing "continue-mod->>: five forms, stop on 5th"
    (let [v (cf/continue-mod->> "xyz" #(if (not (str/includes? % "s"))
                                         {:continue true :data (str "+" %)}
                                         {:continue false :data (str "-" %)})
                                (str "w")
                                (str "v")
                                (str "u")
                                (str "t")
                                (str "s"))]
      (is (string? v))
      (is (= v "-s+t+u+v+wxyz")))))


(deftest continue-mod-as->test
  (testing "continue-mod-as->: no forms, would continue (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :z)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "continue-mod-as->: no forms, wouldn't continue (shouldn't call fn if no forms)"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :a)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "continue-mod-as->: one form, first arg, cont"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :z)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "continue-mod-as->: one form, middle arg, cont"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :z)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc-map-middle :a x 1))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "continue-mod-as->: one form, last arg, cont"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :z)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc-map-last :a 1 x))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "continue-mod-as->: two forms, cont"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (contains? % :z)
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc x :b 2))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "cont"}))))
  (testing "continue-mod-as->: five forms, varying placement, fail on 1st"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (not (contains? % :a))
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc-map-middle :b x 2)
                                  (assoc-map-last :c 3 x)
                                  (assoc x :d 4)
                                  (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "stop"}))))
  (testing "continue-mod-as->: five forms, varying placement, fail on 2nd"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (not (contains? % :b))
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc-map-middle :b x 2)
                                  (assoc-map-last :c 3 x)
                                  (assoc x :d 4)
                                  (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "stop"}))))
  (testing "continue-mod-as->: five forms, varying placement, fail on 3rd"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (not (contains? % :c))
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc-map-middle :b x 2)
                                  (assoc-map-last :c 3 x)
                                  (assoc x :d 4)
                                  (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 3 :a 1 :b 2 :c 3 :fn "stop"}))))
  (testing "continue-mod-as->: five forms, varying placement, fail on 4th"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (not (contains? % :d))
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc-map-middle :b x 2)
                                  (assoc-map-last :c 3 x)
                                  (assoc x :d 4)
                                  (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 4 :a 1 :b 2 :c 3 :d 4 :fn "stop"}))))
  (testing "continue-mod-as->: five forms, varying placement, fail on 5th"
    (let [v (cf/continue-mod-as-> {:z 0} x #(if (not (contains? % :e))
                                              {:continue true :data (update (assoc % :fn "cont") :z inc)}
                                              {:continue false :data (update (assoc % :fn "stop") :z inc)})
                                  (assoc x :a 1)
                                  (assoc-map-middle :b x 2)
                                  (assoc-map-last :c 3 x)
                                  (assoc x :d 4)
                                  (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 5 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "stop"})))))


(deftest stop->test
  (testing "stop->: no forms"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
                                  true
                                  false))]
      (is (map? v))
      (is (= v {:z 26}))))
  (testing "stop->: one form, no stop"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
                                  true
                                  false)
                       (assoc :a 1))]
      (is (map? v))
      (is (= v {:z 26 :a 1}))))
  (testing "stop->: two forms, no stop"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2}))))
  (testing "stop->: three forms, no stop"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :w)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3}))))
  (testing "stop->: five forms, stop after 1st"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :a)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3)
                       (assoc :d 4)
                       (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1}))))
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
  (testing "stop->: five forms, stop after 3rd"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :c)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3)
                       (assoc :d 4)
                       (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3}))))
  (testing "stop->: five forms, stop after 4th"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :d)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3)
                       (assoc :d 4)
                       (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4}))))
  (testing "stop->: five forms, stop naturally where the 5th condition would have also stopped it"
    (let [v (cf/stop-> {:z 26} #(if (contains? % :e)
                                  true
                                  false)
                       (assoc :a 1)
                       (assoc :b 2)
                       (assoc :c 3)
                       (assoc :d 4)
                       (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4 :e 5})))))


(deftest stop->>test
  (testing "stop->>: no forms"
    (let [v (cf/stop->> "abc" #(if (str/includes? % "a")
                                 true
                                 false))]
      (is (string? v))
      (is (= v "abc"))))
  (testing "stop->>: one form, no stop"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "a")
                                 true
                                 false)
                        (str "w"))]
      (is (string? v))
      (is (= v "wxyz"))))
  (testing "stop->>: two forms, no stop"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "a")
                                 true
                                 false)
                        (str "w")
                        (str "v"))]
      (is (string? v))
      (is (= v "vwxyz"))))
  (testing "stop->>: three forms, no stop"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "a")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u"))]
      (is (string? v))
      (is (= v "uvwxyz"))))
  (testing "stop->>: five forms, stop after 1st"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "w")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "wxyz"))))
  (testing "stop->>: five forms, stop after 2nd"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "v")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "vwxyz"))))
  (testing "stop->>: five forms, stop after 3rd"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "u")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "uvwxyz"))))
  (testing "stop->>: five forms, stop after 4th"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "t")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "tuvwxyz"))))
  (testing "stop->>: five forms, stop naturally where the 5th (last) condition would have also stopped it"
    (let [v (cf/stop->> "xyz" #(if (str/includes? % "s")
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "stuvwxyz")))))


(deftest stop-as->test
  (testing "stop-as->: no forms"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "stop-as->: one form, first arg, no stop"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false)
                          (assoc x :a 1))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "stop-as->: one form, middle arg, no stop"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false)
                          (assoc-map-middle :a x 1))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "stop-as->: one form, last arg, no stop"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false)
                          (assoc-map-last :a 1 x))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "stop-as->: two forms, no stop"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc x :b 2))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2}))))
  (testing "stop-as->: three forms, varying placement, no stop"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :k)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3}))))
  (testing "stop-as->: five forms, varying placement, fail on 1st"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :a)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x)
                          (assoc x :d 4)
                          (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1}))))
  (testing "stop-as->: five forms, varying placement, fail on 2nd"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :b)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x)
                          (assoc x :d 4)
                          (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2}))))
  (testing "stop-as->: five forms, varying placement, fail on 3rd"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :c)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x)
                          (assoc x :d 4)
                          (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3}))))
  (testing "stop-as->: five forms, varying placement, fail on 4th"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :d)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x)
                          (assoc x :d 4)
                          (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3 :d 4}))))
  (testing "stop-as->: five forms, varying placement, fail on 5th"
    (let [v (cf/stop-as-> {:z 0} x #(if (contains? % :e)
                                      true
                                      false)
                          (assoc x :a 1)
                          (assoc-map-middle :b x 2)
                          (assoc-map-last :c 3 x)
                          (assoc x :d 4)
                          (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 0 :a 1 :b 2 :c 3 :d 4 :e 5})))))


(deftest stop-mod->test
  (testing "stop-mod->: no forms, would have stopped (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :z)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")}))]
      (is (map? v))
      (is (= v {:z 26}))))
  (testing "stop-mod->: no forms, would not have stopped (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :a)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")}))]
      (is (map? v))
      (is (= v {:z 26}))))
  (testing "stop-mod->: one form, no stop"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :w)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :fn "cont"}))))
  (testing "stop-mod->: two forms, no stop"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :w)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :fn "cont"}))))
  (testing "stop-mod->: three forms, no stop"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :w)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :fn "cont"}))))
  (testing "stop-mod->: five forms, stop after 1st"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :a)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :fn "stop"}))))
  (testing "stop-mod->: five forms, stop after 2nd"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :b)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :fn "stop"}))))
  (testing "stop-mod->: five forms, stop after 3rd"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :c)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :fn "stop"}))))
  (testing "stop-mod->: five forms, stop after 4th"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :d)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4 :fn "stop"}))))
  (testing "stop-mod->: five forms, stop after 5th (last)"
    (let [v (cf/stop-mod-> {:z 26} #(if (contains? % :e)
                                      {:stop true :data (assoc % :fn "stop")}
                                      {:stop false :data (assoc % :fn "cont")})
                           (assoc :a 1)
                           (assoc :b 2)
                           (assoc :c 3)
                           (assoc :d 4)
                           (assoc :e 5))]
      (is (map? v))
      (is (= v {:z 26 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "stop"})))))


(deftest stop-mod->>-test
  (testing "stop-mod->>: no forms, would have stopped (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "z")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)}))]
      (is (string? v))
      (is (= v "xyz"))))
  (testing "stop-mod->>: no forms, would not have stopped (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "a")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)}))]
      (is (string? v))
      (is (= v "xyz"))))
  (testing "stop-mod->>: one form, no stop"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "a")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w"))]
      (is (string? v))
      (is (= v "+wxyz"))))
  (testing "stop-mod->>: two forms, no stop"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "a")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v"))]
      (is (string? v))
      (is (= v "+v+wxyz"))))
  (testing "stop-mod->>: three forms, no stop"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "a")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v")
                            (str "u"))]
      (is (string? v))
      (is (= v "+u+v+wxyz"))))
  (testing "stop-mod>>: five forms, stop after 1st"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "w")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))]
      (is (string? v))
      (is (= v "-wxyz"))))
  (testing "stop-mod>>: five forms, stop after 2nd"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "v")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v")
                            (str "u")
                            (str "t")
                            (str "s"))]
      (is (string? v))
      (is (= v "-v+wxyz"))))
  (testing "stop-mod>>: five forms, stop after 3rd"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "u")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v")
                            (str "u")
                            (str "t")
                            (str "s"))]
      (is (string? v))
      (is (= v "-u+v+wxyz"))))
  (testing "stop-mod>>: five forms, stop after 4th"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "t")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v")
                            (str "u")
                            (str "t")
                            (str "s"))]
      (is (string? v))
      (is (= v "-t+u+v+wxyz"))))
  (testing "stop-mod>>: five forms, stop after 5th (and last)"
    (let [v (cf/stop-mod->> "xyz" #(if (str/includes? % "s")
                                     {:stop true :data (str "-" %)}
                                     {:stop false :data (str "+" %)})
                            (str "w")
                            (str "v")
                            (str "u")
                            (str "t")
                            (str "s"))]
      (is (string? v))
      (is (= v "-s+t+u+v+wxyz")))))


(deftest stop-mod-as->test
  (testing "stop-mod-as->: no forms, would continue (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :k)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "stop-mod-as->: no forms, wouldn't continue (shouldn't call fn if no forms)"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :z)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)}))]
      (is (map? v))
      (is (= v {:z 0}))))
  (testing "stop-mod-as->: one form, first arg, no stop"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :k)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "stop-mod-as->: one form, middle arg, no stop"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :k)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc-map-middle :a x 1))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "stop-mod-as->: one form, last arg, no stop"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :k)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc-map-last :a 1 x))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "cont"}))))
  (testing "stop-mod-as->: two forms, no stop"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :k)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc x :b 2))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "cont"}))))
  (testing "stop-mod-as->: five forms, varying placement, fail on 1st"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :a)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 1 :a 1 :fn "stop"}))))
  (testing "stop-mod-as->: five forms, varying placement, fail on 2nd"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :b)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 2 :a 1 :b 2 :fn "stop"}))))
  (testing "stop-mod-as->: five forms, varying placement, fail on 3rd"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :c)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 3 :a 1 :b 2 :c 3 :fn "stop"}))))
  (testing "stop-mod-as->: five forms, varying placement, fail on 4th"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :d)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 4 :a 1 :b 2 :c 3 :d 4 :fn "stop"}))))
  (testing "stop-mod-as->: five forms, varying placement, fail on 5th"
    (let [v (cf/stop-mod-as-> {:z 0} x #(if (contains? % :e)
                                          {:stop true :data (update (assoc % :fn "stop") :z inc)}
                                          {:stop false :data (update (assoc % :fn "cont") :z inc)})
                              (assoc x :a 1)
                              (assoc-map-middle :b x 2)
                              (assoc-map-last :c 3 x)
                              (assoc x :d 4)
                              (assoc x :e 5))]
      (is (map? v))
      (is (= v {:z 5 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "stop"})))))
