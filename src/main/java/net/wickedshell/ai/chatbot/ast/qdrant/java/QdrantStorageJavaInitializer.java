package net.wickedshell.ai.chatbot.ast.qdrant.java;

import net.wickedshell.ai.chatbot.ast.parser.java.ASTExtractor;
import net.wickedshell.ai.chatbot.ast.parser.java.ASTExtractor.SourceFileAbstraction;
import net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.wickedshell.ai.chatbot.core.Constants.*;

public class QdrantStorageJavaInitializer {

    public static final String KEY_PATH = "path";
    public static final String KEY_SOURCE_CODE = "source_code";
    private static final Logger LOG = LoggerFactory.getLogger(QdrantStorageJavaInitializer.class);
    private final QdrantEmbeddingRepository repository;
    private final Path sourceFolder;

    public QdrantStorageJavaInitializer(Path sourceFolder) {
        this.sourceFolder = sourceFolder;
        repository = new QdrantEmbeddingRepository(CODELLAMA_MODEL, EMBEDDINGS_COLLECTION_NAME_JAVA, VECTOR_SIZE_OLLAMA);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new QdrantStorageJavaInitializer(Path.of("<path-to-root-folder>")).initialize();
    }

    private void initialize() throws InterruptedException, ExecutionException {
        repository.reset();
        List<SourceFileAbstraction> sourceFiles = ASTExtractor.parseFolder(sourceFolder.toString());
        for (SourceFileAbstraction sourceFile : sourceFiles) {
            LOG.info("Storing: {}...", sourceFile.path());
            Map<String, String> metadata = Map.of(KEY_PATH, sourceFile.path().toString(), KEY_SOURCE_CODE, sourceFile.sourceCode());
            repository.add(sourceFile.astSourceCode(), metadata);
            LOG.info("done");
        }
    }
}