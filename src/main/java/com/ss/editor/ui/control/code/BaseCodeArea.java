package com.ss.editor.ui.control.code;

import static java.util.Collections.singleton;
import com.ss.editor.annotation.FXThread;
import com.ss.rlib.util.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.undo.UndoManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base implementation of code area control for this editor.
 *
 * @author JavaSaBr
 */
public class BaseCodeArea extends CodeArea {

    @NotNull
    protected static final String CLASS_KEYWORD = "KEYWORD";

    @NotNull
    protected static final String CLASS_VALUE_TYPE = "VALUETYPE";

    @NotNull
    protected static final String CLASS_VALUE_VALUE = "VALUEVALUE";

    @NotNull
    protected static final String CLASS_PAREN = "PAREN";

    @NotNull
    protected static final String CLASS_BRACE = "BRACE";

    @NotNull
    protected static final String CLASS_BRACKET = "BRACKET";

    @NotNull
    protected static final String CLASS_SEMICOLON = "SEMICOLON";

    @NotNull
    protected static final String CLASS_STRING = "STRING";

    @NotNull
    protected static final String CLASS_COMMENT = "COMMENT";

    @NotNull
    protected static final String[][] AVAILABLE_CLASSES = {
            {CLASS_KEYWORD, "keyword"},
            {CLASS_VALUE_TYPE, "value-type"},
            {CLASS_VALUE_VALUE, "value-value"},
            {CLASS_PAREN, "paren"},
            {CLASS_BRACE, "brace"},
            {CLASS_BRACKET, "bracket"},
            {CLASS_SEMICOLON, "semicolon"},
            {CLASS_STRING, "string"},
            {CLASS_COMMENT, "comment"},
    };

    protected static final String PAREN_PATTERN = "\\(|\\)";
    protected static final String BRACE_PATTERN = "\\{|\\}";
    protected static final String BRACKET_PATTERN = "\\[|\\]";
    protected static final String SEMICOLON_PATTERN = "\\;";
    protected static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    protected static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    public BaseCodeArea() {
        richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> setStyleSpans(0, calculateStyleSpans(getText())));
    }

    /**
     * Calculate highlighting for the code.
     *
     * @param pattern the pattern.
     * @param text    the text.
     * @return the highlight styles.
     */
    @FXThread
    protected @NotNull StyleSpans<Collection<String>> computeHighlighting(@NotNull final Pattern pattern,
                                                                          @NotNull final String text) {

        final Matcher matcher = pattern.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastKwEnd = 0;

        while (matcher.find()) {

            String styleClass = null;

            for (final String[] availableClass : AVAILABLE_CLASSES) {

                try {
                    styleClass = matcher.group(availableClass[0]) != null ? availableClass[1] : null;
                } catch (final IllegalArgumentException e) {
                    continue;
                }

                if(styleClass != null) {
                    break;
                }
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
     * Gets style spans.
     *
     * @param text the text
     * @return the style spans
     */
    @FXThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Load the content.
     *
     * @param content the content.
     */
    @FXThread
    public void loadContent(@NotNull final String content) {
        appendText(content);

        final UndoManager undoManager = getUndoManager();
        undoManager.forgetHistory();
    }

    /**
     * Reload the content.
     *
     * @param content the content.
     */
    @FXThread
    public void reloadContent(@NotNull final String content) {
        reloadContent(content, false);
    }

    /**
     * Reload the content.
     *
     * @param content      the content.
     * @param clearHistory true if need to clear history.
     */
    @FXThread
    public void reloadContent(@NotNull final String content, final boolean clearHistory) {

        final String currentContent = getText();

        if (!StringUtils.equals(currentContent, content)) {
            if (content.isEmpty()) {
                deleteText(0, currentContent.length());
            } else {
                replaceText(0, currentContent.length(), content);
            }
        }

        if (clearHistory) {
            final UndoManager undoManager = getUndoManager();
            undoManager.forgetHistory();
        }
    }
}
