#!/bin/bash

/bin/ollama serve &
sleep 5
/bin/ollama run mistral
