package net.wickedshell.ai.chatbot.ast;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import net.wickedshell.ai.chatbot.ast.qdrant.java.QdrantStorageJavaInitializer;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import java.util.List;

import static java.lang.System.out;
import static net.wickedshell.ai.chatbot.core.LLMProfile.CODELLAMA;

public class JavaASTConsoleChatBot extends ConsoleChatBot {

    private final QdrantEmbeddingRepository repository;

    protected JavaASTConsoleChatBot() {
        super(CODELLAMA);
        this.repository = new QdrantEmbeddingRepository(this.llmProfile);
    }

    public static void main(String[] args) {
        new JavaASTConsoleChatBot().run();
    }

    @Override
    protected TokenStream chat(StreamingChatBot chatBot, String input) {
        List<EmbeddingMatch<TextSegment>> results = repository.searchSimilar(input, 8);

        StringBuilder contextBuilder = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : results) {
            Metadata metadata = match.embedded().metadata();
            contextBuilder.append("Code: ").append(metadata.getString(QdrantStorageJavaInitializer.KEY_SOURCE_CODE)).append("\n\n");
            contextBuilder.append("Path: ").append(metadata.getString(QdrantStorageJavaInitializer.KEY_PATH)).append("\n\n");
            out.println("...consider file: " + metadata.getString(QdrantStorageJavaInitializer.KEY_PATH) + " | Score: " + match.score());
        }
        String context = contextBuilder.toString();


        String prompt = """
                %s
                
                You are an expert Java programmer that writes simple, concise code and explanations.
                Don't make up code, refer the given Java code only.
                
                %s
                """.formatted(context, input);

        return chatBot.chat(prompt);
    }
}
