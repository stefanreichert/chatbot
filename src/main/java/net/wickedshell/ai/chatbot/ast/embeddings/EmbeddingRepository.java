package net.wickedshell.ai.chatbot.ast.embeddings;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;

import java.time.Duration;
import java.util.List;

import static net.wickedshell.ai.chatbot.core.Constants.*;

public class EmbeddingRepository {

    private static final EmbeddingModel EMBEDDING_MODEL = OllamaEmbeddingModel.builder()
            .baseUrl(OLLAMA_URL)
            .modelName(CODELLAMA_MODEL)
            .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

    private EmbeddingRepository() {
        // private constructor to avoid instantiation
    }

    public static List<Float> getEmbeddingsFor(String text) {
        return EMBEDDING_MODEL.embed(text).content().vectorAsList();
    }
}
