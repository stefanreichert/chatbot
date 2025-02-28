package net.wickedshell.ai.chatbot.ast;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantStorageInitializer;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import java.util.List;

import static java.lang.System.out;
import static net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingsRepository.searchSimilar;
import static net.wickedshell.ai.chatbot.core.Constants.CODELLAMA_MODEL;

public class ASTConsoleChatBot extends ConsoleChatBot {

    protected ASTConsoleChatBot() {
        super(CODELLAMA_MODEL);
    }

    public static void main(String[] args) {
        new ASTConsoleChatBot().run();
    }

    @Override
    protected TokenStream chat(StreamingChatBot chatBot, String input) {
        List<EmbeddingMatch<TextSegment>> results = searchSimilar(input, 8);

        StringBuilder contextBuilder = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : results) {
            Metadata metadata = match.embedded().metadata();
            contextBuilder.append("Code: ").append(metadata.getString(QdrantStorageInitializer.KEY_SOURCE_CODE)).append("\n\n");
            contextBuilder.append("Path: ").append(metadata.getString(QdrantStorageInitializer.KEY_PATH)).append("\n\n");
            out.println("...consider file: " + metadata.getString(QdrantStorageInitializer.KEY_PATH) + " | Score: " + match.score());
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
