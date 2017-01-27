package com.ss.editor.ui.control.model.tree.dialog;

import static java.util.Objects.requireNonNull;
import static rlib.util.dictionary.DictionaryFactory.newObjectDictionary;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.Messages;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.action.operation.AddControlOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.extension.scene.control.EditableControl;
import com.ss.extension.scene.control.impl.EditableBillboardControl;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.net.URLClassLoader;

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.ClassUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The dialog to create a custom control.
 *
 * @author JavaSaBr
 */
public class CreateCustomControlDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(415, 184);

    private static final Insets THE_FIRST_OFFSET = new Insets(10, 0, 0, 0);
    private static final Insets THE_SECOND_OFFSET = new Insets(0, 0, 10, 0);

    private static final ObjectDictionary<String, EditableControl> BUILT_IN = newObjectDictionary();
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableBillboardControl());
    }

    private static final ClasspathManager CLASSPATH_MANAGER = ClasspathManager.getInstance();
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    private static void register(@NotNull final EditableControl editableControl) {
        BUILT_IN.put(editableControl.getName(), editableControl);
        BUILT_IN_NAMES.add(editableControl.getName());
    }

    /**
     * The list of built in controls.
     */
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating control.
     */
    private CheckBox customCheckBox;

    /**
     * The full class name of creating control.
     */
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

    public CreateCustomControlDialog(@NotNull final ModelChangeConsumer changeConsumer, @NotNull final Spatial spatial) {
        this.changeConsumer = changeConsumer;
        this.spatial = spatial;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_CUSTOM_CONTROL_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final Label builtInLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());

        final HBox buildInContainer = new HBox(builtInLabel, builtInBox);

        final Label customBoxLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        final HBox customBoxContainer = new HBox(customBoxLabel, customCheckBox);

        final Label customNameLabel = new Label(Messages.CREATE_CUSTOM_CONTROL_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        controlNameField = new TextField();
        controlNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        controlNameField.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final HBox customNameContainer = new HBox(customNameLabel, controlNameField);

        FXUtils.addToPane(buildInContainer, root);
        FXUtils.addToPane(customBoxContainer, root);
        FXUtils.addToPane(customNameContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(controlNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(customBoxContainer, THE_FIRST_OFFSET);
        VBox.setMargin(customNameContainer, THE_SECOND_OFFSET);
    }

    @Override
    protected void processOk() {

        if (customCheckBox.isSelected()) {

            Control newExample = null;
            try {
                newExample = ClassUtils.newInstance(controlNameField.getText());
            } catch (final RuntimeException e) {

                final Array<URLClassLoader> classLoaders = RESOURCE_MANAGER.getClassLoaders();

                for (final URLClassLoader classLoader : classLoaders) {
                    try {
                        final Class<?> targetClass = classLoader.loadClass(controlNameField.getText());
                        newExample = ClassUtils.newInstance(targetClass);
                    } catch (final ClassNotFoundException ex) {
                        LOGGER.warning(this, e);
                    }
                }

                final URLClassLoader additionalCL = CLASSPATH_MANAGER.getAdditionalCL();
                if (additionalCL != null) {
                    try {
                        final Class<?> targetClass = additionalCL.loadClass(controlNameField.getText());
                        newExample = ClassUtils.newInstance(targetClass);
                    } catch (final ClassNotFoundException ex) {
                        LOGGER.warning(this, e);
                    }
                }
            }

            if (newExample == null) {
                throw new RuntimeException("Can't create a control of the class " + controlNameField.getText());
            }

            changeConsumer.execute(new AddControlOperation(newExample, spatial));

        } else {

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
