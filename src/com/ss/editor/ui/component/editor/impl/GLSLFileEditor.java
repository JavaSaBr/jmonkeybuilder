package com.ss.editor.ui.component.editor.impl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.editor.EditorDescription;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.undo.UndoManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.Util;

/**
 * The implementation of editor for editing GLSL files.
 *
 * @author JavaSaBr
 */
public class GLSLFileEditor extends AbstractFileEditor<VBox> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(GLSLFileEditor::new);
        DESCRIPTION.setEditorName(Messages.GLSL_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(GLSLFileEditor.class.getName());
        DESCRIPTION.addExtension(FileExtensions.GLSL_FRAGMENT);
        DESCRIPTION.addExtension(FileExtensions.GLSL_VERTEX);
    }

    private static final String[] KEYWORDS = new String[]{
            "define", "undef", "if", "ifdef", "ifndef",
            "else", "elif", "endif", "error", "pragma",
            "extension", "version", "line", "attribute", "const",
            "uniform", "varying", "layout", "centroid", "flat",
            "smooth", "noperspective", "patch", "sample", "break",
            "continue", "do", "for", "while", "switch",
            "case", "default", "if", "subroutine", "in", "out", "inout",
            "void", "true", "false", "invariant", "discard", "return", "struct"
    };

    private static final String[] VALUE_TYPES = new String[]{
            "float", "double", "int", "bool", "mat2", "mat3", "mat4", "uint", "uvec2", "uvec3", "uvec4",
            "sampler1D", "sampler2D", "sampler3D", "samplerCube", "vec2", "vec3", "vec4"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String VALUE_TYPE_PATTERN = "\\b(" + String.join("|", VALUE_TYPES) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<VALUETYPE>" + VALUE_TYPE_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static StyleSpans<Collection<String>> computeHighlighting(final String text) {

        final Matcher matcher = PATTERN.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastKwEnd = 0;

        while (matcher.find()) {

            String styleClass = matcher.group("KEYWORD") != null ? "keyword" : null;

            if (styleClass == null) {
                styleClass = matcher.group("VALUETYPE") != null ? "value-type" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("PAREN") != null ? "paren" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("BRACE") != null ? "brace" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("BRACKET") != null ? "bracket" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("SEMICOLON") != null ? "semicolon" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("STRING") != null ? "string" : null;
            }

            if (styleClass == null) {
                styleClass = matcher.group("COMMENT") != null ? "comment" : null;
            }

            assert styleClass != null;

            spansBuilder.add(singleton("plain-code"), matcher.start() - lastKwEnd);
            spansBuilder.add(singleton(styleClass), matcher.end() - matcher.start());

            lastKwEnd = matcher.end();
        }

        spansBuilder.add(emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }

    /**
     * The original content of the opened file.
     */
    private String originalContent;

    /**
     * The code area.
     */
    private CodeArea codeArea;

    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(final VBox root) {

        codeArea = new CodeArea();
        codeArea.setId(CSSIds.TEXT_EDITOR_TEXT_AREA);
        codeArea.richChanges().subscribe(change -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> updateDirty(newValue));
        codeArea.setStyle("-fx-stroke: white;");
        codeArea.prefHeightProperty().bind(root.heightProperty());
        codeArea.prefWidthProperty().bind(root.widthProperty());
        //codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        FXUtils.addToPane(codeArea, root);
        FXUtils.addClassTo(codeArea, CSSClasses.MAIN_FONT_13);
    }

    /**
     * Обновление состояния измененности.
     */
    private void updateDirty(final String newContent) {
        setDirty(!getOriginalContent().equals(newContent));
    }

    @Override
    protected boolean needToolbar() {
        return true;
    }

    @Override
    protected void createToolbar(final HBox container) {
        super.createToolbar(container);
        FXUtils.addToPane(createSaveAction(), container);
    }

    /**
     * @return the code area.
     */
    private CodeArea getCodeArea() {
        return codeArea;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final byte[] content = Util.safeGet(file, Files::readAllBytes);

        setOriginalContent(new String(content));

        final CodeArea codeArea = getCodeArea();
        codeArea.appendText(getOriginalContent());

        final UndoManager undoManager = codeArea.getUndoManager();
        undoManager.forgetHistory();

        setOriginalContent(codeArea.getText());
        updateDirty(getOriginalContent());
    }

    /**
     * @return the original content of the opened file.
     */
    public String getOriginalContent() {
        return originalContent;
    }

    /**
     * @param originalContent the original content of the opened file.
     */
    public void setOriginalContent(final String originalContent) {
        this.originalContent = originalContent;
    }

    @Override
    public void doSave() {
        super.doSave();

        final CodeArea codeArea = getCodeArea();
        final String newContent = codeArea.getText();

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(getEditFile()))) {
            out.print(newContent);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        setOriginalContent(newContent);
        updateDirty(newContent);
        notifyFileChanged();
    }

    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
