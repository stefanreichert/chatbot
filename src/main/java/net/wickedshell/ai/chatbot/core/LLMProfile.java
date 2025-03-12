package net.wickedshell.ai.chatbot.core;

public enum LLMProfile {

    MISTRAL("mistral", "markdown", 4096),
    CODELLAMA("codellama:13b", "java", 5120);

    private final String modelName;
    private final String embeddingCollectionName;
    private final int vectorSize;

    LLMProfile(String modelName, String embeddingCollectionName, int vectorSize) {
        this.modelName = modelName;
        this.embeddingCollectionName = embeddingCollectionName;
        this.vectorSize = vectorSize;
    }

    public String getModelName() {
        return modelName;
    }

    public String getEmbeddingCollectionName() {
        return embeddingCollectionName;
    }

    public int getVectorSize() {
        return vectorSize;
    }
}
