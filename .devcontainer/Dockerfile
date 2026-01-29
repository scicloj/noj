FROM mcr.microsoft.com/devcontainers/java:21-trixie
RUN gpg --keyserver keyserver.ubuntu.com --recv-keys FF7CB5667B542092084BBDC562D54FD4003F6525
RUN bash -c "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)"
RUN bash -c "bash < <(curl -s https://raw.githubusercontent.com/clojure-lsp/clojure-lsp/master/install)"
RUN ln -s /usr/bin/python3 /usr/bin/python
