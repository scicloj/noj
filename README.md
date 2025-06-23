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

## Getting Started with Noj

### Run a 'Hello World' Example

The instructions below demonstrate running Clojure code without installing 
<a href="https://clojure.org/guides/install_clojure" target="_blank">Clojure CLI</a>:

1. Download `noj-<version>-uber.jar` from <a href="https://github.com/scicloj/noj/releases" target="_blank">Releases</a>
   into a local directory of your choice (replace `<version>` with e.g. `2-beta15`).
2. Navigate to the JAR-downloaded directory in a terminal.
3. Execute the following command to run "Hello Noj":

    a. This command will display a "Hello Noj!" message, confirming that Noj is working correctly:
        `java -cp noj-2-beta15-uber.jar clojupyter.cmdline eval '(str "Hello " "Noj!")'`

    b. This command will provide immediate access to all libraries in Noj `hello.clj` within an external Clojure file:
        `java -cp noj-2-beta15-uber.jar clojupyter.cmdline eval '(load-file "hello.clj")'`

### Live-Reload with Clay

Noj includes <a href="https://github.com/scicloj/clay" target="_blank">Clay</a>, which transforms
Clojure namespaces into interactive notebooks. 
This allows users to write and execute Clojure code within a notebook-like environment. 
For a more interactive experience, Clay can render Clojure files as live notebooks in your browser.

Launch Clay with live-reload functionality:

```bash 
java -jar noj-2-beta15-uber.jar hello.clj
```

This command opens an interactive notebook in the browser. Users can make changes 
to the file using any text editor, and changes will update in the browser automatically.

Clay offers extensive integration with detailed REPL integration and editor support. 

### Jupyter Integration

Noj provides a dedicated kernel for <a href="https://jupyter.org" target="_blank">Jupyter</a>, 
enabling notebook-style development with most Noj visualization capabilities.
The Jupyter kernel provides familiar notebook interface for users comfortable 
with the Jupyter environment.  

**Note**: There's also an ongoing effort to support <a href="https://github.com/qubit55/clojupyter_colab_setup" target="_blank">Google Colab</a>
for cloud-based notebook usage.

#### Environment Setup Process

1. To use the Jupyter kernel, users need to set up a Python environment:
    ```bash
    python3 -m venv python_venv
    source python_venv/bin/activate
    python3 -m pip install jupyterlab
    ```
2. Download `noj-<version>-uber.jar` from <a href="https://github.com/scicloj/noj/releases" target="_blank">Releases</a>
   into a local directory of your choice (replace `<version>` with e.g. `2-beta15`).
3. Navigate to the JAR-downloaded directory in a terminal.
4. Execute the following command to install the Noj Jupyter kernel:

    `java -cp noj-2-beta15-uber.jar clojupyter.cmdline install --jarfile noj-2-beta15-uber.jar --ident noj-2-beta15`

    a. Confirm the installation:
        `java -cp noj-2-beta15-uber.jar clojupyter.cmdline list-installs`
5. Launch Jupyter Lab:
    `jupyter lab`

**Technical Note**: Each notebook instance starts with its own nREPL[^3] server.
For technical support and detailed configuration questions, visit our 
<a href="https://scicloj.github.io/docs/community/chat/" target="_blank">Zulip community chat</a>.

## Learning Resources

The following list of resources provides comprehensive guidance for learning Noj, 
from interactive notebooks and video tutorials to documentation and real-world data examples.

| Resource Type             | Link                                                                                                                                |
|:--------------------------|:------------------------------------------------------------------------------------------------------------------------------------|
| 📖 Notebook/Documentation | <a href="https://scicloj.github.io/noj-v2-getting-started/" target="_blank">Noj Getting Started Notebook</a>                        |
| 🐙 Repo                   | <a href="https://github.com/scicloj/noj-v2-getting-started" target="_blank">Noj Getting Started Repo</a>                            |
| 🎥 Video                  | <a href="https://www.youtube.com/watch?v=vnvcKtHHMVQ" target="_blank">Noj Getting Started</a>                                       |
| 💾 Data Source            | <a href="https://clojureverse.org/t/the-clojure-events-calendar-feed-turns-2/" target="_blank">the Clojure Events Calendar Feed</a> |
| 🎥 Video                  | <a href="https://www.youtube.com/watch?v=gHwFCOkBb_o" target="_blank">Noj in a JAR Intro</a>                                        |
| 📖 Notebook/Documentation | <a href="https://scicloj.github.io/clay" target="_blank">Clay Documentation</a>                                                     |
| 🎥 Video                  | <a href="https://www.youtube.com/watch?v=WiOUiHsq_dc" target="_blank">Clay Overview Demo</a>                                        |

[^1]: <a href="https://scicloj.github.io/noj/noj_book.underlying_libraries.html" target="_blank">📖Underlying libraries</a>
[^2]: <a href="https://github.com/scicloj/noj/tree/main/.devcontainer" target="_blank">🐙 devcontainer setup code</a>
[^3]: <a href="https://github.com/nrepl/nrepl" target="_blank">🐙 nREPL Repo</a>

## License

Copyright © 2025 Scicloj

_EPLv1.0 is just the default for projects generated by `clj-new`: you are not_
_required to open source this project, nor are you required to use EPLv1.0!_
_Feel free to remove or change the `LICENSE` file and remove or update this_
_section of the `README.md` file!_

Distributed under the Eclipse Public License version 1.0.
