package com.ss.editor.model.tool;

import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
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
    public static void useStandardGenerator(@NotNull final Spatial spatial, final boolean splitMirrored) {
        try {

            NodeUtils.visitGeometry(spatial, geometry -> {

                final Mesh mesh = geometry.getMesh();
                final VertexBuffer texCoord = mesh.getBuffer(VertexBuffer.Type.TexCoord);

                if (texCoord != null) {
                    TangentBinormalGenerator.generate(geometry, splitMirrored);
                }
            });

        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, null, e);
        }
    }

    /**
     * Generate tangents using a Mikktspace algorithm.
     *
     * @param spatial the spatial.
     */
    public static void useMikktspaceGenerator(@NotNull final Spatial spatial) {
        try {

            NodeUtils.visitGeometry(spatial, geometry -> {

                final Mesh mesh = geometry.getMesh();
                final VertexBuffer texCoord = mesh.getBuffer(VertexBuffer.Type.TexCoord);

                if (texCoord != null) {
                    MikktspaceTangentGenerator.generate(geometry);
                }
            });

        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, null, e);
        }
    }
}
