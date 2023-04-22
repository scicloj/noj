(ns index
  (:require [scicloj.kind-clerk.api :as kind-clerk]
            [tablecloth.api :as tc]
            [aerial.hanami.templates :as ht]
            [scicloj.noj.v1.vis :as vis]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [scicloj.kindly.v3.api :as kindly]
            [scicloj.kindly.v3.kind :as kind]
            [hiccup.core :as hiccup]
            hiccup.util))


;; ## Adapt Clerk to Kindly
(kind-clerk/setup!)
