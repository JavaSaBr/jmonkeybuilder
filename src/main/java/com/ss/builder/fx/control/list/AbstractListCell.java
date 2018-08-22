package com.ss.builder.fx.control.list;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.model.undo.impl.RenameEditableNameOperation;
import com.ss.editor.extension.EditableName;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The base implementation of list cell.
 *
 * @author JavaSaBr
 */
public abstract class AbstractListCell<T> extends TextFieldListCell<T> {

    @NotNull
    private final StringConverter<T> stringConverter = new StringConverter<>() {

        @Override
        public String toString(@Nullable T object) {
            return getName(object);
        }

        @Override
        public T fromString(@NotNull String newName) {

            var item = getItem();
            if (item == null) {
                return null;
            }

            var object = (EditableName) item;
            var currentName = object.getName();

            changeConsumer.execute(new RenameEditableNameOperation(currentName, newName, object));

            return item;
        }
    };

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
     * The change consumer.
     */
    @NotNull
    private final ChangeConsumer changeConsumer;

    /**
     * The context menu factory.
     */
    @NotNull
    private final Function<T, ContextMenu> contextMenuFactory;

    public AbstractListCell(
            @NotNull ChangeConsumer changeConsumer,
            @NotNull Function<T, ContextMenu> contextMenuFactory
    ) {
        this.changeConsumer = changeConsumer;
        this.contextMenuFactory = contextMenuFactory;
        this.content = new HBox();
        this.text = new Label();
        this.visibleIcon = new ImageView();
        this.visibleIcon.addEventFilter(MouseEvent.MOUSE_RELEASED, this::processHide);
        this.visibleIcon.setOnMouseReleased(this::processHide);
        this.visibleIcon.setPickOnBounds(true);

        setOnMouseClicked(this::processClick);
        setConverter(stringConverter);

        FxUtils.addClass(content, CssClasses.DEF_HBOX);
        FxUtils.addChild(content, visibleIcon, text);

        setEditable(false);
    }

    /**
     * Handle a mouse click.
     */
    @FxThread
    private void processClick(@NotNull MouseEvent event) {

        T item = getItem();
        if (item == null) {
            return;
        }

        var button = event.getButton();
        if (button != MouseButton.SECONDARY) {
            return;
        }

        var contextMenu = contextMenuFactory.apply(item);
        if (contextMenu == null) {
            return;
        }

        ExecutorManager.getInstance()
                .addFxTask(() -> contextMenu.show(this, Side.BOTTOM, 0, 0));
    }

    /**
     * Update hide status.
     */
    @FxThread
    private void processHide(@NotNull MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        processHideImpl();
    }

    /**
     * Process to hide.
     */
    @FxThread
    protected abstract void processHideImpl();

    @Override
    @FxThread
    public void updateItem(@Nullable T item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
            setEditable(false);
            return;
        }

        visibleIcon.setVisible(true);
        visibleIcon.setManaged(true);
        visibleIcon.setImage(!isEnabled(item) ? Icons.INVISIBLE_16 : Icons.VISIBLE_16);
        visibleIcon.setOpacity(!isEnabled(item) ? 0.5D : 1D);

        DynamicIconSupport.updateListener(this, visibleIcon, selectedProperty());

        text.setText(getName(item));

        setText(StringUtils.EMPTY);
        setGraphic(content);
        setEditable(item instanceof EditableName);
    }

    /**
     * Get the name of the item.
     *
     * @param item the item.
     * @return the name.
     */
    @FxThread
    protected abstract @NotNull String getName(@Nullable T item);

    /**
     * @param item the item which needs to check.
     * @return true if the item is enabled.
     */
    @FxThread
    protected abstract boolean isEnabled(@Nullable T item);
}
