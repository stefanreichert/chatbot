package net.wickedshell.ai.chatbot.basic;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import static net.wickedshell.ai.chatbot.core.ConnectionProperties.MISTRAL_MODEL;

public class BasicConsoleChatBot extends ConsoleChatBot {
    private static final int MESSAGE_WINDOW = 20;

    public BasicConsoleChatBot(ChatMemory chatMemory) {
        super(MISTRAL_MODEL, chatMemory);
    }

    public static void main(String[] args) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(MESSAGE_WINDOW)
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .build();

        new BasicConsoleChatBot(chatMemory).run();
    }


    @Override
    protected TokenStream chat(StreamingChatBot chatBot, String input) {
        return chatBot.chat(input);
    }
}