package com.ss.editor.ui.component.editor.impl.particle.emitter;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import emitter.Emitter;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static javafx.geometry.Pos.CENTER_LEFT;

/**
 * The implementation of the {@link ListCell} for using {@link emitter.Emitter}.
 *
 * @author JavaSaBr
 */
public class EmitterListCell extends ListCell<Emitter> {

    /**
     * The particle editor.
     */
    private final ParticleEmitterEditor editor;

    /**
     * The remove button.
     */
    private final Button removeButton;

    public EmitterListCell(final ParticleEmitterEditor editor) {
        this.editor = editor;

        setAlignment(CENTER_LEFT);

        removeButton = new Button();
        removeButton.setId(CSSIds.POST_FILTER_EDITOR_CELL_REMOVE_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(this, CSSClasses.TRANSPARENT_LIST_CELL);
        FXUtils.addClassTo(this, CSSClasses.MAIN_FONT_13);
    }

    /**
     * Удаление материала.
     */
    private void processRemove() {
        final Emitter item = getItem();
        if (item == null) return;
        editor.remove(item);
    }

    @Override
    protected void updateItem(final Emitter item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        setText(item.getName());
        setGraphic(removeButton);
    }

    @Override
    public String toString() {
        return "EmitterListCell{" +
                "editor=" + editor +
                ", removeButton=" + removeButton +
                "} " + super.toString();
    }
}
