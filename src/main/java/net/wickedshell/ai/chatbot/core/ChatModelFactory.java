package net.wickedshell.ai.chatbot.core;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;

import java.time.Duration;

import static net.wickedshell.ai.chatbot.core.ConnectionProperties.*;

public class ChatModelFactory {

    private ChatModelFactory() {
        // private constructor to avoid instantiation
    }

    public static StreamingChatLanguageModel create(String modelName) {
        return OllamaStreamingChatModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .temperature(TEMPERATURE)
                .build();
    }
}
