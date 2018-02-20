package com.ss.editor.util;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import org.jetbrains.annotations.NotNull;

/**
 * The list of all GLSL types.
 *
 * @author JavaSaBr
 */
public enum GlslType {
    BOOL("bool", "Bool"),
    BOOL_VEC_2("bvec2", "Bool vec2"),
    BOOL_VEC_3("bvec3", "Bool vec3"),
    BOOL_VEC_4("bvec4", "Bool vec4"),
    INT("int", "Int"),
    INT_VEC_2("ivec2", "Int vec2"),
    INT_VEC_3("ivec3", "Int vec3"),
    INT_VEC_4("ivec4", "Int vec4"),
    UNSIGNED_INT("uint", "Unsigned int"),
    UNSIGNED_INT_VEC_2("uvec2", "Unsigned int vec2"),
    UNSIGNED_INT_VEC_3("uvec3", "Unsigned int vec3"),
    UNSIGNED_INT_VEC_4("uvec4", "Unsigned int vec4"),
    FLOAT("float", "Float"),
    VEC_2("vec2", "Float vec2"),
    VEC_3("vec3", "Float vec3"),
    VEC_4("vec4", "Float vec4"),
    MAT_2("mat2", "Matrix2"),
    MAT_3("mat3", "Matrix3"),
    MAT_4("mat4", "Matrix4"),
    SAMPLER_2D("sampler2D", "Texture 2D"),
    SAMPLER_CUBE("samplerCube", "Cube Texture");

    @NotNull
    public static final GlslType[] VALUES = values();

    @NotNull
    private static final ObjectDictionary<String, GlslType> RAW_TYPE_TO_ENUM = DictionaryFactory.newObjectDictionary();

    @NotNull
    private static final ObjectDictionary<String, GlslType> UI_NAME_TO_ENUM = DictionaryFactory.newObjectDictionary();

    static {
        for (final GlslType glslType : VALUES) {
            RAW_TYPE_TO_ENUM.put(glslType.getRawType(), glslType);
            UI_NAME_TO_ENUM.put(glslType.getUIName(), glslType);
        }
    }

    /**
     * Get the enum value for the raw type.
     *
     * @param rawType the raw type.
     * @return the enum value.
     */
    @FromAnyThread
    public static @NotNull GlslType ofRawType(@NotNull final String rawType) {
        return notNull(RAW_TYPE_TO_ENUM.get(rawType));
    }

    /**
     * Get the enum value for the UI name.
     *
     * @param uiName the UI name.
     * @return the enum value.
     */
    @FromAnyThread
    public static @NotNull GlslType ofUIName(@NotNull final String uiName) {
        return notNull(RAW_TYPE_TO_ENUM.get(uiName));
    }

    /**
     * The type to use in a shader.
     */
    @NotNull
    private String rawType;

    /**
     * The name for UI.
     */
    @NotNull
    private String uiName;

    GlslType(@NotNull final String rawType, @NotNull final String uiName) {
        this.rawType = rawType;
        this.uiName = uiName;
    }

    /**
     * Get the type to use in a shader.
     *
     * @return the type to use in a shader.
     */
    @FromAnyThread
    public @NotNull String getRawType() {
        return rawType;
    }

    /**
     * Get the name for UI.
     *
     * @return the name for UI.
     */
    @FromAnyThread
    public @NotNull String getUIName() {
        return uiName;
    }

    @Override
    public String toString() {
        return getUIName();
    }
}
