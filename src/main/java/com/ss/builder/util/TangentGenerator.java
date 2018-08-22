package com.ss.builder.util;

import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import org.jetbrains.annotations.NotNull;

/**
 * Tangent generators.
 *
 * @author JavaSaBr
 */
public class TangentGenerator {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(TangentGenerator.class);

    /**
     * Generate tangents using a standard algorithm.
     *
     * @param spatial       the spatial.
     * @param splitMirrored the split mirrored.
     */
    public static void useStandardGenerator(@NotNull Spatial spatial, boolean splitMirrored) {
        try {

            NodeUtils.visitGeometry(spatial, geometry -> {

                var mesh = geometry.getMesh();
                var texCoord = mesh.getBuffer(Type.TexCoord);

                if (texCoord != null) {
                    TangentBinormalGenerator.generate(geometry, splitMirrored);
                }
            });

        } catch (Exception e) {
            EditorUtils.handleException(LOGGER, null, e);
        }
    }

    /**
     * Generate tangents using a Mikktspace algorithm.
     *
     * @param spatial the spatial.
     */
    public static void useMikktspaceGenerator(@NotNull Spatial spatial) {
        try {

            NodeUtils.visitGeometry(spatial, geometry -> {

                var mesh = geometry.getMesh();
                var texCoord = mesh.getBuffer(Type.TexCoord);

                if (texCoord != null) {
                    MikktspaceTangentGenerator.generate(geometry);
                }
            });

        } catch (Exception e) {
            EditorUtils.handleException(LOGGER, null, e);
        }
    }
}
