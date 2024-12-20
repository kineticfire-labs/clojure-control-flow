# clojure-control-flow
[![Powered by KineticFire Labs](https://img.shields.io/badge/Powered_by-KineticFire_Labs-CDA519?link=https%3A%2F%2Flabs.kineticfire.com%2F)](https://labs.kineticfire.com/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Clojars Project](https://img.shields.io/clojars/v/com.kineticfire/control-flow.svg)](https://clojars.org/com.kineticfire/control-flow)
<p></p>

Utilities for control flow mechanisms suitable for [Clojure](https://clojure.org/), [ClojureScript](https://clojurescript.org/), and
[Babashka](https://babashka.org/).

# Contents
1. [Motivation](#motivation)
2. [Installation](#installation)
3. [Usage](#usage)
4. [Documentation](#documentation)
5. [License](#license)


# Motivation

The *clojure-control-flow* library provides functions for managing control flow with an emphasis on keeping code more 
linear--and thus more readable--rather than marching to the right margin, particularly during consecutive data 
validation steps.

Consider the case when evaluating input from a web form submitted by a user that contains a user's name, email address, 
mailing address, and phone number.  Assume that a validation function is available for each item, and that each 
function returns a map result with key `:valid` set to `true` if valid or the key set to `false` if invalid with an 
additional key `:reason` that gives the reason the input was invalid.  A function, `validate-form-data`, accepts the 
form data, validates the four data items in the form data, and has the same return data as the individual functions. The 
code for this validation is shown below in Figure 1.

```clojure
(defn validate-form-data
   [form-data]
   (let [name-validation-result (validate-name form-data)]
      (if-not (:valid name-validation-result)
         name-validation-result
         (let [email-validation-result (validate-email-address form-data)]
            (if-not (:valid email-validation-result)
               email-validation-result
               (let [mailing-address-validation-result (validate-mailing-address form-data)]
                  (if-not (:valid mailing-address-validation-result)
                     mailing-address-validation-result
                     (validate-phone-number form-data))))))))
```
<p align="center">Figure 1 -- Typical Data Validation with Code Marching to Right Margin</p>

Threading macros can help simplify such code and keep it more linear, however breaking out of subsequent validation 
steps when one fails and/or receiving data about why a validation failed proves challenging.  Using the `->` macro won't 
break out of the validation steps if one stage fails, so all steps will always be executed; the `some->` macro will 
break out of validation steps if one fails but can't convey data on the reason for the failure beyond `nil`.

Using *clojure-control-flow*, the same form data validation logic becomes more linear and readable, as depicted in 
Figure 2.

```clojure
(ns the-project.core
   (:require [kineticfire.control-flow.core :as cf]))

(defn validate-form-data
   [form-data]
   (cf/continue-> form-data #(if (:valid %)
                                true
                                false)
                  (validate-name)
                  (validate-email)
                  (validate-mailing-address)
                  (validate-phone)))
```
<p align="center">Figure 2 -- Data Validation Code Kept More Linear with <em>clojure-control-flow</em></p>

The example in Figure 2 uses the `continue->` macro from *clojure-control-flow*, which is similar to `->` in 
Clojure.core which threads the first argument through the forms as the second item; unlike `->`, `stop->` accepts a 
function that determines if the threading process should continue at each step.  The function accepts one argument--the 
result of evaluating the current form--and returns `true` to continue the process or `false` to stop.

The *clojure-control-flow* library provides functionality to keep code more linear and readable for this and other such
scenarios.  The library operates on and returns basic data structures and is not limited to a specific usage domain
(e.g., data validation), which make it broadly applicable.

# Installation

The *clojure-control-flow* library can be installed from [Clojars](https://clojars.org/com.kineticfire/control-flow) 
using one of the following methods:

## Leiningen/Boot

```
[com.kineticfire/control-flow "1.0.0"]
```

## Clojure CLI/deps.edn

```
com.kineticfire/control-flow {:mvn/version "1.0.0"}
```

## Gradle

```
implementation("com.kineticfire:control-flow:1.0.0")
```

## Maven

```
<dependency>
  <groupId>com.kineticfire</groupId>
  <artifactId>control-flow</artifactId>
  <version>1.0.0</version>
</dependency>
```

# Usage

Require the namespace in the `project.clj`, `bb.edn`, or similar file:
```clojure
(ns the-project.core
  (:require [kineticfire.control-flow.core :as cf]))
```

Call a function from the *clojure-control-flow* library:
```clojure
(cf/stop-> data #(if (:valid %)
                    false
                    true)
           (validate-name)
           (validate-email)
           (validate-mailing-address)
           (validate-phone))
```

# Documentation
1. [control-flow.core](#control-flowcore)


## control-flow.core

1. [continue->](#continue-)
2. [continue->>](#continue--1)
3. [continue-mod->](#continue-mod-)
4. [continue-mod->>](#continue-mod--1)
5. [stop->](#stop-)
6. [stop->>](#stop--1)
7. [stop-mod->](#stop-mod-)
8. [stop-mod->>](#stop-mod--1)


### continue->

```clojure
(continue-> x continue-fn & forms)
```
A macro to thread first:  continue if the evaluation function returns `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
then evaluates the `continue-fn`, which takes exactly one argument:  the output from the evaluation of the current
form.  If the `continue-fn` returns `false`, then returns the current result (and does not continue evaluating the
forms) else if `true` then continues evaluating the forms by inserting the first form as the second item in second
form, and so on until no forms remain.  The `continue-fn` is not called if there are no forms or on the result from
the evaluation of the last form.

```clojure
;; continues through all forms
(cf/continue-> {:z 0} #(if (not (contains? % :k))
                          true
                          false)
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 0 :a 1 :b 2 :c 3 :d 4 :e 5}

;; stops after 3rd form
(cf/continue-> {:z 0} #(if (not (contains? % :c))
                          true
                          false)
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 0 :a 1 :b 2 :c 3}

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(continue-> {:z 0} #()) 
;;=> {:z 0}
```

### continue->>

```clojure
(continue->> x continue-fn & forms)
```
A macro to thread last:  continue if the evaluation function returns `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
then evaluates the `continue-fn`, which takes exactly one argument:  the output from the evaluation of the current
form.  If the `continue-fn` returns `true`, then returns the current result (and stops evaluating the forms) else if
`false` then continues evaluating the forms by inserting the first form as the last item in second form, and so on
until no forms remain.  The `continue-fn` is not called if there are no forms or on the result from the evaluation of
the last form.

```clojure
;; (:require [clojure.string :as str])

;; continues through all forms
(cf/continue->> "xyz" #(if (not (str/includes? % "k"))
                          true
                          false)
                (str "w")
                (str "v")
                (str "u")
                (str "t")
                (str "s"))
;;=> stuvwxyz

;; stops after 3rd form
(cf/continue->> "xyz" #(if (not (str/includes? % "u"))
                                 true
                                 false)
                        (str "w")
                        (str "v")
                        (str "u")
                        (str "t")
                        (str "s"))
;;=> uvwxyz

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(continue->> "xyz" #())
;;=> "xyz"
```

### continue-mod->

```clojure
(continue-mod-> x continue-mod-fn & forms)
```

A macro to thread first:  modify the result with the evaluation function, and continue if it indicates `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
of it if it is not a list already.  Passes the result of the `continue-mod-fn`, which takes exactly one argument:  the
output from the evaluation of the current form.  The `continue-mod-fn` must return a map with key `:result` set to the
data, either modified or not, and key `:continue` to `false` to not pass the result to next from and return the result
else `true` to continue and pass the item `:data` as the second item as input to the next form.  And so on until there
are no more forms.  The `continue-mod-fn` is not called if there are no forms; it is always called on the result from
the evaluation of the last form.

```clojure
;; continues through all forms
(cf/continue-mod-> {:z 0} #(if (not (contains? % :k))
                               {:continue true :data (update (assoc % :fn "cont") :z inc)}
                               {:continue false :data (update (assoc % :fn "stop") :z inc)})
                   (assoc :a 1)
                   (assoc :b 2)
                   (assoc :c 3)
                   (assoc :d 4)
                   (assoc :e 5))
;;=> {:z 5 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "cont"}

;; stops after 3rd form
(cf/continue-mod-> {:z 0} #(if (not (contains? % :c))
                               {:continue true :data (update (assoc % :fn "cont") :z inc)}
                               {:continue false :data (update (assoc % :fn "stop") :z inc)})
                   (assoc :a 1)
                   (assoc :b 2)
                   (assoc :c 3)
                   (assoc :d 4)
                   (assoc :e 5))
;;=> {:z 3 :a 1 :b 2 :c 3 :fn "stop"}

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(continue-mod-> {:z 0} #((if (not (contains? % :c))
                            {:continue true :data (update (assoc % :fn "cont") :z inc)}
                            {:continue false :data (update (assoc % :fn "stop") :z inc)}))) 
;;=> {:z 0}
```

### continue-mod->>

```clojure
(continue-mod->> x continue-mod-fn & forms)
```

A macro to thread first:  modify the result with the evaluation function, and continue if it indicates `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
of it if it is not a list already.  Passes the result of the `continue-mod-fn`, which takes exactly one argument:  the
output from the evaluation of the current form.  The `continue-mod-fn` must return a map with key `:result` set to the
data, either modified or not, and key `:continue` to `false` to not pass the result to next from and return the result
else `true` to continue and pass the item `:data` as the second item as input to the next form.  And so on until there
are no more forms.  The `continue-mod-fn` is not called if there are no forms; it is always called on the result from
the evaluation of the last form.

```clojure
;; (:require [clojure.string :as str])

;; continues through all forms
(cf/continue-mod->> "xyz" #(if (not (str/includes? % "v"))
                              {:continue true :data (str "+" %)}
                              {:continue false :data (str "-" %)})
                    (str "w")
                    (str "v")
                    (str "u")
                    (str "t")
                    (str "s"))
;;=> +s+t+u+v+wxyz

;; stops after 3rd form
(cf/continue-mod->> "xyz" #(if (not (str/includes? % "v"))
                              {:continue true :data (str "+" %)}
                              {:continue false :data (str "-" %)})
                    (str "w")
                    (str "v")
                    (str "u")
                    (str "t")
                    (str "s"))
;;=> -v+wxyz

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(continue->> "xyz" #())
;;=> "xyz"
```

### stop->

```clojure
(stop-> x stop-fn & forms)
```

A macro to thread first:  stop if the evaluation function returns `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
If the `stop-fn` returns `true`, then returns the current result (and stops evaluating the forms) else if `false`
then continues evaluating the forms by inserting the first form as the second item in second form, and so on until no
forms remain.  The `stop-fn` is not called if there are no forms or on the result from the evaluation of the last
form.

```clojure
;; continues through all forms
(cf/stop-> {:z 0} #(if (contains? % :k)
                       true
                       false)
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 0 :a 1 :b 2 :c 3 :d 4 :e 5}

;; stops after 3rd form
(cf/stop-> {:z 0} #(if (contains? % :c)
                       true
                       false)
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 0 :a 1 :b 2 :c 3}

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(stop-> {:z 0} #()) 
;;=> {:z 0}
```

### stop->>

```clojure
(stop->> x stop-fn & forms)
```
A macro to thread last:  stop if the evaluation function returns `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
of it if it is not a list already.  If there are no more forms, then returns the result.  If there are more forms,
then evaluates the `stop-fn`, which takes exactly one argument:  the output from the evaluation of the current form.
If the `stop-fn` returns `true`, then returns the current result (and stops evaluating the forms) else if `false`
then continues evaluating the forms by inserting the first form as the last item in second form, and so on until no
forms remain.  The `stop-fn` is not called if there are no forms or on the result from the evaluation of the last
form.

```clojure
;; (:require [clojure.string :as str])

;; continues through all forms
(cf/stop->> "xyz" #(if (str/includes? % "a")
                      true
                      false)
            (str "w")
            (str "v")
            (str "u")
            (str "t")
            (str "s"))
;;=> stuvwxyz

;; stops after 3rd form
(cf/stop->> "xyz" #(if (str/includes? % "u")
                      true
                      false)
            (str "w")
            (str "v")
            (str "u")
            (str "t")
            (str "s"))
;;=> uvwxyz

;; no forms defined so returns 'x'; the 'continue-fn' function is irrelevant
(stop->> "xyz" #())
;;=> "xyz"
```

### stop-mod->

```clojure
(stop-mod-> x stop-mod-fn & forms)
```

A macro to thread first:  modify the result with the evaluation function, and stop if it indicates `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the second item in the first form, making a list
of it if it is not a list already.  Passes the result of the `stop-mod-fn`, which takes exactly one argument:  the
output from the evaluation of the current form.  The `stop-mod-fn` must return a map with key `:result` set to the
data, either modified or not, and key `:stop` to `true` to not pass the result to next from and return the result else
`false` to continue and pass the item `:data` as the second item as input to the next form.  And so on until there
are no more forms.  The `stop-mod-fn` is not called if there are no forms; it is always called on the result from the
evaluation of the last form.

```clojure
;; continues through all forms
(cf/stop-mod-> {:z 0} #(if (contains? % :k)
                          {:continue true :data (update (assoc % :fn "stop") :z inc)}
                          {:continue false :data (update (assoc % :fn "cont") :z inc)})
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 5 :a 1 :b 2 :c 3 :d 4 :e 5 :fn "cont"}

;; stops after 3rd form
(cf/stop-mod-> {:z 0} #(if (contains? % :c)
                          {:continue true :data (update (assoc % :fn "stop") :z inc)}
                          {:continue false :data (update (assoc % :fn "cont") :z inc)})
               (assoc :a 1)
               (assoc :b 2)
               (assoc :c 3)
               (assoc :d 4)
               (assoc :e 5))
;;=> {:z 3 :a 1 :b 2 :c 3 :fn "stop"}

;; no forms defined so returns 'x'; the 'stop-fn' function is irrelevant
(stop-mod-> {:z 0} #((if (not (contains? % :c))
                        {:continue true :data (update (assoc % :fn "stop") :z inc)}
                        {:continue false :data (update (assoc % :fn "cont") :z inc)}))) 
;;=> {:z 0}
```

### stop-mod->>

```clojure
(stop-mod->> x stop-mod-fn & forms)
```

A macro to thread last:  modify the result with the evaluation function, and stop if it indicates `true`.

Threads the expression `x` through the forms `forms`. Inserts `x` as the last item in the first form, making a list
of it if it is not a list already.  Passes the result to the `stop-mod-fn`, which takes exactly one argument:  the 
output from the evaluation of the current form.  The `stop-mod-fn` must return a map with key `:result` set to the data,
either modified or not, and key `:stop` to `true` to not pass the result to next from and return the result else `false` 
to continue and pass the item `:data` as the last item of input to the next form.  And so on until there are no more 
forms. The `stop-mod-fn` is not called if there are no forms; it is always called on the result from the evaluation of 
the last form.

```clojure
;; (:require [clojure.string :as str])

;; continues through all forms
(cf/stop-mod->> "xyz" #(if (str/includes? % "a")
                              {:continue true :data (str "+" %)}
                              {:continue false :data (str "-" %)})
                    (str "w")
                    (str "v")
                    (str "u")
                    (str "t")
                    (str "s"))
;;=> +s+t+u+v+wxyz

;; stops after 3rd form
(cf/stop-mod->> "xyz" #(if (str/includes? % "u")
                              {:continue true :data (str "+" %)}
                              {:continue false :data (str "-" %)})
                    (str "w")
                    (str "v")
                    (str "u")
                    (str "t")
                    (str "s"))
;;=> -u+v+wxyz

;; no forms defined so returns 'x'; the 'stop-fn' function is irrelevant
(stop->> "xyz" #())
;;=> "xyz"
```

# License
The *clojure-control-flow* project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
