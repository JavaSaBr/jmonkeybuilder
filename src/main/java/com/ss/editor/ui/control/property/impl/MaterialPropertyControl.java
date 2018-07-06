package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.fx.util.FxUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * The implementation of the {@link PropertyControl} to edit the {@link Material}.
 *
 * @param <T> the type of a {@link Spatial}
 * @param <V> the type of material property
 * @author JavaSaBr
 */
public class MaterialPropertyControl<C extends ChangeConsumer, T, V> extends PropertyControl<C, T, V> {

    /**
     * The constant NO_MATERIAL.
     */
    @NotNull
    protected static final String NO_MATERIAL = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;

    /**
     * The constant MATERIAL_EXTENSIONS.
     */
    @NotNull
    protected static final Array<String> MATERIAL_EXTENSIONS = ArrayFactory.asArray(FileExtensions.JME_MATERIAL);

    /**
     * The material name label.
     */
    @Nullable
    private Label materialLabel;

    public MaterialPropertyControl(
            @Nullable V element,
            @NotNull String paramName,
            @NotNull C changeConsumer
    ) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    /**
     * Handle drag exited events.
     *
     * @param dragEvent the drag exited event.
     */
    @FxThread
    private void handleDragExitedEvent(@NotNull DragEvent dragEvent) {

    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the dropped event.
     */
    @FxThread
    private void handleDragDroppedEvent(@NotNull DragEvent dragEvent) {

        var files = EditorUtil.getFiles(dragEvent.getDragboard());
        if (files.size() != 1) {
            return;
        }

        var file = files.get(0);

        if (!file.getName().endsWith(FileExtensions.JME_MATERIAL)) {
            return;
        }

        addMaterial(file.toPath());
    }

    /**
     * Handle drag over events.
     *
     * @param dragEvent the drag over event.
     */
    @FxThread
    private void handleDragOverEvent(@NotNull DragEvent dragEvent) {

        var dragboard = dragEvent.getDragboard();
        var files = EditorUtil.getFiles(dragboard);
        if (files.size() != 1) {
            return;
        }

        var file = files.get(0);

        if (!file.getName().endsWith(FileExtensions.JME_MATERIAL)) {
            return;
        }

        var transferModes = dragboard.getTransferModes();
        var isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    /**
     * Add the mew material.
     *
     * @param file the file
     */
    @FxThread
    protected void addMaterial(@NotNull Path file) {
    }

    @Override
    @FxThread
    protected void createControls(@NotNull HBox container) {
        super.createControls(container);

        materialLabel = new Label(NO_MATERIAL);

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(this::change);

        var editButton = new Button();
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(this::openToEdit);

        materialLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(editButton.widthProperty()));

        FxUtils.addClass(container,
                        CssClasses.TEXT_INPUT_CONTAINER,
                        CssClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER)
                .addClass(materialLabel,
                        CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL)
                .addClass(changeButton, editButton,
                        CssClasses.FLAT_BUTTON,
                        CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        FxUtils.addChild(container,
                materialLabel, changeButton, editButton);

        DynamicIconSupport.addSupport(changeButton, editButton);
    }

    /**
     * Show dialog to choose another material.
     *
     * @param event the action event.
     */
    @FxThread
    protected void change(@Nullable ActionEvent event) {
    }

    /**
     * Open this material in the material editor.
     *
     * @param event the action event.
     */
    @FxThread
    protected void openToEdit(@Nullable ActionEvent event) {
    }

    /**
     * Get the material name label.
     *
     * @return the material name label.
     */
    @FxThread
    protected @NotNull Label getMaterialLabel() {
        return notNull(materialLabel);
    }
}
