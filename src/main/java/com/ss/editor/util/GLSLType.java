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
public enum GLSLType {
    BOOL("bool", "Boolean"),
    BOOL_VEC_2("bvec2", "Boolean vector x 2"),
    BOOL_VEC_3("bvec3", "Boolean vector x 3"),
    BOOL_VEC_4("bvec4", "Boolean vector x 4"),
    INT("int", "Integer"),
    INT_VEC_2("ivec2", "Integer vector x 2"),
    INT_VEC_3("ivec3", "Integer vector x 3"),
    INT_VEC_4("ivec4", "Integer vector x 4"),
    UNSIGNED_INT("uint", "Unsigned integer"),
    UNSIGNED_INT_VEC_2("uvec2", "Unsigned integer vector x 2"),
    UNSIGNED_INT_VEC_3("uvec3", "Unsigned integer vector x 3"),
    UNSIGNED_INT_VEC_4("uvec4", "Unsigned integer vector x 4"),
    FLOAT("float", "Float"),
    VEC_2("vec2", "Float vector x 2"),
    VEC_3("vec3", "Float vector x 3"),
    VEC_4("vec4", "Float vector x 4"),
    MAT_2("mat2", "Matrix x 2"),
    MAT_3("mat3", "Matrix x 3"),
    MAT_4("mat4", "Matrix x 4"),
    SAMPLER_2D("sampler2D", "Texture 2D"),
    SAMPLER_CUBE("samplerCube", "Cube Texture");

    @NotNull
    public static final GLSLType[] VALUES = values();

    @NotNull
    private static final ObjectDictionary<String, GLSLType> RAW_TYPE_TO_ENUM = DictionaryFactory.newObjectDictionary();

    @NotNull
    private static final ObjectDictionary<String, GLSLType> UI_NAME_TO_ENUM = DictionaryFactory.newObjectDictionary();

    static {
        for (final GLSLType glslType : VALUES) {
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
    public static @NotNull GLSLType ofRawType(@NotNull final String rawType) {
        return notNull(RAW_TYPE_TO_ENUM.get(rawType));
    }

    /**
     * Get the enum value for the UI name.
     *
     * @param uiName the UI name.
     * @return the enum value.
     */
    @FromAnyThread
    public static @NotNull GLSLType ofUIName(@NotNull final String uiName) {
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

    GLSLType(@NotNull final String rawType, @NotNull final String uiName) {
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
