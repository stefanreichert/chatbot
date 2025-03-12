package net.wickedshell.ai.chatbot.ast;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import net.wickedshell.ai.chatbot.ast.qdrant.markdown.QdrantStorageMarkdownInitializer;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import java.util.List;

import static java.lang.System.out;
import static net.wickedshell.ai.chatbot.core.LLMProfile.MISTRAL;

public class MarkdownASTConsoleChatBot extends ConsoleChatBot {

    private final QdrantEmbeddingRepository repository;

    protected MarkdownASTConsoleChatBot() {
        super(MISTRAL);
        this.repository = new QdrantEmbeddingRepository(this.llmProfile);
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
            contextBuilder.append(metadata.getString(QdrantStorageMarkdownInitializer.KEY_CONTENT)).append("\n\n");
            out.println("...considered section " + metadata.getString(QdrantStorageMarkdownInitializer.KEY_HEADING) + " in  file: " + metadata.getString(QdrantStorageMarkdownInitializer.KEY_PATH) + " | Score: " + match.score());
        }

        String prompt = """
                %s
                You are a senior Software Architect that is responsible and accountable for the architecture development, decisions and design of a system.
                Don't make up any facts, refer the Software Architecture Document content given below only. Be precise and concise.
                If you don't know the answer say so and ask questions to get necessary information.
                
                %s
                """.formatted(contextBuilder.toString(), input);

        return chatBot.chat(prompt);
    }
}
