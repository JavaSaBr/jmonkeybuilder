package com.ss.editor.ui.control.list;

import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
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
 * The base implementation of list cell.
 *
 * @author JavaSaBr
 */
public abstract class AbstractListCell<T> extends TextFieldListCell<T> {

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

    public AbstractListCell() {
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
    }

    /**
     * Update hide status.
     */
    private void processHide(@NotNull final MouseEvent event) {
        event.consume();

        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }

        processHideImpl();
    }

    /**
     * Process to hide.
     */
    protected abstract void processHideImpl();

    @Override
    public void updateItem(@Nullable final T item, final boolean empty) {
        super.updateItem(item, empty);

        if (item == null) {
            setText(StringUtils.EMPTY);
            setGraphic(null);
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
    }

    /**
     * Gets a name of the item.
     *
     * @param item the item.
     * @return the name.
     */
    @NotNull
    protected abstract String getName(@Nullable final T item);

    /**
     * @return true if the item is enabled.
     */
    protected abstract boolean isEnabled(@Nullable final T item);
}
