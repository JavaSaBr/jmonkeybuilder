package com.ss.editor.ui.component.scripting;

import static java.util.Collections.singleton;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.layout.VBox;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The groovy editor component.
 *
 * @author JavaSaBr
 */
public class GroovyEditorComponent extends VBox {

    private static final String[] KEYWORDS = new String[] {
            "as", "assert", "break", "case", "catch",
            "class", "const", "continue", "def", "default",
            "do", "else", "enum", "extends", "false",
            "finally", "for", "goto", "if", "implement",
            "import", "in", "instanceof", "interface", "new",
            "null", "package", "return", "super", "switch",
            "this", "throw", "throws", "trait", "true",
            "try", "while"
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
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

        spansBuilder.add(singleton("plain-code"), text.length() - lastKwEnd);

        return spansBuilder.create();
    }

    /**
     * The code area.
     */
    @NotNull
    private final CodeArea codeArea;

    /**
     * Instantiates a new Groovy editor component.
     *
     * @param editable the editable
     */
    public GroovyEditorComponent(final boolean editable) {

        codeArea = new CodeArea();
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> codeArea.setStyleSpans(0, getStyleSpans(codeArea.getText())));
        codeArea.prefHeightProperty().bind(heightProperty());
        codeArea.prefWidthProperty().bind(widthProperty());
        codeArea.setEditable(editable);

        FXUtils.addToPane(codeArea, this);
        FXUtils.addClassesTo(this, CSSClasses.TEXT_EDITOR_TEXT_AREA, CSSClasses.GROOVY_EDITOR_COMPONENT);
    }

    @NotNull
    private StyleSpans<? extends Collection<String>> getStyleSpans(@NotNull final String text) {
        return computeHighlighting(text);
    }

    /**
     * Gets code.
     *
     * @return the result code.
     */
    @NotNull
    public String getCode() {
        return codeArea.getText();
    }

    /**
     * Set the new code.
     *
     * @param code the new code.
     */
    public void setCode(@NotNull final String code) {
        codeArea.clear();
        codeArea.appendText(code);
    }
}
