FROM ollama/ollama

COPY ./pull_mistral.sh ./pull_mistral.sh
RUN ./pull_mistral.sh

ENTRYPOINT ["/bin/sh", "-c", "./run_mistral.sh"]