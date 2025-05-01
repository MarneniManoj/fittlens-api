package com.fittlens.core.service;

import com.fittlens.core.dto.EquipmentResponse;
import com.fittlens.core.dto.ai.EquipmentRecognitionResponse;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.ResponseFormatJsonSchema;
import com.openai.models.ResponseFormatJsonSchema.JsonSchema;
import com.openai.core.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private OpenAIClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        client = OpenAIOkHttpClient
                    .builder()
                    .apiKey(openaiApiKey)
                    .build();
    }

    public EquipmentRecognitionResponse recognizeEquipmentFromImageUrl(String imageUrl) {
        try {
            // Create content parts for the message
            ChatCompletionContentPart textPart = ChatCompletionContentPart.ofText(
                ChatCompletionContentPartText.builder()
                    .text("Please analyze this gym equipment image and provide a structured response with the following fields:\n" +
                          "- equipment_name: The name of the equipment\n" +
                          "- category: The category of equipment (e.g., cardio, strength, etc.)\n" +
                          "- possible_exercises: List of possible exercises that can be performed with the equipment, including name, description and primary muscles worked\n" +
                          "- description: A brief description of the equipment\n\n" +
                          "Format the response as a valid JSON object.")
                    .build());

            ChatCompletionContentPart imagePart = ChatCompletionContentPart.ofImageUrl(
                ChatCompletionContentPartImage.builder()
                    .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder()
                        .url(imageUrl)
                        .build())
                    .build());

            // Define JSON schema for structured output
            JsonSchema.Schema schema = JsonSchema.Schema.builder()
                .putAdditionalProperty("type", JsonValue.from("object"))
                .putAdditionalProperty("properties", JsonValue.from(Map.of(
                    "equipment_name", Map.of("type", "string"),
                    "category", Map.of("type", "string"),
                    "possible_exercises", Map.of("type", "array", "items", Map.of("type", "object", "properties", Map.of(
                        "name", Map.of("type", "string"),
                        "description", Map.of("type", "string"),
                        "primary_muscles", Map.of("type", "array", "items", Map.of("type", "string"))
                    ))),
                    "description", Map.of("type", "string")
                )))
                .build();

            // Create the chat completion request
            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1)
                .maxCompletionTokens(1000)
                .responseFormat(ResponseFormatJsonSchema.builder()
                    .jsonSchema(JsonSchema.builder()
                        .name("equipment-analysis")
                        .schema(schema)
                        .build())
                    .build())
                .addUserMessageOfArrayOfContentParts(List.of(textPart, imagePart))
                .build();

            // Make the API call
            String jsonResponse = client.chat().completions().create(params)
                .choices().get(0)
                .message().content()
                .orElseThrow(() -> new RuntimeException("No response content received"));
            System.out.println(
                    jsonResponse
            );
            // Parse the JSON response into EquipmentResponse object
            return mapper.readValue(jsonResponse, EquipmentRecognitionResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze equipment image", e);
        }
    }
}