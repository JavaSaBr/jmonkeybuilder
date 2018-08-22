package com.ss.builder.fx.control.code;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.FxThread;
import org.fxmisc.richtext.model.StyleSpans;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
            "inTangent", "inBinormal", "inInterleavedData", "inIndex", "inBindPosePosition", "inBindPoseNormal",
            "inBoneWeight", "inBoneIndex", "inBindPoseTangent", "inHWBoneWeight", "inHWBoneIndex", "inInstanceData",
            "inPosition", "inSize", "Normal", "inTexCoord", "inColor",
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

    @Override
    @FxThread
    protected @NotNull StyleSpans<? extends Collection<String>> calculateStyleSpans(@NotNull final String text) {
        return computeHighlighting(PATTERN, text);
    }
}
