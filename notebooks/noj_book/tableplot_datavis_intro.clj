;; # Intro to data visualization with Tableplot - DRAFT 🛠

;; This tutorial will guide us through an exploration of the classic Iris dataset using the [Tableplot](https://scicloj.github.io/tableplot) library in Clojure. We will demonstrate how to use Tableplot's Plotly API to create various visualizations, while explaining the core ideas and functionality of the API.

;; ## Setup

(ns tableplot-book.tableplot-datavis-intro
  (:require [scicloj.tableplot.v1.plotly :as plotly]
            [tablecloth.api :as tc]
            [noj-book.datasets :as datasets]))

;; ## Introduction

;; Tableplot is a Clojure library for creating data visualizations using a functional grammar inspired by [ggplot2](https://ggplot2.tidyverse.org/) and the layered grammar of graphics. It allows for composable plots, where layers can be built up incrementally and data transformations can be seamlessly integrated.

;; In this tutorial, we will:

;; - Inspect the Iris dataset using [Tablecloth](https://scicloj.github.io/tablecloth/).
;; - Create various types of plots using Tableplot's Plotly API.
;; - Explore the relationships between different variables in the dataset.
;; - Demonstrate how to customize plots and use different features of the API.

;; ## Looking into the Iris Dataset

;; First, let's look into the Iris dataset we have read
;; [in the datasets chapter](./noj_book.datasets).

datasets/iris

;; The Iris dataset contains measurements for 150 iris flowers from three species (`setosa`, `versicolor`, `virginica`). The variables are:

;; - `sepal-length`: Length of the sepal (cm)
;; - `sepal-width`: Width of the sepal (cm)
;; - `petal-length`: Length of the petal (cm)
;; - `petal-width`: Width of the petal (cm)
;; - `species`: Species of the iris flower

;; ## Scatter Plot

;; Let's start by creating a simple scatter plot to visualize the relationship between `sepal-length` and `sepal-width`.

(-> datasets/iris
    (plotly/layer-point
     {:=x :sepal-length
      :=y :sepal-width
      :=mark-size 7}))

;; This plot shows the distribution of sepal length and width for the flowers in the dataset.

;; ### Adding Color by Species

;; To distinguish between the different species, we can add color encoding based on the `species` column.

(-> datasets/iris
    (plotly/layer-point
     {:=x :sepal-length
      :=y :sepal-width
      :=color :species
      :=mark-size 7}))

;; Now, each species is represented by a different color, making it easier to see any patterns or differences between them.

;; ## Exploring Petal Measurements

;; Next, let's explore how petal measurements vary across species.

(-> datasets/iris
    (plotly/layer-point
     {:=x :petal-length
      :=y :petal-width
      :=color :species
      :=mark-size 7}))

;; This plot shows a clearer separation between species based on petal measurements compared to sepal measurements.

;; ## Combining Sepal and Petal Measurements

;; We can create a scatter plot matrix (SPLOM) to visualize the relationships between all pairs of variables.

(-> datasets/iris
    (plotly/splom
     {:=colnames [:sepal-length :sepal-width :petal-length :petal-width]
      :=color :species
      :=height 600
      :=width 600}))

;; The SPLOM shows pairwise scatter plots for all combinations of the selected variables, with points colored by species.

;; ## Histograms

;; Let's create histograms to explore the distribution of `sepal-length`.

(-> datasets/iris
    (plotly/layer-histogram
     {:=x :sepal-length
      :=histnorm "count"
      :=histogram-nbins 20}))

;; ### Histograms by Species

;; To see how the distribution of `sepal-length` varies by species, we can add color encoding.

(-> datasets/iris
    (plotly/layer-histogram
     {:=x :sepal-length
      :=color :species
      :=histnorm "count"
      :=histogram-nbins 20
      :=mark-opacity 0.7}))

;; ## Box Plots

;; Box plots are useful for comparing distributions across categories.

(-> datasets/iris
    (plotly/layer-boxplot
     {:=y :sepal-length
      :=x :species}))

;; This box plot shows the distribution of `sepal-length` for each species.

;; ## Violin Plots

;; Violin plots provide a richer representation of the distribution.

(-> datasets/iris
    (plotly/layer-violin
     {:=y :sepal-length
      :=x :species
      :=box-visible true
      :=meanline-visible true}))

;; ## Scatter Plot with Trend Lines

;; We can add a smoothing layer to show trend lines in the data.

(-> datasets/iris
    (plotly/base
     {:=x :sepal-length
      :=y :sepal-width
      :=color :species})
    (plotly/layer-point
     {:=mark-size 7})
    (plotly/layer-smooth))

;; This plot shows a scatter plot of sepal measurements with trend lines added for each species.

;; ## Customizing Plots

;; Tableplot allows for customization of plot aesthetics.

;; ### Changing Marker Sizes

(-> datasets/iris
    (plotly/layer-point
     {:=x :sepal-length
      :=y :sepal-width
      :=color :species
      :=symbol :species
      :=mark-size 15}))

;; ### Adjusting Opacity

(-> datasets/iris
    (plotly/layer-point
     {:=x :sepal-length
      :=y :sepal-width
      :=color :species
      :=mark-size 15
      :=mark-opacity 0.6}))

;; ## 3d Scatter Plot

;; We can create a 3d scatter plot to visualize relationships in three dimensions.

(-> datasets/iris
    (plotly/layer-point
     {:=x :sepal-length
      :=y :sepal-width
      :=z :petal-length
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

;; ## References

;; - [Tableplot documentation](https://scicloj.github.io/tableplot/)
;; - [Tableplot's reference for its Plotly API](https://scicloj.github.io/tableplot/tableplot_book.plotly_reference.html)
;; - [Tableplot GitHub Repository](https://github.com/scicloj/tableplot)
;; - [Plotly.js Documentation](https://plotly.com/javascript/)
;; - [Tablecloth Documentation](https://scicloj.github.io/tablecloth/)
;; - [Iris Dataset Description](https://en.wikipedia.org/wiki/Iris_flower_data_set)
