package com.ss.editor.plugin.api.property.control;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * The control to edit resource values.
 *
 * @author JavaSaBr
 */
public abstract class ResourcePropertyEditorControl<T> extends PropertyEditorControl<T> {

    protected static final String NOT_SELECTED =
            Messages.RESOURCE_PROPERTY_EDIT_CONTROL_NOTHING_IS_SELECTED;

    /**
     * The label with name of the resource.
     */
    @Nullable
    private Label resourceLabel;

    protected ResourcePropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
        FxUtils.addClass(this, CssClasses.PROPERTY_CONTROL_RESOURCE);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        resourceLabel = new Label(NOT_SELECTED);

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> chooseNew());

        var removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> removeCurrent());
        removeButton.disableProperty()
                .bind(resourceLabel.textProperty().isEqualTo(NOT_SELECTED));

        var container = new HBox(resourceLabel, changeButton, removeButton);
        container.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        resourceLabel.prefWidthProperty()
                .bind(container.widthProperty());

        FxUtils.addChild(this, container);

        FxUtils.addClass(container,
                        CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER)
                .addClass(changeButton, removeButton,
                        CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON)
                .addClass(resourceLabel,
                        CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);

        DynamicIconSupport.addSupport(changeButton);
    }

    /**
     * Choose a new resource.
     */
    @FxThread
    protected void chooseNew() {

    }

    /**
     * Remove the current resource.
     */
    @FxThread
    protected void removeCurrent() {
        setPropertyValue(null);
        change();
        reload();
    }

    /**
     * Handle grad exiting.
     */
    @FxThread
    private void dragExited(@NotNull DragEvent dragEvent) {
    }

    /**
     * Handle dropped files to editor.
     */
    @FxThread
    private void dragDropped(@NotNull DragEvent dragEvent) {

        var dragboard = dragEvent.getDragboard();
        var files = ClassUtils.<List<File>>unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        var file = files.get(0);
        if (!canAccept(file)) {
            return;
        }

        handleFile(file);
    }

    /**
     * Handle a dropped file.
     *
     * @param file the dropped file.
     */
    @FxThread
    protected void handleFile(@NotNull File file) {
    }

    /**
     * Handle drag over.
     */
    @FxThread
    private void dragOver(@NotNull DragEvent dragEvent) {

        var dragboard = dragEvent.getDragboard();
        var files = ClassUtils.<List<File>>unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        var file = files.get(0);
        if (!canAccept(file)) {
            return;
        }

        var transferModes = dragboard.getTransferModes();
        var isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    @FxThread
    protected boolean canAccept(@NotNull File file) {
        return false;
    }

    /**
     * @return the label with name of the resource.
     */
    @FxThread
    protected @NotNull Label getResourceLabel() {
        return notNull(resourceLabel);
    }
}
