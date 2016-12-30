package com.ss.editor.model.tool;

import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.ss.editor.util.EditorUtil;

import rlib.logging.Logger;
import rlib.logging.LoggerManager;

/**
 * Tangent generators.
 *
 * @author JavaSaBr
 */
public class TangentGenerator {

    private static final Logger LOGGER = LoggerManager.getLogger(TangentGenerator.class);

    /**
     * Generate tangents using a standard algorithm.
     */
    public static void useStandardGenerator(final Spatial spatial, final boolean splitMirrored) {
        try {
            TangentBinormalGenerator.generate(spatial, splitMirrored);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, null, e);
        }
    }

    /**
     * Generate tangents using a Mikktspace algorithm.
     */
    public static void useMikktspaceGenerator(final Spatial spatial) {
        try {
            MikktspaceTangentGenerator.generate(spatial);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, null, e);
        }
    }
}
