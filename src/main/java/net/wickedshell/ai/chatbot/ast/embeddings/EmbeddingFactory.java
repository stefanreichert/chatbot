package net.wickedshell.ai.chatbot.ast.embeddings;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.jspecify.annotations.NonNull;

import java.time.Duration;

import static net.wickedshell.ai.chatbot.core.Constants.OLLAMA_URL;
import static net.wickedshell.ai.chatbot.core.Constants.TIMEOUT_SECONDS;

public class EmbeddingFactory {
    private EmbeddingFactory() {
        // private constructor to avoid instantiation
    }

    public static @NonNull Embedding createEmbeddingFor(String text, String modelName) {
        OllamaEmbeddingModel embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .build();
        return embeddingModel.embed(text).content();
    }
}
