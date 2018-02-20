package com.ss.editor.ui.control.code;

import static java.util.Collections.singleton;
import com.ss.editor.annotation.FxThread;
import com.ss.rlib.util.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.undo.UndoManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base implementation of code area control for this editor.
 *
 * @author JavaSaBr
 */
public class BaseCodeArea extends CodeArea {

    protected static final String CSS_KEYWORD = "keyword";
    protected static final String CSS_VALUE_TYPE = "value-type";
    protected static final String CSS_VALUE_VALUE = "value-value";
    protected static final String CSS_PAREN = "paren";
    protected static final String CSS_BRACE = "brace";
    protected static final String CSS_BRACKET = "bracket";
    protected static final String CSS_SEMICOLON = "semicolon";
    protected static final String CSS_STRING = "string";
    protected static final String CSS_COMMENT = "comment";

    protected static final String CLASS_KEYWORD = "KEYWORD";
    protected static final String CLASS_VALUE_TYPE = "VALUETYPE";
    protected static final String CLASS_VALUE_VALUE = "VALUEVALUE";
    protected static final String CLASS_PAREN = "PAREN";
    protected static final String CLASS_BRACE = "BRACE";
    protected static final String CLASS_BRACKET = "BRACKET";
    protected static final String CLASS_SEMICOLON = "SEMICOLON";
    protected static final String CLASS_STRING = "STRING";
    protected static final String CLASS_COMMENT = "COMMENT";

    @NotNull
    protected static final String[][] AVAILABLE_CLASSES = {
            {CLASS_KEYWORD, CSS_KEYWORD},
            {CLASS_VALUE_TYPE, CSS_VALUE_TYPE},
            {CLASS_VALUE_VALUE, CSS_VALUE_VALUE},
            {CLASS_PAREN, CSS_PAREN},
            {CLASS_BRACE, CSS_BRACE},
            {CLASS_BRACKET, CSS_BRACKET},
            {CLASS_SEMICOLON, CSS_SEMICOLON},
            {CLASS_STRING, CSS_STRING},
            {CLASS_COMMENT, CSS_COMMENT},
    };

    protected static final String PAREN_PATTERN = "\\(|\\)";
    protected static final String BRACE_PATTERN = "\\{|\\}";
    protected static final String BRACKET_PATTERN = "\\[|\\]";
    protected static final String SEMICOLON_PATTERN = "\\;";
    protected static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    protected static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    /**
     * The list of available classes.
     */
    @NotNull
    protected final String[][] availableClasses;

    public BaseCodeArea() {
        this.availableClasses = createAvailableClasses();
        richChanges().filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> setStyleSpans(0, calculateStyleSpans(getText())));
    }

    @Override
    public void replaceSelection(@Nullable String replacement) {

        if ("\t".equals(replacement)) {
            replacement = "    ";
        }

        replaceText(getSelection(), replacement);
    }

    /**
     * Create a list of available classes.
     *
     * @return the list of available classes.
     */
    @FxThread
    protected @NotNull String[][] createAvailableClasses() {
        return AVAILABLE_CLASSES;
    }

    /**
     * Get the list of available classes.
     *
     * @return the list of available classes.
     */
    @FxThread
    protected @NotNull String[][] getAvailableClasses() {
        return AVAILABLE_CLASSES;
    }

    /**
     * Calculate highlighting for the code.
     *
     * @param pattern the pattern.
     * @param text    the text.
     * @return the highlight styles.
     */
    @FxThread
    protected @NotNull StyleSpans<Collection<String>> computeHighlighting(@NotNull final Pattern pattern,
                                                                          @NotNull final String text) {

        final Matcher matcher = pattern.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastKwEnd = 0;

        while (matcher.find()) {

            String styleClass = null;

            for (final String[] availableClass : getAvailableClasses()) {

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
    @FxThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        throw new RuntimeException("unsupported");
    }

    /**
     * Load the content.
     *
     * @param content the content.
     */
    @FxThread
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
    @FxThread
    public void reloadContent(@NotNull final String content) {
        reloadContent(content, false);
    }

    /**
     * Reload the content.
     *
     * @param content      the content.
     * @param clearHistory true if need to clear history.
     */
    @FxThread
    public void reloadContent(@NotNull final String content, final boolean clearHistory) {

        final String currentContent = getText();

        if (!StringUtils.equals(currentContent, content)) {
            if (content.isEmpty()) {
                try {
                    clear();
                } catch (final IllegalStateException e) {
                    //FIXME it's a bug in the richfxeditor library
                    e.printStackTrace();
                }
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
