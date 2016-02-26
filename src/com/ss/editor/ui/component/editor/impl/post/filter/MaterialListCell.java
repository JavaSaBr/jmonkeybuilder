package com.ss.editor.ui.component.editor.impl.post.filter;

import com.jme3.material.Material;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.Icons;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static com.ss.editor.ui.css.CSSClasses.MAIN_FONT_13;
import static com.ss.editor.ui.css.CSSClasses.TOOLBAR_BUTTON;
import static com.ss.editor.ui.css.CSSClasses.TRANSPARENT_LIST_CELL;
import static com.ss.editor.ui.css.CSSIds.POST_FILTER_EDITOR_CELL_REMOVE_BUTTON;
import static javafx.geometry.Pos.CENTER_LEFT;

/**
 * Реализация ячейки для списка материалов.
 *
 * @author Ronn
 */
public class MaterialListCell extends ListCell<Material> {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * Ссылка на редактор.
     */
    private final PostFilterEditor editor;

    /**
     * Кнопка для удаления материала.
     */
    private final Button removeButton;

    public MaterialListCell(final PostFilterEditor editor) {
        this.editor = editor;

        setAlignment(CENTER_LEFT);

        removeButton = new Button();
        removeButton.setId(POST_FILTER_EDITOR_CELL_REMOVE_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_16));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addClassTo(removeButton, TOOLBAR_BUTTON);
        FXUtils.addClassTo(this, TRANSPARENT_LIST_CELL);
        FXUtils.addClassTo(this, MAIN_FONT_13);
    }

    /**
     * Удаление материала.
     */
    private void processRemove() {

        final Material item = getItem();

        if(item == null) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> editor.remove(item));
    }

    @Override
    protected void updateItem(final Material item, final boolean empty) {
        super.updateItem(item, empty);

        if(item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        final Path path = Paths.get(item.getAssetName());
        final Path fileName = path.getFileName();

        setText(fileName.toString());
        setGraphic(removeButton);
    }
}
