FROM babashka/babashka:0.8.157

COPY /src /src/
COPY /public /public/
COPY bb.edn bb.edn

ENTRYPOINT bb serve
