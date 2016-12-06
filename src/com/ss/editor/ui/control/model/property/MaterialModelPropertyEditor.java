package com.ss.editor.ui.control.model.property;

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

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of the {@link ModelPropertyControl} for editing the {@link Material}.
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

    /**
     * The button for choosing other material.
     */
    private Button changeButton;

    /**
     * The button for editing the material.
     */
    private Button editButton;

    public MaterialModelPropertyEditor(@Nullable final V element, @NotNull final String paramName, @NotNull final ModelChangeConsumer modelChangeConsumer) {
        super(element, paramName, modelChangeConsumer);
    }

    @Override
    protected void createComponents(@NotNull final HBox container) {
        super.createComponents(container);

        materialLabel = new Label(NO_MATERIAL);
        materialLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        editButton = new Button();
        editButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(event -> processEdit());

        FXUtils.addToPane(materialLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(editButton, BUTTON_OFFSET);

        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(editButton, CSSClasses.TOOLBAR_BUTTON);
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
    protected Label getMaterialLabel() {
        return materialLabel;
    }
}
