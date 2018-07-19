package com.ss.editor.ui.control.property.impl;

import com.jme3.asset.AssetKey;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit {@link AssetKey}.
 *
 * @param <C> the change consumer's type.
 * @param <E> the edited object's type.
 * @param <T> the key's type.
 * @author JavaSaBr
 */
public abstract class KeyPropertyControl<C extends ChangeConsumer, E, T>
        extends PropertyControl<C, E, T> {

    /**
     * The label with name of the audio key.
     */
    @NotNull
    protected final Label keyLabel;

    public KeyPropertyControl(
            @Nullable T element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
        this.keyLabel = new Label(getNoKeyLabel());
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    /**
     * Return the "no key" label.
     *
     * @return the "no key" label.
     */
    @FromAnyThread
    protected abstract @NotNull String getNoKeyLabel();

    /**
     * Get a list of extensions of this asset key.
     *
     * @return the list of extensions of this asset key.
     */
    @FromAnyThread
    protected abstract @NotNull Array<String> getExtensions();

    /**
     * Handle drag exited events.
     *
     * @param dragEvent the drag exited event.
     */
    @FxThread
    protected void handleDragExitedEvent(@NotNull DragEvent dragEvent) {
    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the dropped event.
     */
    @FxThread
    protected void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {
        UiUtils.handleDroppedFile(dragEvent, this, KeyPropertyControl::applyNewKey);
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag over events.
     */
    @FxThread
    protected void handleDragOverEvent(@NotNull DragEvent dragEvent) {
        UiUtils.acceptIfHasFile(dragEvent, getExtensions());
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));

        var openButton = new Button();
        openButton.setGraphic(new ImageView(Icons.EDIT_16));
        openButton.disableProperty()
                .bind(keyLabel.textProperty().isEqualTo(getNoKeyLabel()));

        keyLabel.prefWidthProperty()
                .bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(openButton.widthProperty()));

        FxUtils.addClass(container,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER)
                .addClass(keyLabel,
                        CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL)
                .addClass(changeButton, openButton,
                        CssClasses.FLAT_BUTTON,
                        CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        FxControlUtils.onAction(changeButton, this::chooseKey);
        FxControlUtils.onAction(openButton, this::openKey);

        FxUtils.addChild(container, keyLabel, changeButton, openButton);

        DynamicIconSupport.addSupport(changeButton, openButton);
    }

    /**
     * Show dialog to choose another key.
     */
    @FxThread
    protected void chooseKey() {
    }

    /**
     * Add the new key.
     *
     * @param file the new key.
     */
    @FxThread
    protected void applyNewKey(@NotNull Path file) {
        reload();
    }

    /**
     * Open this asset key in an another editor.
     */
    @FxThread
    protected void openKey() {
    }
}
