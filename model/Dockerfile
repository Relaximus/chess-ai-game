FROM ollama/ollama

COPY ./pull_mistral.sh ./pull_mistral.sh
RUN ./pull_mistral.sh

ENTRYPOINT exec /bin/ollama serve & /bin/ollama run mistral