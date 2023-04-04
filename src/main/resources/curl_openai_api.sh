#!/bin/bash

curl https://api.openai.com/v1/chat/completions -H "Content-Type: application/json"   -H "Authorization: Bearer $OPEN_AI_KEYS"   -d '{
    "model": "gpt-3.5-turbo",
    "messages": [{"role": "user", "content": "你被限流了"}]
  }'