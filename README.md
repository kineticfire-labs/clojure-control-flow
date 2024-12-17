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
linear (rather than sliding to the right margin), particularly during consecutive data validation steps.


# Installation

The *clojure-control-flow* library can be installed from [Clojars](https://clojars.org/com.kineticfire/control-flow) using
one of the following methods:

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
  (:require [kineticfire.control-flow :as cf]))
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
