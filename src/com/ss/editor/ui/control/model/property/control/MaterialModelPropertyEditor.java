package com.ss.editor.ui.control.model.property.control;

import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.event.FXEventManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} to edit the {@link Material}.
 *
 * @author JavaSaBr
 */
public class MaterialModelPropertyEditor<T extends Spatial, V> extends ModelPropertyControl<T, V> {

    public static final String NO_MATERIAL = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;
    public static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    protected static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    protected static final Array<String> MATERIAL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MATERIAL_EXTENSIONS.add(FileExtensions.JME_MATERIAL);
    }

    /**
     * The label with name of the material.
     */
    private Label materialLabel;

    public MaterialModelPropertyEditor(@Nullable final V element, @NotNull final String paramName,
                                       @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        materialLabel = new Label(NO_MATERIAL);
        materialLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        final Button changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        final Button editButton = new Button();
        editButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(event -> processEdit());

        materialLabel.prefWidthProperty().bind(widthProperty()
                .subtract(changeButton.widthProperty())
                .subtract(editButton.widthProperty())
                .subtract(BUTTON_OFFSET.getLeft() * 2));

        FXUtils.addToPane(materialLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(editButton, BUTTON_OFFSET);

        FXUtils.addClassTo(materialLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(changeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(editButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(editButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Show dialog for choosing another material.
     */
    protected void processChange() {
    }

    /**
     * Open this material in the material editor.
     */
    protected void processEdit() {
    }

    /**
     * @return the label with name of the material.
     */
    @NotNull
    protected Label getMaterialLabel() {
        return Objects.requireNonNull(materialLabel);
    }
}
