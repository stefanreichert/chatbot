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

import static net.wickedshell.ai.chatbot.ast.embeddings.EmbeddingFactory.createEmbeddingFor;
import static net.wickedshell.ai.chatbot.core.Constants.*;

public class QdrantEmbeddingRepository {

    private final String embeddingsCollectionName;
    private final String modelName;
    private final QdrantEmbeddingStore embeddingStore;
    private final int vectorSize;

    public QdrantEmbeddingRepository(String modelName, String embeddingsCollectionName, int vectorSize) {
        this.modelName = modelName;
        this.embeddingsCollectionName = embeddingsCollectionName;
        embeddingStore = QdrantEmbeddingStore.builder()
                .host(QDRANT_HOST)
                .port(QDRANT_PORT)
                .collectionName(embeddingsCollectionName)
                .build();
        this.vectorSize = vectorSize;
    }

    public List<EmbeddingMatch<TextSegment>> searchSimilar(String query, int maxResultCount) {
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(createEmbeddingFor(query, modelName))
                .maxResults(maxResultCount)
                .minScore(0.5)
                .build();
        return embeddingStore.search(request).matches();
    }

    public void add(String text, Map<String, String> metadata) {
        List<Float> vector = createEmbeddingFor(text, modelName).vectorAsList();
        embeddingStore.add(Embedding.from(vector), TextSegment.textSegment(text, Metadata.from(metadata)));
    }

    public void reset() throws ExecutionException, InterruptedException {
        Collections.DeleteCollection deleteEmbeddings = Collections.DeleteCollection.newBuilder().setCollectionName(embeddingsCollectionName).build();
        Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(vectorSize)
                .build();
        Collections.VectorsConfig vectorConfig = Collections.VectorsConfig.newBuilder()
                .setParams(vectorParams)
                .build();
        Collections.CreateCollection createEmbeddings = Collections.CreateCollection.newBuilder()
                .setCollectionName(embeddingsCollectionName)
                .setVectorsConfig(vectorConfig).build();
        try (QdrantGrpcClient grpcClient = QdrantGrpcClient
                .newBuilder(QDRANT_HOST, QDRANT_PORT, false)
                .build()) {
            grpcClient.collections().delete(deleteEmbeddings).get();
            grpcClient.collections().create(createEmbeddings).get();
        }
    }
}