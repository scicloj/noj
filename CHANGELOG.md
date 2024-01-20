# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [1-alpha25] - 2024-01-20
- added Clojisr dependency

## [1-alpha24] - 2024-01-15
- added missing diamonds dataset (forgot to commit in the past)

## [1-alpha23] - 2024-01-14
- deps: removed core.async, updated Kindly; updated dev deps
- histogram - correct x lables

## [1-alpha22] - 2023-12-12
- handling grouped datasets correctly in linear regressions

## [1-alpha21] - 2023-12-12
- added R^2 to linear regression plots

## [1-alpha20] - 2023-12-11
- a slight renaming of columns on the table resulting from a grouped-dataset

## [1-alpha19] - 2023-12-08
- avoiding unnecessary `kind/vega-lite` annotation on compound plots (to support tables)
- added `hanami-combined-plot` API
- refactored and added information to regression models
- added model information in prediction columnw metadata
- moved hanami-related functions to a dedicated namespace
- made `calc-correlations-matrix` return a dataset

## [1-alpha18] - 2023-12-06
- removed the obsolete user namespace
- updated deps
- support for grouped dataets in `hanami-plot`

## [1-alpha17] - 2023-11-01
- updated deps, removed clerk dev dep
- adapted to kindly updates, cleaned up obsolete functions

## [1-alpha16] - 2023-10-06
- bringing the `scicloj.ml` dependency back, with the new `0.3` version which is compatible with TMD 7.x (#2)

## [1-alpha15] - 2023-09-21
- updated deps
- dataset layer: using resources directory, refactoring, column renaming

## [1-alpha14] - 2023-09-12
- updating some deps

## [1-alpha13] - 2023-09-01
- adapting to kindly v4

## [1-alpha12] - 2023-08-21
- bugfix: datasets should be under resources

## [1-alpha11] - 2023-08-12
- extensions to Hanami templates and defaults
- histogram-support
- stats: making some functionality respect tablecloth groups 
- updated deps
- added datasets namespace

## [1-alpha10] - 2023-06-23
- progress with statistical functions
- progress with Hanami conveience functions (breaking changes to the `hanami-plot` API)
- note: breaking changes to these APIs

## [1-alpha9] - 2023-05-27
- basic tensor->image support

## [1-alpha8] - 2023-04-26
- updated deps
- avoiding the scicloj.ml dependency due to conflicts

## [1-alpha7] - 2023-04-14
- another deps update

## [1-alpha6] - 2023-04-14
- updated some deps

## [1-alpha5] - 2023-02-26
- user.clj cleanup (avoiding missing deps)

## [1-alpha4] - 2023-02-26
- updated deps
- cleanup
- some stats functions (WIP)

## [1-alpha3] - 2023-02-21
- removed kind-clerk references from library code

## [1-alpha2] - 2023-02-21
- python matplotlib support
- deps updates
- minor additions to html handling

## [1-alpha1] - 2023-02-16
- initial version (with a simple tech.ml.dataset-Hanami adapter)

