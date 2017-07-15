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
 * The implementation of editor to edit material definition files.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionFileEditor extends CodeAreaFileEditor {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final EditorDescription DESCRIPTION = new EditorDescription();

    static {
        DESCRIPTION.setConstructor(MaterialDefinitionFileEditor::new);
        DESCRIPTION.setEditorName(Messages.MATERIAL_DEFINITION_FILE_EDITOR_NAME);
        DESCRIPTION.setEditorId(MaterialDefinitionFileEditor.class.getSimpleName());
        DESCRIPTION.addExtension(FileExtensions.JME_MATERIAL_DEFINITION);
    }

    @NotNull
    private static final String[] KEYWORDS = {
            "MaterialDef", "MaterialParameters", "Technique", "WorldParameters", "Defines", "ForcedRenderState"
    };

    @NotNull
    private static final String[] VALUE_TYPES = {
            "Texture2D", "Float", "Boolean", "Int", "Color", "Vector3", "TextureCubeMap", "Matrix4", "Vector4", "Vector2",
            "VertexShader", "TessellationEvaluationShader ", "TessellationControlShader", "FragmentShader", "LightMode",
            "WorldViewProjectionMatrix", "Time", "NormalMatrix", "WorldViewMatrix",
            "ViewMatrix", "CameraPosition", "WorldMatrix", "FaceCull", "DepthTest", "DepthWrite", "PolyOffset",
            "ColorWrite", "Blend", "Resolution", "FragmentShader", "ViewProjectionMatrix",
            "IntArray", "FloatArray", "Vector2Array", "Vector3Array", "Vector4Array", "Matrix3",
            "Matrix3Array", "Matrix4Array", "TextureBuffer", "Texture3D", "TextureArray", "GeometryShader"
    };

    @NotNull
    private static final String[] VALUE_VALUES = {
            "true", "false", "Off", "On", "True", "False", "Disable", "SinglePass", "MultiPass",
            "SinglePassAndImageBased", "FixedPipeline", "StaticPass", "InPass", "PostPass", "World", "View",
            "Legacy", "GLSL100", "GLSL110", "GLSL120", "GLSL130", "GLSL140", "GLSL150", "GLSL400", "GLSL330",
            "GLSL410", "GLSL420", "GLSL430", "GLSL440", "GLSL450",
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String VALUE_TYPE_PATTERN = "\\b(" + String.join("|", VALUE_TYPES) + ")\\b";
    private static final String VALUE_VALUE_PATTERN = "\\b(" + String.join("|", VALUE_VALUES) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<VALUETYPE>" + VALUE_TYPE_PATTERN + ")"
                    + "|(?<VALUEVALUE>" + VALUE_VALUE_PATTERN + ")"
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
                styleClass = matcher.group("VALUEVALUE") != null ? "value-value" : null;
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
