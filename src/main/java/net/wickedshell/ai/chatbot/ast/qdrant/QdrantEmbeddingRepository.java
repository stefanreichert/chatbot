package net.wickedshell.ai.chatbot.ast.qdrant;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import net.wickedshell.ai.chatbot.core.LLMProfile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.wickedshell.ai.chatbot.ast.embeddings.EmbeddingFactory.createEmbeddingFor;
import static net.wickedshell.ai.chatbot.core.ConnectionProperties.*;

public class QdrantEmbeddingRepository {

    private final QdrantEmbeddingStore embeddingStore;
    private final LLMProfile llmProfile;

    public QdrantEmbeddingRepository(LLMProfile llmProfile) {
        this.llmProfile = llmProfile;
        embeddingStore = QdrantEmbeddingStore.builder()
                .host(QDRANT_HOST)
                .port(QDRANT_PORT)
                .collectionName(llmProfile.getCollectionName())
                .build();
    }

    public List<EmbeddingMatch<TextSegment>> searchSimilar(String query, int maxResultCount) {
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(createEmbeddingFor(query, llmProfile.getModelName()))
                .maxResults(maxResultCount)
                .minScore(0.5)
                .build();
        return embeddingStore.search(request).matches();
    }

    public void add(String text, Map<String, String> metadata) {
        List<Float> vector = createEmbeddingFor(text, llmProfile.getModelName()).vectorAsList();
        embeddingStore.add(Embedding.from(vector), TextSegment.textSegment(text, Metadata.from(metadata)));
    }

    public void reset() throws ExecutionException, InterruptedException {
        Collections.DeleteCollection deleteEmbeddings = Collections.DeleteCollection.newBuilder().setCollectionName(llmProfile.getCollectionName()).build();
        Collections.VectorParams vectorParams = Collections.VectorParams.newBuilder()
                .setDistance(Collections.Distance.Cosine)
                .setSize(llmProfile.getVectorSize())
                .build();
        Collections.VectorsConfig vectorConfig = Collections.VectorsConfig.newBuilder()
                .setParams(vectorParams)
                .build();
        Collections.CreateCollection createEmbeddings = Collections.CreateCollection.newBuilder()
                .setCollectionName(llmProfile.getCollectionName())
                .setVectorsConfig(vectorConfig).build();
        try (QdrantGrpcClient grpcClient = QdrantGrpcClient
                .newBuilder(QDRANT_HOST, QDRANT_PORT, false)
                .build()) {
            grpcClient.collections().delete(deleteEmbeddings).get();
            grpcClient.collections().create(createEmbeddings).get();
        }
    }
}