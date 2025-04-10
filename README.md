![ci workflow](https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg)
[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)

# Noj - a data science toolkit
![Noj logo](notebooks/Noj.svg)

Noj gets you started with Clojure for data and science.
* You get a collection of good libraries out of the box which are tested and known-to-work-together
* .. and documentation that shows you how to use the different libraries together
* (and if you want: a 'devcontainer' setup which is known to work with the native parts of the libraries)

Noj is a Clojure library that is also released as a Jupyter kernel (see below).
It includes the [underlying libraries](https://scicloj.github.io/noj/noj_book.underlying_libraries.html)
as dependencies, and adds documentation and integration tests (which are mostly derived from the documentation, thus verifying its correctness).

The included libraries 
* use [tech.ml.dataset](https://github.com/techascent/tech.ml.dataset) directly for tabular data structures or provide high interoperability with it
* support [kindly](https://github.com/scicloj/kindly) for visualisations of data



|||
|-|-|
|Website | [https://scicloj.github.io/noj/](https://scicloj.github.io/noj/)
|Source |[![(GitHub repo)](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)|
|Deps |[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)|
|Tests |![ci workflow](https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg)|
|License |[EPLv1.0](https://github.com/scicloj/noj/blob/main/LICENSE)|
|Status |Beta stage.|
|Dev chat|[#noj-dev](https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev) at [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/)|
|User chat|[#data-science](https://clojurians.zulipchat.com/#narrow/stream/151924-data-science) at [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/)|

## Video tutorials

### From raw data to a blog post, 2025-01-24
* 📖 [notebook](https://scicloj.github.io/noj-v2-getting-started/)
* 📁 [repo](https://github.com/scicloj/noj-v2-getting-started)
* 💾 [data source - the Clojure Events Calendar Feed](https://clojureverse.org/t/the-clojure-events-calendar-feed-turns-2/)
* 💡 main topics: Tablcloth (processing), Tableplot (datavis), Clay (notebooking), Emacs, CIDER
* 🎥 video: 

[![Noj v2 video tutorial](https://img.youtube.com/vi/vnvcKtHHMVQ/0.jpg)](https://www.youtube.com/watch?v=vnvcKtHHMVQ)

## Clojure namespaces are Noj notebooks

Noj has [Clay](https://github.com/scicloj/clay) included, which takes a Clojure namespace and turns it into a notebook. To get started,

### Run a 'Hello world'

From [noj/releases](https://github.com/scicloj/noj/releases), download  `noj-<version>-uber.jar` into a local directory of your choice (replacing `<version>` with e.g. `2-beta14`).

In your terminal, switch to that directory and run the following command:
```
java -cp noj-2-beta14-uber.jar clojupyter.cmdline eval '(str "Hello " "Noj!")'
```

A nice message should appear on your screen. In this way, without having to install the Clojure CLI, you can run a Clojure program, e.g. some `hello.clj`

```
java -cp noj-2-beta14-uber.jar clojupyter.cmdline eval '(load-file "hello.clj")'
```

that already has access to all Noj libraries. However, for a more readable output of your Clojure program files,

### Use Clay with live-reload

Clay renders Clojure files as notebooks in the browser. One way to use Clay is by typing

```
java -jar noj-2-beta14-uber.jar hello.clj
```

Then, you can edit the file with any editor you they know and love and see the updating browser view. See the recent 🎥 [Noj in a JAR](https://www.youtube.com/watch?v=gHwFCOkBb_o) video.

Clay can also be used in other modes, with mode detailed integration into the use of Clojure editors and REPLs -- see the [Clay documentation](https://scicloj.github.io/clay/) and [a video overview](https://www.youtube.com/watch?v=WiOUiHsq_dc).

If you look for in-browser editing, one option is to

### Install Jupyter

Noj also provides a kernel for [Jupyter](https://jupyter.org). Compared to Clay, the kernel supports most but not all data visualization kinds. We also would like to mention the ongoing efforts to support [Colab](https://github.com/qubit55/clojupyter_colab_setup), the hosted Jupyter service. However, to start with the more mature local version, you best start using a Python environment:

```
python3 -m venv python_venv
source python_venv/bin/activate
python3 -m pip install jupyterlab
```
Then, install the Noj Jupyter Kernel using the JAR file downloaded from the [noj/releases](https://github.com/scicloj/noj/releases):

```
java -cp noj-2-beta14-uber.jar clojupyter.cmdline install --jarfile noj-2-beta14-uber.jar --ident noj-2-beta14
```

Verify,

```
java -cp noj-2-beta14-uber.jar clojupyter.cmdline list-installs
```

and run Jupyter

```
jupyter lab
```

(Technical note: every notebook starts its own `nREPL` server. For details, ask on [Zulipchat](https://scicloj.github.io/docs/community/chat/))

## License

Copyright © 2025 Scicloj

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.

