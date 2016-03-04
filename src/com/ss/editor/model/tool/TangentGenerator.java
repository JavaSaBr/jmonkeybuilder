package com.ss.editor.model.tool;

import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;

/**
 * Реализация генератора тангентов для геометрии.
 *
 * @author Ronn
 */
public class TangentGenerator {

    /**
     * Генерация тангетсов используя стандартный алгоритм.
     */
    public static void useStandardGenerator(final Spatial spatial, final boolean splitMirrored) {
        TangentBinormalGenerator.generate(spatial, splitMirrored);
    }

    /**
     * Генерация тангетсов используя новый алгоритм.
     */
    public static void useMikktspaceGenerator(final Spatial spatial) {
        MikktspaceTangentGenerator.generate(spatial);
    }
}
