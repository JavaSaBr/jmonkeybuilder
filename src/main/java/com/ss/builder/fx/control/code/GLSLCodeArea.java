package com.ss.builder.ui.control.code;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.util.GlslType;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.util.GlslType;
import org.fxmisc.richtext.model.StyleSpans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The implementation of code area to show glsl code.
 *
 * @author JavaSaBr
 */
public class GLSLCodeArea extends BaseCodeArea {

    @NotNull
    private static final String[] KEYWORDS = {
            "define", "undef", "if", "ifdef", "ifndef",
            "else", "elif", "endif", "error", "pragma",
            "extension", "version", "line", "attribute", "const",
            "uniform", "varying", "layout", "centroid", "flat",
            "smooth", "noperspective", "patch", "sample", "break",
            "continue", "do", "for", "while", "switch",
            "case", "default", "if", "subroutine", "in", "out", "inout",
            "void", "true", "false", "invariant", "discard", "return", "struct"
    };

    @NotNull
    private static final String[] VALUE_TYPES;

    static {
        VALUE_TYPES = Arrays.stream(GlslType.VALUES)
                .map(GlslType::getRawType)
                .toArray(String[]::new);
    }

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String VALUE_TYPE_PATTERN = "\\b(" + String.join("|", VALUE_TYPES) + ")\\b";

    /**
     * The available fields.
     */
    @Nullable
    private final List<String> fields;

    /**
     * THe pattern.
     */
    @NotNull
    private Pattern pattern;

    public GLSLCodeArea() {
        this.pattern = buildPattern();
        this.fields = new ArrayList<>();
    }

    /**
     * Get available fields.
     *
     * @return available fields.
     */
    @FxThread
    private @Nullable List<String> getFields() {
        return fields;
    }

    /**
     * Build the pattern to highlight code.
     *
     * @return the pattern to highlight code.
     */
    @FxThread
    private @NotNull Pattern buildPattern() {

        String result = "(?<" + CLASS_KEYWORD + ">" + KEYWORD_PATTERN + ")" +
                "|(?<" + CLASS_VALUE_TYPE + ">" + VALUE_TYPE_PATTERN + ")";

        final List<String> fields = getFields();
        if (fields != null && !fields.isEmpty()) {
            result += "|(?<" + CLASS_VALUE_VALUE + ">" + "\\b(" + String.join("|", fields) + ")\\b";
        }

        result += "|(?<" + CLASS_PAREN + ">" + PAREN_PATTERN + ")" +
                "|(?<" + CLASS_BRACE + ">" + BRACE_PATTERN + ")" +
                "|(?<" + CLASS_BRACKET + ">" + BRACKET_PATTERN + ")" +
                "|(?<" + CLASS_SEMICOLON + ">" + SEMICOLON_PATTERN + ")" +
                "|(?<" + CLASS_STRING + ">" + STRING_PATTERN + ")" +
                "|(?<" + CLASS_COMMENT + ">" + COMMENT_PATTERN + ")";

        return Pattern.compile(result);
    }

    @Override
    @FxThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        return computeHighlighting(pattern, text);
    }
}
