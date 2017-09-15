package com.ss.editor.ui.control.code;

import static java.util.Collections.singleton;
import com.ss.editor.annotation.FXThread;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The implementation of code area to show glsl code.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionCodeArea extends BaseCodeArea {

    @NotNull
    private static final String[] KEYWORDS = {
            "MaterialDef", "MaterialParameters", "Technique", "WorldParameters", "Defines", "ForcedRenderState",
            "VertexShaderNodes", "ShaderNode", "Definition", "InputMappings", "OutputMappings", "FragmentShaderNodes"
    };

    @NotNull
    private static final String[] VALUE_TYPES = {
            "Texture2D", "Float", "Boolean", "Int", "Color", "Vector3", "TextureCubeMap", "Matrix4", "Vector4", "Vector2",
            "VertexShader", "TessellationEvaluationShader ", "TessellationControlShader", "FragmentShader", "LightMode",
            "FaceCull", "DepthTest", "DepthWrite", "PolyOffset",
            "ColorWrite", "Blend", "FragmentShader", "IntArray", "FloatArray", "Vector2Array", "Vector3Array",
            "Vector4Array", "Matrix3", "Matrix3Array", "Matrix4Array", "TextureBuffer", "Texture3D", "TextureArray",
            "GeometryShader", "Global", "WorldParam", "Attr"
    };

    @NotNull
    private static final String[] VALUE_VALUES = {
            "true", "false", "Off", "On", "True", "False", "Disable", "SinglePass", "MultiPass",
            "SinglePassAndImageBased", "FixedPipeline", "StaticPass", "InPass", "PostPass", "World", "View",
            "Legacy",
            // glsl versions
            "GLSL100", "GLSL110", "GLSL120", "GLSL130", "GLSL140", "GLSL150", "GLSL400", "GLSL330",
            "GLSL410", "GLSL420", "GLSL430", "GLSL440", "GLSL450",
            // attributes
            "Tangent", "Binormal", "InterleavedData", "Index", "BindPosePosition", "BindPoseNormal",
            "BoneWeight", "BoneIndex", "BindPoseTangent", "HWBoneWeight", "HWBoneIndex", "InstanceData",
            "Position", "Size", "Normal", "TexCoord", "Color",
            // uniforms
            "WorldViewProjectionMatrix", "Time", "NormalMatrix", "WorldViewMatrix", "ViewMatrix", "CameraPosition",
            "WorldMatrix", "Resolution", "ViewProjectionMatrix", "ProjectionMatrix", "NormalMatrix", "WorldMatrixInverseTranspose",
            "WorldMatrixInverse", "ViewMatrixInverse", "ProjectionMatrixInverse", "ViewProjectionMatrixInverse",
            "WorldViewMatrixInverse", "NormalMatrixInverse", "WorldViewProjectionMatrixInverse",
            "ViewPort", "FrustumNearFar", "ResolutionInverse", "Aspect", "CameraDirection", "CameraLeft", "CameraUp",
            "Tpf", "FrameRate", "LightDirection", "LightPosition", "AmbientLightColor", "LightColor"
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

    @FXThread
    private static @NotNull StyleSpans<Collection<String>> computeHighlighting(final String text) {

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

    @Override
    @FXThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        return computeHighlighting(text);
    }
}
