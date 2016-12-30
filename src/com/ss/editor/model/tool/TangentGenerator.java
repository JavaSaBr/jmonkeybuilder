package com.ss.editor.model.tool;

import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

/**
 * Tangent generators.
 *
 * @author JavaSaBr
 */
public class TangentGenerator {

    /**
     * Generate tangents using a standard algorithm.
     */
    public static void useStandardGenerator(final Spatial spatial, final boolean splitMirrored) {
        TangentBinormalGenerator.generate(spatial, splitMirrored);
    }

    /**
     * Generate tangents using a Mikktspace algorithm.
     */
    public static void useMikktspaceGenerator(final Spatial spatial) {
        MikktspaceTangentGenerator.generate(spatial);
    }
}
