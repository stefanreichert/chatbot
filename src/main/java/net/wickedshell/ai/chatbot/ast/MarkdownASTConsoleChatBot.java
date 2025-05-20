package net.wickedshell.ai.chatbot.ast;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import net.wickedshell.ai.chatbot.core.ConsoleChatBot;
import net.wickedshell.ai.chatbot.core.StreamingChatBot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.out;
import static net.wickedshell.ai.chatbot.ast.qdrant.markdown.QdrantStorageMarkdownInitializer.*;
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

        Map<String, EmbeddingMatch<TextSegment>> chapters = new HashMap<>();
        for (EmbeddingMatch<TextSegment> result : results) {
            chapters.put(result.embedded().metadata().getString(KEY_HEADING), result);
        }
        out.println("...found " + chapters.size() + " relevant chapters.");

        StringBuilder contextBuilder = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : chapters.values()) {
            Metadata metadata = match.embedded().metadata();
            contextBuilder.append(match.embedded().metadata().getString(KEY_CHAPTER_CONTENT));
            out.println("...considered chapter " + metadata.getString(KEY_HEADING) + " in file: " + metadata.getString(KEY_PATH));
        }

        String prompt = """
                %s
                You are a senior Software Architect that is responsible and accountable for the architecture development, decisions and design of a system.
                The relevant paragraphs of the Software Architecture Document are listed above. Your answer must be based on these paragraphs. Don't make up any facts, refer the Software Architecture Document content given below only. Be precise and concise.
                If you don't know the answer say so and ask questions to get necessary information. If you need more information, ask for it. If you need to make assumptions, state them clearly.
                The term ADR stands for Architectural Decision Record. The term QS stands for Quality Scenario. Constraints are identified by a 4 letter code starting with C. 
                Please provide your answer to the following question:
                %s
                """.formatted(contextBuilder.toString(), input);
        out.println("...prompt used: ");
        out.println(prompt);
        out.println();

        return chatBot.chat(prompt);
    }
}
