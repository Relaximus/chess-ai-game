#!/bin/bash

/bin/ollama serve &
ollama_pid=$!
echo "Ollama pid: $ollama_pid"
sleep 5
/bin/ollama pull mistral
kill -INT $ollama_pid
