package net.wickedshell.ai.chatbot.ast;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import net.wickedshell.ai.chatbot.ast.qdrant.java.QdrantStorageJavaInitializer;
import net.wickedshell.ai.chatbot.ast.qdrant.markdown.QdrantStorageMarkdownInitializer;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import java.util.List;

import static java.lang.System.out;
import static net.wickedshell.ai.chatbot.core.Constants.*;

public class MarkdownASTConsoleChatBot extends ConsoleChatBot {

    private final QdrantEmbeddingRepository repository;

    protected MarkdownASTConsoleChatBot() {
        super(MISTRAL_MODEL);
        this.repository = new QdrantEmbeddingRepository(getModelName(), EMBEDDINGS_COLLECTION_NAME_MARKDOWN, VECTOR_SIZE_MISTRAL);
    }

    public static void main(String[] args) {
        new MarkdownASTConsoleChatBot().run();
    }

    @Override
    protected TokenStream chat(StreamingChatBot chatBot, String input) {
        List<EmbeddingMatch<TextSegment>> results = repository.searchSimilar(input, 8);

        StringBuilder contextBuilder = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : results) {
            Metadata metadata = match.embedded().metadata();
            contextBuilder.append("Code: ").append(metadata.getString(QdrantStorageMarkdownInitializer.KEY_CONTENT)).append("\n\n");
            out.println("...consider section " + metadata.getString(QdrantStorageMarkdownInitializer.KEY_HEADING) + " in  file: " + metadata.getString(QdrantStorageMarkdownInitializer.KEY_PATH) + " | Score: " + match.score());
        }
        String context = contextBuilder.toString();


        String prompt = """
                %s
                You are a senior Software Architect that is responsible and accountable for the architecture of a system.
                Don't make up any facts, refer the Software Architecture Document content given below only. Be precise and concise.
                
                %s
                """.formatted(context, input);

        return chatBot.chat(prompt);
    }
}
