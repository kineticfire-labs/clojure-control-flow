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
linear--rather than marching to the right margin--particularly during consecutive data validation steps resulting from 
nested `if` and `let` statements.

Consider the case when evaluating input from a web form submitted by a user that contains a user's name, email address, 
and phone number.  Assume that a validation function is available for each item, and that each function returns a map 
result with key ':valid' set to 'true' if valid or the key set to 'false' with an additional key ':reason' that give the
reason the input was invalid.  The code for this validation is shown below in Figure 1.

```clojure
(defn validate-form-data
   [form-data]
   (let [name-validation-result (validate-name form-data)]
      (if-not (:valid name-validation-result)
         name-validation-result
         (let [email-validation-result (validate-phone form-data)]
            (if-not (:valid email-validation-result)
               email-validation-result
               (let [phone-validation-result (validate-phone form-data)]
                  (if-not (:valid phone-validation-result)
                     phone-validation-result
                     {:valid true})))))))
```
<p align="center">Figure 1 -- Simple Data Validation with Code Marching to Right Margin</p>

Threading macros can help simplify such code and keep it more linear, however breaking out of subsequent validation 
steps when one fails and receiving data about why a validation failed proves challenging.  Using the `->` macro won't 
break out of the validation steps if one stage fails, so all steps will always be executed; the `some->` macro will 
break out of validation steps if one fails but can't convey data on the reason for the failure beyond `nil`.

```clojure
(ns the-project.core
   (:require [kineticfire.control-flow.core :as cf]))

(defn validate-form-data
   [form-data]
   (let [result (cf/stop-> form-data #(if (:valid %)
                                         false
                                         true)
                           (validate-name)
                           (validate-email)
                           (validate-phone))]))


```

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

todo:
Call a function from the *clojure-collections* library:
```clojure
(kf-coll/not-empty? x)
```

# Documentation
1. [control-flow.core](#control-flowcore)
   1. todo


## control-flow.core

1. [todo](#todo)

### todo

```clojure
(symmetric-difference set1 set2)
```
Returns a set that is the symmetric difference between the first set `set1` and second set `set2`.  That is, the
returned set contains all the values that are present in one set but not the other.

```clojure
(symmetric-difference #{} #{})
;;=> #{}

(symmetric-difference #{1} #{})
;;=> #{1}

(symmetric-difference #{} #{1})
;;=> #{1}

(symmetric-difference #{1 2 3} #{1 2 4})
;;=> #{3 4}
```


# License
The *clojure-control-flow* project is released under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
