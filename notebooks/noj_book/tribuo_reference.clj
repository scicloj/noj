^:kindly/hide-code
(ns noj-book.tribuo-reference
  (:require
   [clojure.java.classpath]
   [clojure.reflect]
   [scicloj.ml.tribuo]
   [noj-book.utils.tribuo-render-tools :refer [trainer-infos all-non-trainer render-configurables]]))


;; ## Tribuo reference - DRAFT 🛠

; ### Tribuo trainer reference 
^:kindly/hide-code
(render-configurables (trainer-infos))

;; ### Tribuo component reference 
^:kindly/hide-code
(render-configurables  (all-non-trainer))

     

