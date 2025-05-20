package net.wickedshell.ai.chatbot.core;

public enum LLMProfile {

    LLAMA3("llama3", "markdown", 8192),
    MISTRAL("mistral", "markdown", 4096),
    CODELLAMA("codellama", "java", 4096),
    PHI3("phi3", "markdown", 4096),
    GEMMA("gemma", "markdown", 8192);

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
