package net.wickedshell.ai.chatbot.ast.qdrant.markdown;

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import net.wickedshell.ai.chatbot.core.LLMProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class QdrantStorageMarkdownInitializer {

    public static final String KEY_PATH = "path";
    public static final String KEY_HEADING = "heading";
    public static final String KEY_CHAPTER_CONTENT = "chapter_content";
    private static final Logger LOG = LoggerFactory.getLogger(QdrantStorageMarkdownInitializer.class);
    private final QdrantEmbeddingRepository repository;
    private final Path sourceFolder;

    public QdrantStorageMarkdownInitializer(Path sourceFolder) {
        this.sourceFolder = sourceFolder;
        repository = new QdrantEmbeddingRepository(LLMProfile.MISTRAL);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        new QdrantStorageMarkdownInitializer(Path.of("<path-to-root-folder>")).initialize();
    }

    private void initialize() throws IOException, InterruptedException, ExecutionException {
        repository.reset();
        findMarkdownFiles(sourceFolder).forEach(this::addMarkdownSectionsFor);
    }

    private void addMarkdownSectionsFor(Path markdownFile) {
        LOG.info("Processing File: {}...", markdownFile.toAbsolutePath());
        try {
            Parser parser = Parser.builder().build();
            Document markdownDocument = parser.parse(Files.readString(markdownFile));
            getHeadingsRecursively(markdownDocument).forEach(heading -> addChapter(heading, markdownFile));
            LOG.error("Done");
        } catch (IOException exception) {
            LOG.error("Error reading file: {}", markdownFile);
            throw new RuntimeException(exception);
        }
    }

    private void addChapter(Heading heading, Path markdownFile) {
        LOG.info("Add Chapter: {}...", heading.getChars());
        List<Paragraph> paragraphsForHeading = getParagraphsForHeading(heading);
        String chapterContent = toString(heading, paragraphsForHeading);
        paragraphsForHeading.forEach(paragraph -> addParagraph(paragraph, heading, markdownFile, chapterContent));
        LOG.info("Added {} Paragraphs for Chapter: {}...", paragraphsForHeading.size(), heading.getChars());
    }

    private void addParagraph(Paragraph paragraph, Heading heading, Path markdownFile, String chapterContent) {
        Map<String, String> metadata = Map.of(
                KEY_PATH, markdownFile.toString(),
                KEY_HEADING, heading.getChars().toString(),
                KEY_CHAPTER_CONTENT, chapterContent);
        repository.add(paragraph.getChars().toString(), metadata);
    }

    private String toString(Heading heading, List<Paragraph> paragraphs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(heading.getChars()).append("\n");
        for (Paragraph paragraph : paragraphs) {
            stringBuilder.append(paragraph.getChars()).append("\n");
        }
        return stringBuilder.toString();
    }

    private List<Heading> getHeadingsRecursively(Node node) {
        List<Heading> headingNodes = new ArrayList<>();
        if (node instanceof Heading heading) {
            headingNodes.add(heading);
        }
        for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
            headingNodes.addAll(getHeadingsRecursively(child));
        }
        return headingNodes;
    }

    private List<Paragraph> getParagraphsForHeading(Heading heading) {
        List<Paragraph> paragraphNodes = new ArrayList<>();
        if (heading.getNext() != null && !(heading.getNext() instanceof Heading)) {
            paragraphNodes.addAll(getParagraphsRecursively(heading.getNext()));
        }
        return paragraphNodes;
    }

    private List<Paragraph> getParagraphsRecursively(Node node) {
        List<Paragraph> paragraphNodes = new ArrayList<>();
        if (node instanceof Paragraph paragraph) {
            paragraphNodes.add(paragraph);
        }
        if (node != null && !(node.getNext() instanceof Heading)) {
            paragraphNodes.addAll(getParagraphsRecursively(node.getNext()));
        }
        return paragraphNodes;
    }

    private List<Path> findMarkdownFiles(Path sourceFolder) throws IOException {
        // Recursively inspects the source folder and returns a list of all markdown files.
        try (Stream<Path> paths = Files.walk(sourceFolder)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".md"))
                    .toList();
        }
    }


}