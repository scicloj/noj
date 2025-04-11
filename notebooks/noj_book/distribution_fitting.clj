;; ## Working with distributions

;; Author: Cvetomir Dimov

;; We often have to fit diftributions to describe variable values in our data. In Clojure, the workhorse for this kind of work is the [fitdistr](https://github.com/generateme/fitdistr) package, which is based on the similarly named R package, but built on top of the Clojure [fastmath](https://github.com/generateme/fastmath). In this chapter, we will present how to fit distributions with `fitdistr`.

;; First, let us load all necessary libraries.

(ns noj-book.distribution-fitting
  (:require [tablecloth.api :as tc]
            [fitdistr.core :as fd]
            [fitdistr.distributions :as fdd]
            [scicloj.kindly.v4.kind :as kind]
            [scicloj.tableplot.v1.plotly :as plotly]))

;; ## Using distributions

;; Distributions are generated with the function `distribution`. Distribution parameter values are specified in a map.
(def a-normal-distribution (fd/distribution :normal {:mu 4 :sd 9}))

;; The density (i.e., pdf) and cumulative density (i.e., cdf) of the distribution can be computed for different values.

(fd/pdf a-normal-distribution 22)
(fd/cdf a-normal-distribution 22)

;; Note that `probability` outputs the pdf for continuous and probability for discrete distributions. For a continuous distribution like the normal, the output is the same as `pdf`.
(fd/probability a-normal-distribution 4)

;; sample - random value from distribution
;; log-likelihood - log-likelihood of data
;; likelihood - likelihood of data
;; mean and variance of the distribution
;; lower-bound and upper-bounds - distribution support
;; drandom, lrandom, irandom - double/long/integer random sample from the distribution
;; ->seq - generate sequence of samples
;; set-seed! - set distribution seed

;; Distributions can be sampled with the `->seq` function, which takes a distribution and sample size as inputs, and returns a sequence of random values.

(fd/->seq a-normal-distribution 4)

(-> (tc/dataset {:some-data (fd/->seq a-normal-distribution 10000)})
    (plotly/layer-histogram
     {:=x :some-data
      :=histnorm "count"
      :=histogram-nbins 100}))

(-> (tc/dataset {:x-data (fd/->seq a-normal-distribution 1000)
                 :y-data (fd/->seq (fd/distribution :gamma {:scale 2 :shape 2}) 1000)})
    (plotly/layer-point
     {:=x :x-data
      :=y :y-data}))

;; ## Fitting distributions

;; The function `fit` can be used for fitting distributions to data as follows `(fit method distribution data params)`. Multipe methods are supported, which include `:mle` (maximum log-likelihood estimation), `:mme` (method of moments), `:ad` (Anderson-Darling), and so on (see [fit's reference](https://cljdoc.org/d/generateme/fitdistr/1.1.0-alpha1/api/fitdistr.core#fit) for a full list). Let us fit a normal distribution to a sample from the similarly shaped logistic distribution:

(def fitted-distribution
  (fd/fit :mle :normal (fd/->seq (fd/distribution :logistic {:mu 3 :s 4}) 1000)))

;; The fitted distribution contains goodness-of-fit statistics, the estimated parameter values, and the distribution name.

fitted-distribution

;; It can be converted back to a distribution and used in all the ways listed above.
(fd/->distribution fitted-distribution)

;; ## Supported distributions

;; Currently, `fitdistr` supports a large number of distributions.
(-> fdd/distribution-data
    methods
    keys
    count)

;; Here is a full list of them:
(-> fdd/distribution-data
    methods
    keys
    sort)

;; Their parameter names are as expected by those familiar with the distributions. A complete list can be found in the [documentation](https://github.com/generateme/fitdistr//blob/4a668db8a49b356c5c639d3afeb12a34c8970144/src/fitdistr/distributions.clj). In the two following sections, we will list the most common ones. 

;; ### Commonly used discrete probability distributions

;; | distribution | fitdistr name | parameters |
;; |----|---|---|
;; | [Binomial distribution](https://en.wikipedia.org/wiki/Binomial_distribution) | `:binomial` | `:p` `:trials` |"])
;; | [Bernoulli distribution](https://en.wikipedia.org/wiki/Bernoulli_distribution) | `:bernoulli` | `:p`|
;; | [Geometric distribution](https://en.wikipedia.org/wiki/Geometric_distribution) | `:geometric` | `:p` |
;; | [Poisson distribution](https://en.wikipedia.org/wiki/Poisson_distribution) | `:poisson` | `:p` |
;; | [Negative binomial distribution](https://en.wikipedia.org/wiki/Negative_binomial_distribution) | `:negative-binomial` | `:r` `:p` |

;; ### Commonly used continuous probability distributions

;; | distribution | fitdistr name | parameters |
;; |----|---|---|
;; | [Normal distribution](https://en.wikipedia.org/wiki/Normal_distribution) | `:normal` | `:mu` `:sigma` |
;; | [Student's t distribution](https://en.wikipedia.org/wiki/Student%27s_t-distribution) | `:t` | `:degrees-of-freedom`
;; | [beta distribution](https://en.wikipedia.org/wiki/Beta_distribution) | `:beta` | `:alpha` `:beta` |
;; | [Logistic distribution](https://en.wikipedia.org/wiki/Logistic_distribution) | `:logistic` | `:mu` `:s` |
;; | [Chi-squared distribution](https://en.wikipedia.org/wiki/Chi-squared_distribution) | `:chi-squared` | `:degrees-of-freedom` |
;; | [Gamma distribution](https://en.wikipedia.org/wiki/Gamma_distribution) | `:gamma` | `:scale` | `:shape` |
;; | [Weibull distribution](https://en.wikipedia.org/wiki/Weibull_distribution) | `:weibull` | `:alpha` `:beta` |
;; | [Log-normal distribution](https://en.wikipedia.org/wiki/Log-normal_distribution) | `:log-normal` | `:scale` `:shape` |
