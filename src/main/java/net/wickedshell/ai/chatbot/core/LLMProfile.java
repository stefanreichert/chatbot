package net.wickedshell.ai.chatbot.core;

public enum LLMProfile {

    MISTRAL("mistral", "markdown", 4096),
    CODELLAMA("codellama:13b", "java", 5120);

    private final String modelName;
    private final String collectionName;
    private final int vectorSize;

    LLMProfile(String modelName, String collectionName, int vectorSize) {
        this.modelName = modelName;
        this.collectionName = collectionName;
        this.vectorSize = vectorSize;
    }

    public String getModelName() {
        return modelName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public int getVectorSize() {
        return vectorSize;
    }
}
