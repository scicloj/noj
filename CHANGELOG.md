# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## unreleased
- use scikit-learn = "1.8.0" in integration tests
- document better "disabled tests"
- upgraded clojure version in uberjar to 1.12.4
- support Arrow, Excel, Parquet files out of-the-box with tablecloth

## [2-beta19.1 - 2025-11-09]
- version updates: Kindly, Clay, Fastmath, Hanami, Tableplot, Metamorph.ml, scicloj.ml.xgboost

## [2-beta18 - 2025-05-08]
- updated deps (Tableplot, Clay)

## [2-beta17 - 2025-04-30]
- updated deps (Kindly, Clay, Kindly-advice)

## [2-beta16 - 2025-04-18]
- updated Clay version

## [2-beta15 - 2025-04-09]
- updated Clay version

## [2-beta14] - 2025-04-02
- updated deps (Clay, Tableplot)

## [2-beta13] - 2025-03-21
- updated Clay version (IDE webview support)

## [2-beta12] - 2025-03-19
- updated deps (Clojupyter, Clay)

## [2-beta11.1] - 2025-03-15
- reverted Clojupyter verion

## [2-beta11] - 2025-03-15
- added [fitdistr](https://github.com/generateme/fitdistr) dependency
- updated deps (Clojupyter, Clay)

## [2-beta10.1] - 2025-03-12
- redeployed after version fix

## [2-beta10] - 2025-03-12
- fixed #144 - tableplot support in clojupyter
- changed the name of the deployed uberjar
- removed slf4j-simple from the main deps, but added it to the uberjar
- updated deps (Clay, SCI) 
  - the Clay update updates the CLI behaviour

## [2-beta9.1] - 2025-03-06
- a new version following an update of the CI process

## [2-beta9] - 2025-03-05
- updated deps (Clay 2-beta31.1, methamorph.ml 1.2)
- added dependency: slf4j-simple
- updated for methamorph.ml 1.2
- use kaocha for running tests
- column-names of all datasets under 
     scicloj.metamorph.ml.toydata
     scicloj.metamorph.ml.toydata.ggplot
  are now kebab-cased (ev. breaking change)

## [2-beta8] - 2025-02-25
- update deps (Clay)
- added Clay's main CLI to the uberjar build


## [2-beta7] - 2025-02-20
- updated deps (tablecloth, kindly, tableplot, same/ish, clay)
  
## [2-beta6.1] - 2025-02-05
- updated dep: Tableplot

## [2-beta6] - 2025-02-05
- updated ML related libs and tests for methamorph.ml 1.0
- updated deps: Tableplot and Clay
- this is the first version which is also released as a Clojupyter Kernel

## [2-beta5.1] - 2025-01-16
- updated deps (Tableplot)

## [2-beta5] - 2025-01-16
- updated deps (Metamorph.ml, Tableplot)

## [2-beta4] - 2025-01-01
- removed the Scicloj.ml.smile from the direct Noj dependencies (though it is still recommended to use with Noj)
- updated deps (Metamorph.ml. Scicloj.ml.tribuo, Clay, Tableplot)

## [2-beta3] - 2024-12-24
- updated deps (Libpython-clj, Tableplot, Clay)

## [2-beta2] - 2024-12-10
- added Clay

## [2-beta1] - 2024-12-08
- added libsvm and liblinear Tribuo models
- updated deps (tech.ml.dataset, metamorph.ml, scicloj.ml.tribuo, clojure.java-time, Tableplot)

## [2-alpha12.1] - 2024-11-16
- updated deps (Kindly, Fastmath, Tableplot)

## [2-alpha12] - 2024-11-08
- updated deps (Metamorph.ml, scicloj.ml.xgboost, Tableplot)

## [2-alpha11] - 2024-11-06
- added dependency: clojure.java-time

## [2-alpha10.1] - 2024-11-06
- updated deps (Kindly, Hanami, Tableplot, Kind-pyplot, SCI)

## [2-alpha10] - 2024-10-31
- added deps: sci, emmy-viewers

## [2-alpha9.3] - 2024-10-20
- updated deps (renaming Hanamicloth to Tableplot)

## [2-alpha9.2] - 2024-10-15
- updated deps (Fastmath, Hanamicloth, scicloj.ml.xgboost)

## [2-alpha9.1] - 2024-09-28
- brought back same/ish dep which was removed by mistake

## [2-alpha9] - 2024-09-28
- updated deps (scicloj.ml.tribuo, Tribuo models)
- same/ish dep was removed by mistake

## [2-alpha8.2] - 2024-09-28
- added scicloj.ml.xgboost

## [2-alpha8.1] - 2024-09-28
- added same/ish dependency

## [2-alpha8] - 2024-09-27
- updated deps (metamorph.ml. scicloj.ml.tribuo)
- reverted Tribuo version to match scicloj.ml.tribuo
- added scicloj-ml.smile, sklearn-clj

## [2-alpha7.1] - 2024-09-23
- added exclusions to deps to avoid a version conflict

## [2-alpha7] - 2024-09-22
- updated deps (Fastmath, Hanamicloth, metamorph.ml, Tribuo)
- added the deps for Tribuo xgboost models

## [2-alpha6] - 2024-09-18
- updated deps, switched to git deps rather than Clojars for now

## [2-alpha5-SNAPSHOT] - 2024-08-25
- version updates: metamorph.ml, scicloj.ml.tribuo - using mvn versions rather than git deps to resolve some conflicts

## [2-alpha4-SNAPSHOT] - 2024-08-24
- version updates: metamorph.ml, scicloj.ml.tribuo - fixing some version conflicts

## [2-alpha3-SNAPSHOT] - 2024-08-23
- changed Clojure version

## [2-alpha2-SNAPSHOT] - 2024-08-21
- added dependencies: tmd-parquet, tcutils

## [2-alpha1-SNAPSHOT] - 2024-08-20
- updated the set of depndencies: Fastmath 3-SNAPSHOT, the relevant metamorph.ml branch, kind-pyplot, hanamicloth, etc.
- removed all Noj namespaces

## [1-alpha34] - 2024-04-15
- removed the currently-unnecessary `calc-correlations-matrix` and `round` functions (#22) - thanks, @behrica
- dropped the histogram computation function and used the one in Fastmath instead (#21) - thanks, @behrica
- updated deps - in particular, added Tablecloth Columns

## [1-alpha33] - 2024-04-08
- updated Tablecloth version

## [1-alpha32] - 2024-04-08
- scicloj.ml.tribuo dependency

## [1-alpha31] - 2024-03-23
- changed histogram implementation - using bar-charts now (#5)
- updated deps
- removed the Hanami templates namespace, as it has been adapted and moved into Hanami itself

## [1-alpha30] - 2024-03-20
- reorganizing the vis namespaces

## [1-alpha29] - 2024-03-12
- rendering Hanami plots as SVG by default
               
## [1-alpha28] - 2024-02-29
- removed the tensor->image function, as it exists in dtype-next now (#14)

## [1-alpha27] - 2024-02-24
- updated metamorph.ml dep

## [1-alpha26] - 2024-02-21
- removing the datasets namespace to avoid duplication with metamporh.ml (#4)

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

