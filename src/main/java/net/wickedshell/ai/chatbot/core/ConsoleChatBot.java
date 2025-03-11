package net.wickedshell.ai.chatbot.core;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;

import java.util.Scanner;

import static java.lang.System.*;

public abstract class ConsoleChatBot {

    private static final String COMMAND_EXIT = "exit";
    private static final String COMMAND_CLEAR = "clear";

    private static final String PREFIX_YOU = "You: ";
    private static final String PREFIX_BOT = "Bot:";
    private static final String PREFIX_ERROR = "Error: ";

    private final StreamingChatBot chatBot;
    private final String modelName;
    private ChatMemory chatMemory;

    protected ConsoleChatBot(String modelName) {
        this.modelName = modelName;
        this.chatBot = AiServices.builder(StreamingChatBot.class)
                .streamingChatLanguageModel(ChatModelFactory.create(modelName))
                .build();
    }

    protected ConsoleChatBot(String modelName, ChatMemory chatMemory) {
        this.modelName = modelName;
        this.chatBot = AiServices.builder(StreamingChatBot.class)
                .streamingChatLanguageModel(ChatModelFactory.create(modelName))
                .chatMemory(chatMemory)
                .build();
    }

    public void run() {
        Scanner scanner = new Scanner(in);

        out.print("Welcome to the AI chatbot! Type 'exit' to quit.");
        if(hasMemoryWindow()) {
            out.print(" Type 'clear' to clear the chat memory.");
        }
        out.println();
        printBotSuffix(null);

        while (true) {
            String input = scanner.nextLine();
            if (COMMAND_EXIT.equalsIgnoreCase(input)) {
                break;
            }
            if (hasMemoryWindow() && COMMAND_CLEAR.equalsIgnoreCase(input)) {
                chatMemory.clear();
                printBotSuffix(null);
            } else {
                handleWithChatBot(input);
            }
        }
        exit(0);
    }

    private boolean hasMemoryWindow() {
        return chatMemory != null;
    }

    protected String getModelName() {
        return modelName;
    }

    protected abstract TokenStream chat(StreamingChatBot chatBot, String input);

    private void handleWithChatBot(String input) {
        printBotPrefix();

        try {
            TokenStream response = chat(chatBot, input);
            response
                    .onPartialResponse(out::print)
                    .onCompleteResponse(this::printBotSuffix)
                    .onError(this::printBotErrorSuffix)
                    .start();
        } catch (Exception exception) {
            printBotErrorSuffix(exception);
        }
    }

    private void printBotSuffix(ChatResponse chatResponse) {
        out.println();
        out.println();
        out.print(PREFIX_YOU);
    }

    private void printBotPrefix() {
        out.println(PREFIX_BOT);
    }

    private void printBotErrorSuffix(Throwable throwable) {
        out.println();
        out.println(PREFIX_ERROR + throwable.getMessage());
        out.println();
    }
}
