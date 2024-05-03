FROM ollama/ollama

COPY ./pull_mistral.sh ./pull_mistral.sh
COPY ./run_mistral.sh ./run_mistral.sh
RUN ./pull_mistral.sh

ENTRYPOINT exec ./run_mistral.sh
#ENTRYPOINT ["/bin/sh", "-c", "./run_mistral.sh"]