package com.ss.editor.ui.component.editor.impl;

import static java.util.Collections.singleton;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.editor.EditorDescription;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation of editor to edit GLSL files.
 *
 * @author JavaSaBr
 */
public class GLSLFileEditor extends CodeAreaFileEditor {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(GLSLFileEditor::new);
        DESCRIPTION.setEditorName(Messages.GLSL_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(GLSLFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.GLSL_FRAGMENT);
        DESCRIPTION.addExtension(FileExtensions.GLSL_VERTEX);
        DESCRIPTION.addExtension(FileExtensions.GLSL_TESSELLATION_CONTROL);
        DESCRIPTION.addExtension(FileExtensions.GLSL_TESSELLATION_EVALUATION);
        DESCRIPTION.addExtension(FileExtensions.GLSL_GEOM);
        DESCRIPTION.addExtension(FileExtensions.GLSL_LIB);
    }

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
    private static final String[] VALUE_TYPES = {
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

    @NotNull
    private static StyleSpans<Collection<String>> computeHighlighting(@NotNull final String text) {

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

        spansBuilder.add(singleton("plain-code"), text.length() - lastKwEnd);

        return spansBuilder.create();
    }

    @NotNull
    @Override
    protected StyleSpans<? extends Collection<String>> getStyleSpans(@NotNull final String text) {
        return computeHighlighting(text);
    }

    @NotNull
    @Override
    public EditorDescription getDescription() {
        return DESCRIPTION;
    }
}
