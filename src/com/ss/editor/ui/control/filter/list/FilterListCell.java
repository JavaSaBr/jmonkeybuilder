package com.ss.editor.ui.control.filter.list;

import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.filter.operation.DisableSceneFilterOperation;
import com.ss.editor.ui.control.filter.operation.EnableSceneFilterOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of list cell to present an editable scene filter.
 *
 * @author JavaSaBr
 */
public class FilterListCell extends TextFieldListCell<EditableSceneFilter<?>> {

    /**
     * The list of filters.
     */
    @NotNull
    private final FilterList stateList;

    /**
     * The content box.
     */
    @NotNull
    private final HBox content;

    /**
     * The label of this cell.
     */
    @NotNull
    private final Label text;

    /**
     * The visible icon.
     */
    @NotNull
    private final ImageView visibleIcon;

    /**
     * Instantiates a new Filter list cell.
     *
     * @param stateList the state list
     */
    public FilterListCell(final @NotNull FilterList stateList) {
        this.stateList = stateList;
        this.content = new HBox();
        this.text = new Label();
        this.visibleIcon = new ImageView();
        this.visibleIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, this::processHide);
        this.visibleIcon.setOnMouseReleased(this::processHide);
        this.visibleIcon.setPickOnBounds(true);

        FXUtils.addToPane(visibleIcon, content);
        FXUtils.addToPane(text, content);

        setEditable(false);

        FXUtils.addClassTo(content, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(this, CSSClasses.SCENE_FILTER_LIST_CELL);
    }

    /**
     * @return the list of app states.
     */
    @NotNull
    private FilterList getStateList() {
        return stateList;
    }

    /**
     * Update hide status.
     */
    private void processHide(@NotNull final MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        final EditableSceneFilter<?> item = getItem();
        final FilterList stateList = getStateList();
        final SceneChangeConsumer changeConsumer = stateList.getChangeConsumer();

        if (item.isEnabled()) {
            changeConsumer.execute(new DisableSceneFilterOperation(item));
        } else {
            changeConsumer.execute(new EnableSceneFilterOperation(item));
        }
    }

    @Override
    public void updateItem(@Nullable final EditableSceneFilter<?> item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            return;
        }

        visibleIcon.setVisible(true);
        visibleIcon.setManaged(true);
        visibleIcon.setImage(!item.isEnabled() ? Icons.INVISIBLE_16 : Icons.VISIBLE_16);
        visibleIcon.setOpacity(!item.isEnabled() ? 0.5D : 1D);

        text.setText(item.getName());

        setText(StringUtils.EMPTY);
        setGraphic(content);
    }
}
