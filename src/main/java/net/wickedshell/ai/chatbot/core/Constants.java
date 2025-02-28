package net.wickedshell.ai.chatbot.core;

public class Constants {

    public static final String OLLAMA_URL = "http://localhost:11434";
    public static final String MISTRAL_MODEL = "mistral";
    public static final String CODELLAMA_MODEL = "codellama:13b";
    public static final String QDRANT_HOST = "localhost";
    public static final int QDRANT_PORT = 6334;
    public static final int TIMEOUT_SECONDS = 600;
    public static final double TEMPERATURE = 0.01;
    private Constants() {
        // private constructor to avoid instantiation
    }
}
