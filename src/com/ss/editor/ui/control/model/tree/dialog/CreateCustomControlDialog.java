package com.ss.editor.ui.control.model.tree.dialog;

import static com.ss.editor.util.EditorUtil.tryToCreateUserObject;
import static java.util.Objects.requireNonNull;
import static rlib.util.dictionary.DictionaryFactory.newObjectDictionary;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.AddControlOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.extension.scene.control.EditableControl;
import com.ss.extension.scene.control.impl.EditableBillboardControl;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.ObjectDictionary;

import java.awt.*;

/**
 * The dialog to create a custom control.
 *
 * @author JavaSaBr
 */
public class CreateCustomControlDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, 184);

    @NotNull
    private static final Insets SETTINGS_CONTAINER = new Insets(10, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    @NotNull
    private static final ObjectDictionary<String, EditableControl> BUILT_IN = newObjectDictionary();

    @NotNull
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableBillboardControl());
    }

    private static void register(@NotNull final EditableControl editableControl) {
        BUILT_IN.put(editableControl.getName(), editableControl);
        BUILT_IN_NAMES.add(editableControl.getName());
    }

    /**
     * The list of built in controls.
     */
    @Nullable
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating control.
     */
    @Nullable
    private CheckBox customCheckBox;

    /**
     * The full class name of creating control.
     */
    @Nullable
    private TextField controlNameField;

    /**
     * The changes consumer.
     */
    @NotNull
    private final ModelChangeConsumer changeConsumer;

    /**
     * The spatial.
     */
    @NotNull
    private final Spatial spatial;

    public CreateCustomControlDialog(@NotNull final ModelChangeConsumer changeConsumer,
                                     @NotNull final Spatial spatial) {
        this.changeConsumer = changeConsumer;
        this.spatial = spatial;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_CUSTOM_CONTROL_DIALOG_TITLE;
    }

    /**
     * @return the check box to chose an option of creating control.
     */
    @NotNull
    private CheckBox getCustomCheckBox() {
        return requireNonNull(customCheckBox);
    }

    /**
     * @return the list of built in controls.
     */
    @NotNull
    private ComboBox<String> getBuiltInBox() {
        return requireNonNull(builtInBox);
    }

    /**
     * @return the full class name of creating control.
     */
    @NotNull
    private TextField getControlNameField() {
        return requireNonNull(controlNameField);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final Label customBoxLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customNameLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        controlNameField = new TextField();
        controlNameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        controlNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        controlNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final GridPane settingsContainer = new GridPane();
        settingsContainer.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
        settingsContainer.add(builtInLabel, 0, 0);
        settingsContainer.add(builtInBox, 1, 0);
        settingsContainer.add(customBoxLabel, 0, 1);
        settingsContainer.add(customCheckBox, 1, 1);
        settingsContainer.add(customNameLabel, 0, 2);
        settingsContainer.add(controlNameField, 1, 2);

        FXUtils.addToPane(settingsContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(controlNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(settingsContainer, SETTINGS_CONTAINER);
    }

    @Override
    protected void processOk() {

        final CheckBox customCheckBox = getCustomCheckBox();

        if (customCheckBox.isSelected()) {

            final TextField controlNameField = getControlNameField();
            final Control newExample = tryToCreateUserObject(this, controlNameField.getText(), Control.class);

            if (newExample == null) {
                throw new RuntimeException("Can't create a control of the class " + controlNameField.getText());
            }

            changeConsumer.execute(new AddControlOperation(newExample, spatial));

        } else {

            final ComboBox<String> builtInBox = getBuiltInBox();
            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final EditableControl example = requireNonNull(BUILT_IN.get(name));
            final EditableControl newExample = ClassUtils.newInstance(example.getClass());

            changeConsumer.execute(new AddControlOperation(newExample, spatial));
        }

        super.processOk();
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
