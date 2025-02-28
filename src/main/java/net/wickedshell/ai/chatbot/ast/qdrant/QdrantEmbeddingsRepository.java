package net.wickedshell.ai.chatbot.ast.qdrant;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.wickedshell.ai.chatbot.ast.embeddings.EmbeddingRepository.getEmbeddingsFor;
import static net.wickedshell.ai.chatbot.core.Constants.QDRANT_HOST;
import static net.wickedshell.ai.chatbot.core.Constants.QDRANT_PORT;

public class QdrantEmbeddingsRepository {

    private static final String COLLECTION_EMBEDDINGS = "embeddings";

    private static final QdrantEmbeddingStore EMBEDDING_STORE = QdrantEmbeddingStore.builder()
            .host(QDRANT_HOST)
            .port(QDRANT_PORT)
            .collectionName(COLLECTION_EMBEDDINGS)
            .build();

    private QdrantEmbeddingsRepository() {
        // private constructor to avoid instantiation
    }

    public static List<EmbeddingMatch<TextSegment>> searchSimilar(String query, int maxResultCount) {
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(Embedding.from(getEmbeddingsFor(query)))
                .maxResults(maxResultCount)
                .minScore(0.5)
                .build();
        return EMBEDDING_STORE.search(request).matches();
    }

    public static void add(String text, Map<String, String> metadata) {
        List<Float> vector = getEmbeddingsFor(text);
        EMBEDDING_STORE.add(Embedding.from(vector), TextSegment.textSegment(text, Metadata.from(metadata)));
    }

    public static void reset() throws ExecutionException, InterruptedException {
        Collections.DeleteCollection deleteEmbeddings = Collections.DeleteCollection.newBuilder().setCollectionName(COLLECTION_EMBEDDINGS).build();
        Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(5120)
                .build();
        Collections.VectorsConfig vectorConfig = Collections.VectorsConfig.newBuilder()
                .setParams(vectorParams)
                .build();
        Collections.CreateCollection createEmbeddings = Collections.CreateCollection.newBuilder()
                .setCollectionName(COLLECTION_EMBEDDINGS)
                .setVectorsConfig(vectorConfig).build();
        try (QdrantGrpcClient grpcClient = QdrantGrpcClient
                .newBuilder(QDRANT_HOST, QDRANT_PORT, false)
                .build()) {
            grpcClient.collections().delete(deleteEmbeddings).get();
            grpcClient.collections().create(createEmbeddings).get();
        }
    }
}