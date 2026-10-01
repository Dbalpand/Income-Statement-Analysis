package com.divyesh.incomestatementanalysis.ai;

import org.springframework.stereotype.Component;

@Component
public class BedrockPromptBuilder {

    /**
     * Builds the prompt for AWS Bedrock (Anthropic Claude 3 Haiku).
     *
     * @param extractedText OCR extracted text
     * @return Prompt to send to Bedrock
     */
    public String buildPrompt(String extractedText) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are an expert financial analyst.

Analyze the following OCR extracted income statement.

Extract the information and return ONLY valid JSON.

Do not explain anything.
Do not add markdown.
Do not add comments.
Do not include extra text.

The JSON format must be exactly:

{
  "companyName": "",
  "financialYear": "",
  "currency": "",
  "items": [
    {
      "name": "",
      "amount": 0,
      "confidence": 0.0,
      "source": ""
    }
  ]
}

Income Statement:

""");

        prompt.append(extractedText);

        return prompt.toString();

    }

}