;; # Intro to data visualization with Tableplot

;; This tutorial will guide us through an exploration of the classic Iris dataset using the Tableplot library in Clojure. We will demonstrate how to use Tableplot's Plotly API to create various visualizations, while explaining the core ideas and functionality of the API.

;; ## Setup

(ns tableplot-book.tableplot-datavis-intro
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]
            [tablecloth.column.api :as tcc]
            [scicloj.kindly.v4.api :as kindly]
            [scicloj.kindly.v4.kind :as kind]
            [tech.v3.dataset.print :as ds-print]))


;; ## Introduction

;; Tableplot is a Clojure library for creating data visualizations using a functional grammar inspired by [ggplot2](https://ggplot2.tidyverse.org/) and the layered grammar of graphics. It allows for composable plots, where layers can be built up incrementally and data transformations can be seamlessly integrated.

;; In this tutorial, we will:

;; - Load and inspect the Iris dataset using Tablecloth.
;; - Create various types of plots using Tableplot's Plotly API.
;; - Explore the relationships between different variables in the dataset.
;; - Demonstrate how to customize plots and use different features of the API.

;; ## Loading the Iris Dataset

;; First, let's load the Iris dataset. We can use Tablecloth's `read-dataset` function to read the data.

(def iris
  (tc/dataset "https://raw.githubusercontent.com/mwaskom/seaborn-data/master/iris.csv"
              {:key-fn keyword}))

;; Let's take a look at the first few rows of the dataset.

(tc/head iris)

;; The Iris dataset contains measurements for 150 iris flowers from three species (`setosa`, `versicolor`, `virginica`). The variables are:

;; - `sepal_length`: Length of the sepal (cm)
;; - `sepal_width`: Width of the sepal (cm)
;; - `petal_length`: Length of the petal (cm)
;; - `petal_width`: Width of the petal (cm)
;; - `species`: Species of the iris flower

;; ## Scatter Plot

;; Let's start by creating a simple scatter plot to visualize the relationship between `sepal_length` and `sepal_width`.

(-> iris
    (plotly/layer-point
      {:=x :sepal_length
       :=y :sepal_width
       :=mark-size 10}))

;; This plot shows the distribution of sepal length and width for the flowers in the dataset.

;; ### Adding Color by Species

;; To distinguish between the different species, we can add color encoding based on the `species` column.

(-> iris
    (plotly/layer-point
      {:=x :sepal_length
       :=y :sepal_width
       :=color :species
       :=mark-size 10}))

;; Now, each species is represented by a different color, making it easier to see any patterns or differences between them.

;; ## Exploring Petal Measurements

;; Next, let's explore how petal measurements vary across species.

(-> iris
    (plotly/layer-point
      {:=x :petal_length
       :=y :petal_width
       :=color :species
       :=mark-size 10}))

;; This plot shows a clearer separation between species based on petal measurements compared to sepal measurements.

;; ## Combining Sepal and Petal Measurements

;; We can create a scatter plot matrix (SPLOM) to visualize the relationships between all pairs of variables.

(-> iris
    (plotly/splom
      {:=colnames [:sepal_length :sepal_width :petal_length :petal_width]
       :=color :species
       :=height 600
       :=width 600}))

;; The SPLOM shows pairwise scatter plots for all combinations of the selected variables, with points colored by species.

;; ## Histograms

;; Let's create histograms to explore the distribution of `sepal_length`.

(-> iris
    (plotly/layer-histogram
     {:=x :sepal_length
      :=histnorm "count"
      :=histogram-nbins 20}))

;; ### Histograms by Species

;; To see how the distribution of `sepal_length` varies by species, we can add color encoding.

(-> iris
    (plotly/layer-histogram
     {:=x :sepal_length
      :=color :species
      :=histnorm "count"
      :=histogram-nbins 20
      :=mark-opacity 0.7}))

;; ## Box Plots

;; Box plots are useful for comparing distributions across categories.

(-> iris
    (plotly/layer-boxplot
      {:=y :sepal_length
       :=x :species}))

;; This box plot shows the distribution of `sepal_length` for each species.

;; ## Violin Plots

;; Violin plots provide a richer representation of the distribution.

(-> iris
    (plotly/layer-violin
      {:=y :sepal_length
       :=x :species
       :=box-visible true
       :=meanline-visible true}))

;; ## Scatter Plot with Trend Lines

;; We can add a smoothing layer to show trend lines in the data.

(-> iris
    (plotly/base
      {:=x :sepal_length
       :=y :sepal_width
       :=color :species})
    (plotly/layer-point
      {:=mark-size 10})
    (plotly/layer-smooth))

;; This plot shows a scatter plot of sepal measurements with trend lines added for each species.

;; ## Customizing Plots

;; Tableplot allows for customization of plot aesthetics.

;; ### Changing Marker Symbols

(-> iris
    (plotly/layer-point
      {:=x :sepal_length
       :=y :sepal_width
       :=color :species
       :=symbol :species
       :=mark-size 10}))

;; ### Adjusting Opacity

(-> iris
    (plotly/layer-point
      {:=x :sepal_length
       :=y :sepal_width
       :=color :species
       :=mark-size 10
       :=mark-opacity 0.6}))

;; ## Surface Plot (3D Visualization)

;; We can create a surface plot to visualize relationships in three dimensions.

(-> iris
    (plotly/layer-point
     {:=x :sepal_length
      :=y :sepal_width
      :=z :petal_length
      :=color :species
      :=coordinates :3d
      :=mark-size 3}))

;; ## Conclusion

;; In this tutorial, we have explored the Iris dataset using the Tableplot library in Clojure. We demonstrated how to create various types of plots, customize them, and explore relationships in the data.

;; Tableplot's API is designed to be intuitive and flexible, allowing for the creation of complex plots with simple, composable functions.

;; For more information and advanced usage, refer to the Tableplot documentation.

;; ## Appendix: Understanding the Tableplot API

;; The core idea of the Tableplot API is to build plots by composing layers. Each layer corresponds to a visual representation of data, such as points, lines, bars, etc.

;; ### Basic Functions

;; - `plotly/layer-point`: Adds a scatter plot layer with points.
;; - `plotly/layer-line`: Adds a line plot layer.
;; - `plotly/layer-bar`: Adds a bar plot layer.
;; - `plotly/layer-boxplot`: Adds a box plot layer.
;; - `plotly/layer-violin`: Adds a violin plot layer.
;; - `plotly/layer-histogram`: Adds a histogram layer.
;; - `plotly/layer-smooth`: Adds a smoothing layer (trend line).
;; - `plotly/splom`: Creates a scatter plot matrix (SPLOM).

;; ### Parameters

;; Parameters are provided as a map, with keys prefixed by `:=` to distinguish them from dataset columns.

;; - `:=x`: The x-axis variable.
;; - `:=y`: The y-axis variable.
;; - `:=z`: The z-axis variable (for 3D plots).
;; - `:=color`: Variable used to color the data points.
;; - `:=symbol`: Variable used to determine marker symbols.
;; - `:=mark-opacity`: Opacity of the markers.
;; - `:=mark-size`: Size of the markers.
;; - `:=histogram-nbins`: Number of bins in the x-axis for histograms.
;; - `:=box-visible`: Whether to show box plot inside violin plots.
;; - `:=meanline-visible`: Whether to show mean line in violin plots.

;; ### Composing Plots

;; Plots are built by starting with a dataset and chaining layer functions.

(comment
  (-> dataset
      (plotly/layer-point
       {:=x :x-variable
        :=y :y-variable})))

;; Multiple layers can be added to create more complex plots.

(comment
  (-> dataset
      (plotly/base
       {:=x :x-variable
        :=y :y-variable})
      (plotly/layer-point)
      (plotly/layer-smooth)))

;; ### Customization

;; The Tableplot API allows for detailed customization of plot aesthetics by adjusting parameters in the layer functions.

;; ### Exploring the Data with `tablecloth`

;; In addition to plotting, we can use Tablecloth's functions to manipulate and explore the dataset.

;; #### Summarizing the Data

(tc/info iris)

;; #### Grouping and Aggregation

;; Let's calculate the mean sepal length for each species.

(-> iris
    (tc/group-by :species)
    (tc/aggregate {:mean-sepal-length #(tcc/mean (:sepal_length %))}))

;; #### Filtering Data

;; We can filter the dataset to include only one species.

(def setosa-data
  (tc/select-rows iris #(= (:species %) "setosa")))

(tc/head setosa-data)

;; ## References

;; - [Tableplot GitHub Repository](https://github.com/scicloj/tableplot)
;; - [Plotly.js Documentation](https://plotly.com/javascript/)
;; - [Tablecloth Documentation](https://scicloj.github.io/tablecloth/)
;; - [Iris Dataset Description](https://en.wikipedia.org/wiki/Iris_flower_data_set)
