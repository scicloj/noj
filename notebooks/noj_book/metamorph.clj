^:kindly/hide-code
(ns noj-book.metamorph
  (:require
   [scicloj.kindly.v4.kind :as kind]))

;; # Machine learning pipelines
;; ## Clojure Core Pipelines  

;; Clojure has built-in support for data processing pipelines—a series of functions where the output  
;; of one step is the input to the next. In core Clojure, these are supported by the so-called  
;; **threading macro**.  

;; ### Example: Using the Threading Macro  

(require '[clojure.string :as str])
(-> "hello"
    (str/upper-case)
    (str/reverse)
    (first))

;; In the example above:  

;; 1. `"hello"` is converted to uppercase, resulting in `"HELLO"`.  
;; 2. The uppercase string is reversed, giving `"OLLEH"`.  
;; 3. The first character of the reversed string is extracted, which is `\O`.  

;; ## Function Composition with `comp`  

;; We can achieve the same result using **function composition** with `comp`. Note that when using  
;; `comp`, the order of functions is reversed compared to the threading macro.  

(def upper-reverse-first
  (comp first str/reverse str/upper-case))

(upper-reverse-first "hello")

;; This defines a function `upper-reverse-first` that:  

;; 1. Converts the input string to uppercase.  
;; 2. Reverses the uppercase string.  
;; 3. Extracts the first character.  

;; #### Applying the Composed Function  

;; We can carry the composed function around and apply it in different places:  

(upper-reverse-first "world")

;; Or using `apply`:  

(apply upper-reverse-first ["world"])

;; #### Inlining the Composed Function  

;; We can also inline the composed function without assigning it to a variable:  

((comp first str/reverse str/upper-case) "hello")

;; ## Pipelines in Machine Learning  

;; In machine learning, we usually have two separate concepts:  

;; - **Pre-processing of the data**: Zero or more steps to prepare the data.  
;; - **Fitting a model**: A single step where the model learns from the data.  

;; Considering these concepts, we aim to create a pipeline that satisfies the following goals:  

;; ### Pipeline Goals  

;; - **Unify Pre-processing and Fitting**: Combine all steps into a single pipeline.  
;; - **Reusability**: The same pipeline can be executed multiple times (e.g., training vs. prediction),  
;;   possibly on different data.  
;; - **Conditional Behavior**: Functions within the pipeline may need to behave differently during  
;;   training and prediction.  
;; - **Stateful Steps**: Some steps might need to learn from the data during training and then apply  
;;   that learned state during prediction.  
;; - **Readability**: Write pipeline steps in order for easier understanding.  
;; - **Movability**: The entire pipeline should be assignable to a variable or addable to a sequence,  
;;   making it modular and reusable.  
;; - **Callable**: The pipeline should be callable like a function, taking data as input and returning  
;;   the transformed data.  

;; ### The Need for a New Approach  

;; Clojure's threading macro (`->`) and function composition (`comp`) do not fully meet these requirements  
;; because:  

;; - They lack the ability to handle state between training and prediction phases.  
;; - They don't support conditional behavior based on the execution context (e.g., training vs. prediction).  
;; - They may not represent the pipeline steps in a readable, sequential order when using `comp`.  

;; ## Introducing Metamorph Pipelines  

;; To address these limitations, **Metamorph pipelines** were developed. [Metamorph](https://github.com/scicloj/metamorph) provides a way to  
;; create pipelines that:  

;; - Compose processing steps in a readable, sequential order.  
;; - Maintain state between different stages of execution.  
;; - Allow for conditional behavior within pipeline steps.  
;; - Can be easily moved, assigned, and called like functions.  


; ### A pipeline is a composition of functions

;; A metamorph pipeline is created by the function `scicloj.metamorph.core/pipeline`.  
;; It takes functions as input and composes them in order (unlike `comp`, which composes them in reverse order).  
;; Note that it is not a macro, so it cannot take expressions such as `(str/upper-case)` directly.  

(require '[scicloj.metamorph.core :as mm] )
(def metamorph-pipeline-1
  (mm/pipeline
   str/upper-case
   str/reverse
   first))

;; This creates a function that can be called with data, like this:  
;; `(metamorph-pipeline-1 "hello")`
;;  
;; However, this would fail because metamorph pipeline functions are expected to return a map,  
;; but the above functions return a string.  
;;  
; ### Pipelines steps input/output a context map
;;
;; To maintain state and allow for **stateful steps**, we conventionally use a **context map** that is 
;; passed through each function.  
;;
;; So we can only add functions to a metamorph pipeline which input and output a single map,  
;; the so-called context map, often called `ctx`.

(def metamorph-pipeline-2
  (mm/pipeline
   (fn [ctx] ctx)
   (fn [ctx] ctx)
   (fn [ctx] ctx)))

; ### Context map key :metamorph/data 

;; A second convention is that the map should have several "default keys", and all functions should understand them.  
;; One of these keys is `:metamorph/data`.  
;;
;; It exists because in a metamorph pipeline we always pass around one main data object and several states.  
;; The main data object manipulated by the pipeline needs to be stored and passed under the key `:metamorph/data`.  
;;  
;; We now change the metamorph pipeline accordingly, so that each function reads and writes from `:metamorph/data`.  

(def metamorph-pipeline-3-a
  (mm/pipeline
   (fn [ctx]
     (assoc ctx :metamorph/data (str/upper-case (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx :metamorph/data (str/reverse (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx :metamorph/data (first (:metamorph/data ctx))))))

;; Alternatively, using `update`:  

(def metamorph-pipeline-3-b
  (mm/pipeline
   (fn [ctx] (update ctx :metamorph/data str/upper-case))
   (fn [ctx] (update ctx :metamorph/data str/reverse))
   (fn [ctx] (update ctx :metamorph/data first))))

;; Example usage:  

(metamorph-pipeline-3-a {:metamorph/data "hello"})  

(metamorph-pipeline-3-b {:metamorph/data "hello"})  

; ### Pass additional state

;; We can pass a main data object and any state through the pipeline.  

(def metamorph-pipeline-4
  (mm/pipeline
   (fn [ctx]
     (assoc ctx
            :metamorph/data (str/upper-case (:metamorph/data ctx))
            :my-state (count (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (str/reverse (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (first (:metamorph/data ctx))))))

;; Example usage:  

(metamorph-pipeline-4 {:metamorph/data "hello"})  

;;
; ### Step functions can pass state to themselves (in other :mode)

;; In nearly all cases, a step function wants to pass information only to itself.  
;; It learns something in mode `:fit` and wants to use it in a second run of the pipeline in mode `:transform`.  
;;  
;; To make this easier, each step receives in the context map a unique step ID under the key `:metamorph/id`.  
;; We can use this to store and retrieve state specific to that step,  
;; avoiding clashes of keys between different step functions.  
;;
;; (to ease readability of the code, we now use destructuring of the arguments)

(def metamorph-pipeline-5
  (mm/pipeline
   (fn [{:metamorph/keys [data id] :as ctx}]
     (assoc ctx
            :metamorph/data (str/upper-case data)
            id (str (count data))))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (str/reverse (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (first (:metamorph/data ctx))))))

;; Example usage:  

(metamorph-pipeline-5 {:metamorph/data "hello"})  

;; Note: The actual UUID will vary each time the pipeline is run.  

;; To implement the requirement of allowing different behavior per step, we introduce another key in the 
;; context map: `:metamorph/mode`.  
;;  
;; This can take two values, `:fit` and `:transform`, representing the concept of running the pipeline to 
; learn something from the data (train or fit the pipeline/model)  
;; and apply what was learned on new data (predict or transform).  
;; The learned information can be stored in the context map, becoming available in later runs.  


;; This passing of state only makes sense if the state is written to the map in one pass  
;; and used in a different pass.  

(def metamorph-pipeline-6
  (mm/pipeline
   (fn [{:metamorph/keys [data id mode] :as ctx}]
     (case mode
       :fit
       (assoc ctx
              :metamorph/data (str/upper-case data)
              id (str (count data)))    ;; write state to ctx
       :transform
       (do
         (println :state (get ctx id))  ;; read state from ctx
         ctx)))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (str/reverse (:metamorph/data ctx))))
   (fn [ctx]
     (assoc ctx
            :metamorph/data (first (:metamorph/data ctx))))))

; ### Run first in :fit then in :transform

;; This shows how the pipeline is supposed to be run twice.  
;; First in `:fit` mode and then in `:transform` mode, passing the full state context (`ctx`)  
;; while updating the standard keys.  

;; Usage:  

(def fitted-ctx  
   (metamorph-pipeline-6 {:metamorph/data "hello"  
                          :metamorph/mode :fit}))  

;; This will print `:state "5"` in the terminal, showing that the state from the `:fit` phase is used during the 
;; `:transform` phase.  

(metamorph-pipeline-6  
  (merge fitted-ctx  
         {:metamorph/data "world"  
          :metamorph/mode :transform}))  


;; #### Lifting to create pipeline functions
;; As we have seen , most pipeline functions will behave exactly the same
;; in `:fit` and `:transform`, so they neither need state.
;;
;; Example:

;; `(fn [ctx] (update ctx :metamorph/data str/upper-case))`

;; This type of functions can be created by **lifting** the base fn, so `str/upper-case`,
;; for which we provide the function  `scicloj.metamorph.core/pipeline`

(def metamorph-pipeline-7
  (mm/pipeline
   (mm/lift str/upper-case)
   (mm/lift str/reverse)
   (mm/lift first)))

(metamorph-pipeline-7 {:metamorph/data "hello"})  


;; #### Pipelines for machine learning
;; As we have seen so far, the data object at key `:metamorph/data`
;; can be anything, so far we have used a `String`.
;;
;; In machine leading pipelines we use a `tech.v3.dataset` instead,
;; and the pipeline step functions transform the dataset.
;; 
;; The **state** is often the result of a **model** function. It is calculated in `:fit` 
;; on training data and applied in `:transform` on other data to make a prediction.

;; All the rest stays the same.



