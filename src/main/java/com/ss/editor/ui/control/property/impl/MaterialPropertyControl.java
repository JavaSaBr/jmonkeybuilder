package com.ss.editor.ui.control.property.impl;

import static com.ss.rlib.util.ClassUtils.unsafeCast;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

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
     * The label with name of the material.
     */
    @Nullable
    private Label materialLabel;

    public MaterialPropertyControl(@Nullable final V element, @NotNull final String paramName,
                                   @NotNull final C changeConsumer) {
        super(element, paramName, changeConsumer);
        setOnDragOver(this::handleDragOverEvent);
        setOnDragDropped(this::handleDragDroppedEvent);
        setOnDragExited(this::handleDragExitedEvent);
    }

    /**
     * Handle grad exited events.
     *
     * @param dragEvent the drag exited event.
     */
    @FxThread
    private void handleDragExitedEvent(@NotNull final DragEvent dragEvent) {

    }

    /**
     * Handle dropped events.
     *
     * @param dragEvent the dropped event.
     */
    @FxThread
    private void handleDragDroppedEvent(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);

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
    private void handleDragOverEvent(@NotNull final DragEvent dragEvent) {

        final Dragboard dragboard = dragEvent.getDragboard();
        final List<File> files = unsafeCast(dragboard.getContent(DataFormat.FILES));

        if (files == null || files.size() != 1) {
            return;
        }

        final File file = files.get(0);

        if (!file.getName().endsWith(FileExtensions.JME_MATERIAL)) {
            return;
        }

        final Set<TransferMode> transferModes = dragboard.getTransferModes();
        final boolean isCopy = transferModes.contains(TransferMode.COPY);

        dragEvent.acceptTransferModes(isCopy ? TransferMode.COPY : TransferMode.MOVE);
        dragEvent.consume();
    }

    /**
     * Add the mew material.
     *
     * @param file the file
     */
    @FxThread
    protected void addMaterial(@NotNull final Path file) {
    }

    @Override
    @FxThread
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        materialLabel = new Label(NO_MATERIAL);

        final Button changeButton = new Button();
        changeButton.setGraphic(new ImageView(Icons.ADD_16));
        changeButton.setOnAction(event -> processChange());

        final Button editButton = new Button();
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(event -> processEdit());

        materialLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(editButton.widthProperty()));

        FXUtils.addToPane(materialLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        FXUtils.addClassesTo(container, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(materialLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_ELEMENT_LABEL);
        FXUtils.addClassesTo(changeButton, editButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(changeButton, editButton);
    }

    /**
     * Show dialog for choosing another material.
     */
    @FxThread
    protected void processChange() {
    }

    /**
     * Open this material in the material editor.
     */
    @FxThread
    protected void processEdit() {
    }

    /**
     * Gets material label.
     *
     * @return the label with name of the material.
     */
    @FxThread
    protected @NotNull Label getMaterialLabel() {
        return notNull(materialLabel);
    }
}
