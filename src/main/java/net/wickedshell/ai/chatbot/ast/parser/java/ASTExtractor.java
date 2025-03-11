package net.wickedshell.ai.chatbot.ast.parser.java;

import org.json.XML;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all functions for extracting Abstract Syntax Trees (ASTs) from java files.
 *
 * <p>This class is based on the ASTExtractor project available at:
 * <a href="https://github.com/thdiaman/ASTExtractor">https://github.com/thdiaman/ASTExtractor</a></p>
 * <p>It has been modified to work with the Chatbot project.</p>
 *
 * @author themis
 */
public class ASTExtractor {

    private static final int INDENTION = 0;

    /**
     * Parses all the files of a folder and returns a unified AST.
     *
     * @param folderName the path of the folder of which the files are parsed.
     * @return an AST containing all the files of a folder in XML or JSON format.
     */
    public static List<SourceFileAbstraction> parseFolder(String folderName) {
        String folderAbsolutePath = new File(folderName).getAbsolutePath();
        List<File> files = FileSystemHelpers.getJavaFilesOfFolderRecursively(folderName);
        List<SourceFileAbstraction> results = new ArrayList<>();
        for (File file : files) {
            String fileAbsolutePath = file.getAbsolutePath();
            String sourceCode = FileSystemHelpers.readFileToString(fileAbsolutePath);
            if(sourceCode != null) {
                String filePath = FileSystemHelpers.getRelativePath(folderAbsolutePath, fileAbsolutePath);
                String astSourceCode = JavaASTParser.parse(sourceCode);
                results.add(new SourceFileAbstraction(Path.of(filePath), removeLinebreaks(wrapASTSourceCode(filePath, astSourceCode)), removeLinebreaks(sourceCode)));
            }
        }
        return results;
    }

    private static String removeLinebreaks(String text) {
        return text.replace(System.lineSeparator(), "");
    }

    private static String wrapASTSourceCode(String filePath, String astSourceCode) {
        return XML.toJSONObject("<file>\n<path>" + filePath + "</path>\n<ast>\n" + astSourceCode + "</ast>\n</file>\n").toString(INDENTION);
    }

    public record SourceFileAbstraction(Path path, String astSourceCode, String sourceCode) {
    }
}