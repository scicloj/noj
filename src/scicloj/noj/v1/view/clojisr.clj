(ns scicloj.noj.v1.view.clojisr
  (:require [clojisr.v1.r :as r :refer [r+ r* r r->clj clj->r bra colon]]
            [clojisr.v1.require :refer [require-r]]
            [clojisr.v1.applications.plotting :as clojisr-plotting :refer [plot->file]]
            [tablecloth.api :as tc]
            [scicloj.kindly.v2.api :as kindly]
            [scicloj.kindly.v2.kind :as kind]
            [nextjournal.clerk :as clerk]))


(require-r '[base :as base :refer [$ <-]]
           '[utils :as u]
           '[stats :as stats]
           '[graphics :as g]
           '[grDevices :as dev]
           '[tidyverse]
           '[knitr :as knitr]
           '[dplyr :as dplyr]
           '[tidyr :as tidyr]
           '[ggplot2 :as gg]
           '[viridis :as viridis]
           '[forcats]
           '[extrafont]
           '[hrbrthemes :as th]
           '[datasets :refer :all])



(defn plot [spec]
  (-> spec
      clojisr-plotting/plot->buffered-image
      ;; clojisr-plotting/plot->svg
      ;; clerk/html
      ))


;; TODO: make it generic
