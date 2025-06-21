![ci workflow](https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg)
[![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)

# Noj - a data science toolkit
![Noj logo](notebooks/Noj.svg)

Noj is an out-of-the-box Clojure library designed to streamline data science workflows
for both newcomers and experienced users. Noj provides a tested and integrated collection 
of tools that are known to work seamlessly together from day one, rather than 
requiring users to find, configure, and integrate multiple libraries separately.

Traditional data science setups involve piecing together various libraries for different tasks,
which can be time-consuming and error-prone. Noj solves this problem by providing a
curated collection of libraries that covers the entire data science pipeline[^1]:

* **Bridges & Interop**: Connect and interact with other language systems and libraries
* **Data Processing**: Efficiently transform and prepare data for analysis
* **High-Performance Computing**: Access optimized tools to tackle complex computations
* **Mathematics & Statistics**: Perform comprehensive mathematical and statistical operations
* **Machine Learning**: Access powerful machine learning tools and models
* **Data Visualization**: Generate clear and compelling data visualizations

All included libraries are designed with consistent data handling as a unified architecture - 
they use <a href="https://github.com/techascent/tech.ml.dataset" target="_blank">tech.ml.dataset</a>
directly for tabular data structures or provide high interoperability with it, 
and support <a href="https://github.com/scicloj/kindly" target="_blank">kindly</a> framework 
for data visualization across different output formats.

## Benefits and Foundation

Noj goes above and beyond as a comprehensive bundling solution:

* **Pre-integrated and Tested Libraries**: Users receives a collection of carefully 
curated libraries that are guaranteed to work together, eliminating compatibility issues
and reducing setup time.
* **Comprehensive Documentation**: Noj provides extensive documentation and tutorials[^2] that 
demonstrate how to use different libraries in combination, with practical examples and useful resources.
* **Development-Ready Environment**: For hassle-free setup, Noj includes a **devcontainer**[^2] that
handles complex native dependencies automatically.

[^1]: <a href="https://scicloj.github.io/noj/noj_book.underlying_libraries.html" target="_blank">Underlying libraries</a> <br />
[^2]: <a href="https://github.com/scicloj/noj/tree/main/.devcontainer" target="_blank">devcontainer setup</a>

## General Info

| Resource               | Link                                                                                                                                                       |
|:-----------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Website**            | [https://scicloj.github.io/noj/](https://scicloj.github.io/noj/)                                                                                           |
| **Source Code**        | [![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/scicloj/noj)             |
| **Dependencies**       | [![Clojars Project](https://img.shields.io/clojars/v/org.scicloj/noj.svg)](https://clojars.org/org.scicloj/noj)                                            |
| **Build Status**       | ![CI Workflow](https://github.com/scicloj/noj/actions/workflows/ci.yml/badge.svg)                                                                          |
| **License**            | [EPL v1.0](https://github.com/scicloj/noj/blob/main/LICENSE)                                                                                               |
| **Development Status** | Beta stage                                                                                                                                                 |
| **Developer Chat**     | [#noj-dev](https://clojurians.zulipchat.com/#narrow/stream/321125-noj-dev) on [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/)           |
| **User Support**       | [#data-science](https://clojurians.zulipchat.com/#narrow/stream/151924-data-science) on [Clojurians Zulip](https://scicloj.github.io/docs/community/chat/) |

## Tutorial Resources

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

From [noj/releases](https://github.com/scicloj/noj/releases), download  `noj-<version>-uber.jar` into a local directory of your choice (replacing `<version>` with e.g. `2-beta15`).

In your terminal, switch to that directory and run the following command:
```
java -cp noj-2-beta15-uber.jar clojupyter.cmdline eval '(str "Hello " "Noj!")'
```

A nice message should appear on your screen. In this way, without having to install the Clojure CLI, you can run a Clojure program, e.g. some `hello.clj`

```
java -cp noj-2-beta15-uber.jar clojupyter.cmdline eval '(load-file "hello.clj")'
```

that already has access to all Noj libraries. However, for a more readable output of your Clojure program files,

### Use Clay with live-reload

Clay renders Clojure files as notebooks in the browser. One way to use Clay is by typing

```
java -jar noj-2-beta15-uber.jar hello.clj
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
java -cp noj-2-beta15-uber.jar clojupyter.cmdline install --jarfile noj-2-beta15-uber.jar --ident noj-2-beta15
```

Verify,

```
java -cp noj-2-beta15-uber.jar clojupyter.cmdline list-installs
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

