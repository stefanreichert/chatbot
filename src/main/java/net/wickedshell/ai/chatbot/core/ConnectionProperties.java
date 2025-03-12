package net.wickedshell.ai.chatbot.core;

public class ConnectionProperties {

    // Ollama connection settings
    public static final String OLLAMA_URL = "http://localhost:11434";
    public static final int TIMEOUT_SECONDS = 600;
    public static final double TEMPERATURE = 0.01;

    // qdrant connection settings
    public static final String QDRANT_HOST = "localhost";
    public static final int QDRANT_PORT = 6334;

    private ConnectionProperties() {
        // private constructor to avoid instantiation
    }

}
