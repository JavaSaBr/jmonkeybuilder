package com.ss.editor.ui.dialog.geometry.lod;

import com.jme3.scene.Mesh;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.dialog.geometry.lod.GenerateLodLevelsDialog.ReductionMethod;
import com.ss.editor.ui.util.UIUtils;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the list cell to edit LoD levels.
 *
 * @author JavaSaBr
 */
public class LodValueCell extends TextFieldListCell<Number> {

    @NotNull
    private final StringConverter<Number> converter = new StringConverter<Number>() {

        @Override
        public String toString(final Number value) {
            return String.valueOf(value);
        }

        @Override
        public Number fromString(final String string) {

            final ReductionMethod method = dialog.getMethod();
            final Mesh mesh = dialog.getMesh();

            if (method == ReductionMethod.CONSTANT) {

                final int value = Integer.parseInt(string);
                if (value < 1) return 1;
                if (value > mesh.getTriangleCount()) return mesh.getTriangleCount();

                return value;

            } else {

                final float value = Float.parseFloat(string);
                if (value < 0.001F) return 0.001F;
                if (value >= 1.0F) return 1F;

                return value;
            }
        }
    };

    /**
     * The generator dialog.
     */
    @NotNull
    private final GenerateLodLevelsDialog dialog;

    public LodValueCell(@NotNull final GenerateLodLevelsDialog dialog) {
        setConverter(converter);
        this.dialog = dialog;
    }

    @Override
    @FxThread
    public void startEdit() {
        if (!isEditable()) return;
        super.startEdit();
        UIUtils.updateEditedCell(this);
    }
}
