FROM mcr.microsoft.com/devcontainers/java:21-bookworm

RUN bash -c "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)"
RUN bash -c "bash < <(curl -s https://raw.githubusercontent.com/clojure-lsp/clojure-lsp/master/install)"
RUN ln -s /usr/bin/python3 /usr/bin/python
