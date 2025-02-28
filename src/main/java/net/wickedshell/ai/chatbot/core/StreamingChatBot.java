package net.wickedshell.ai.chatbot.core;

import dev.langchain4j.service.TokenStream;

public interface StreamingChatBot {
    TokenStream chat(String message);
}