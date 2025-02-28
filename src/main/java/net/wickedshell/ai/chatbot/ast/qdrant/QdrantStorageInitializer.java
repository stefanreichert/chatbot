package net.wickedshell.ai.chatbot.ast.qdrant;

import net.wickedshell.ai.chatbot.ast.parser.ASTExtractor;
import net.wickedshell.ai.chatbot.ast.parser.ASTExtractor.SourceFileAbstraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingsRepository.add;
import static net.wickedshell.ai.chatbot.ast.qdrant.QdrantEmbeddingsRepository.reset;

public class QdrantStorageInitializer {

    public static final String KEY_PATH = "path";
    public static final String KEY_SOURCE_CODE = "source_code";
    private static final Logger LOG = LoggerFactory.getLogger(QdrantStorageInitializer.class);
    private final Path path;

    public QdrantStorageInitializer(Path sourceDirectory) {
        this.path = sourceDirectory;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new QdrantStorageInitializer(Path.of("/Users/stefan/Repositories/private/twodfourn/src/main/java")).initializeWith();
    }

    private void initializeWith() throws InterruptedException, ExecutionException {
        reset();
        List<SourceFileAbstraction> sourceFiles = ASTExtractor.parseFolder(path.toString());
        for (SourceFileAbstraction sourceFile : sourceFiles) {
            LOG.info("Storing: {}...", sourceFile.path());
            Map<String, String> metadata = Map.of(KEY_PATH, sourceFile.path().toString(), KEY_SOURCE_CODE, sourceFile.sourceCode());
            add(sourceFile.astSourceCode(), metadata);
            LOG.info("done");
        }
    }
}