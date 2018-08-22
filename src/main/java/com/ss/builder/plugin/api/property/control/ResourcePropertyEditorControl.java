package com.ss.builder.plugin.api.property.control;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.fx.util.UiUtils;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.fx.Icons;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.util.DynamicIconSupport;
import com.ss.builder.fx.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.fx.util.FxControlUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;

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
    @NotNull
    protected final Label resourceLabel;

    protected ResourcePropertyEditorControl(
            @NotNull VarTable vars,
            @NotNull PropertyDefinition definition,
            @NotNull Runnable validationCallback
    ) {
        super(vars, definition, validationCallback);
        this.resourceLabel = new Label(NOT_SELECTED);
        setOnDragOver(this::dragOver);
        setOnDragDropped(this::dragDropped);
        setOnDragExited(this::dragExited);
        FxUtils.addClass(this, CssClasses.PROPERTY_CONTROL_RESOURCE);
    }

    @Override
    @FxThread
    public void postConstruct() {
        super.postConstruct();

        var changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));

        var removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.disableProperty()
                .bind(resourceLabel.textProperty().isEqualTo(NOT_SELECTED));

        var container = new HBox(resourceLabel, changeButton, removeButton);
        container.prefWidthProperty()
                .bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        resourceLabel.prefWidthProperty()
                .bind(container.widthProperty());

        FxControlUtils.onAction(changeButton, this::chooseNewResource);
        FxControlUtils.onAction(removeButton, this::removeCurrentResource);

        FxUtils.addClass(container,
                        CssClasses.DEF_HBOX, CssClasses.TEXT_INPUT_CONTAINER)
                .addClass(changeButton, removeButton,
                        CssClasses.FLAT_BUTTON, CssClasses.INPUT_CONTROL_TOOLBAR_BUTTON)
                .addClass(resourceLabel,
                        CssClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);

        FxUtils.addChild(this, container);

        DynamicIconSupport.addSupport(changeButton);
    }

    /**
     * Choose a new resource.
     */
    @FxThread
    protected void chooseNewResource() {
    }

    /**
     * Remove the current resource.
     */
    @FxThread
    protected void removeCurrentResource() {
        setPropertyValue(null);
        changed();
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
        UiUtils.handleDroppedFile(dragEvent, this::chooseNewResource);
    }

    /**
     * Choose a new resource.
     *
     * @param file the resource's file.
     */
    @FxThread
    protected void chooseNewResource(@NotNull Path file) {
    }

    /**
     * Handle drag over.
     */
    @FxThread
    private void dragOver(@NotNull DragEvent dragEvent) { ;
        UiUtils.acceptIfHasFile(dragEvent, this::canAccept);
    }

    @FxThread
    protected boolean canAccept(@NotNull File file) {
        return false;
    }
}
