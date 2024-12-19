(ns noj-book.metamorph
  (:require [clojure.string :as str]))

;; # Machine learning pipelines - DRAFT 🛠
;; ## Clojure Core Pipelines  

;; Clojure has built-in support for data processing pipelines—a series of functions where the output  
;; of one step is the input to the next. In core Clojure, these are supported by the so-called  
;; **threading macro**.  

;; ### Example: Using the Threading Macro  

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

;; To address these limitations, **Metamorph pipelines** were developed. Metamorph provides a way to  
;; create pipelines that:  

;; - Compose processing steps in a readable, sequential order.  
;; - Maintain state between different stages of execution.  
;; - Allow for conditional behavior within pipeline steps.  
;; - Can be easily moved, assigned, and called like functions.  

;; *Note: The implementation details of Metamorph pipelines are beyond the scope of this chapter
;; but are designed to fulfill the goals outlined above.*  
