FROM mcr.microsoft.com/devcontainers/java:21-trixie
RUN rm  -f /etc/apt/sources.list.d/yarn.list 
RUN bash -c "bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)"
RUN ln -s /usr/bin/python3 /usr/bin/python
