package net.wickedshell.ai.chatbot;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

import java.time.Duration;
import java.util.Scanner;

public class OllamaChat {

    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final String MODEL_NAME = "mistral";

    private static final int TIMEOUT_SECONDS = 60;
    private static final double TEMPERATURE = 0.01;
    private static final int MESSAGE_WINDOW = 20;

    public static final String EXIT = "exit";

    public static final String PREFIX_YOU = "You: ";
    public static final String PREFIX_BOT = "Bot:";
    public static final String PREFIX_ERROR = "Error: ";

    public static void main(String[] args) {
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(MESSAGE_WINDOW)
                .chatMemoryStore(new InMemoryChatMemoryStore())
                .build();

        OllamaStreamingChatModel model = OllamaStreamingChatModel.builder()
                .baseUrl(OLLAMA_URL)
                .modelName(MODEL_NAME)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .temperature(TEMPERATURE)
                .build();

        ChatBot chatBot = AiServices.builder(ChatBot.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory) // Attach memory
                .build();

        runOnConsole(chatBot);
    }

    private static void runOnConsole(ChatBot chatBot) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the AI chatbot! Type 'exit' to quit.");
        printBotSuffix(null);
        while (true) {
            String input = scanner.nextLine();
            if (EXIT.equalsIgnoreCase(input)) {
                break;
            }
            printBotPrefix();

            try {
                TokenStream response = chatBot.chat(input);
                response
                        .onNext(System.out::print)
                        .onComplete(OllamaChat::printBotSuffix)
                        .onError(OllamaChat::printBotErrorSuffix)
                        .start();
            } catch (Throwable throwable) {
                printBotErrorSuffix(throwable);
            }
        }
        System.exit(0);
    }

    private interface ChatBot {
        TokenStream chat(String message);
    }

    private static void printBotPrefix(){
        System.out.println(PREFIX_BOT);
    }

    private static void printBotSuffix(Response<AiMessage> aiMessageResponse){
        System.out.println();
        System.out.println();
        System.out.print(PREFIX_YOU);
    }

    private static void printBotErrorSuffix(Throwable throwable){
        System.out.println();
        System.out.println(PREFIX_ERROR + throwable.getMessage());
        System.out.println();
    }

}