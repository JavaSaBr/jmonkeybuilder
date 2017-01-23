package com.ss.editor.ui.control.app.state.dialog;

import static java.util.Objects.requireNonNull;
import static rlib.util.dictionary.DictionaryFactory.newObjectDictionary;

import com.ss.editor.Messages;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.app.state.operation.AddAppStateOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.EditableSceneAppState;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.app.state.impl.EditableLightingSceneAppState;
import com.ss.extension.scene.app.state.impl.EditableSkySceneAppState;
import com.ss.extension.scene.filter.SceneFilter;

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
 * The dialog to create a scene app state.
 *
 * @author JavaSaBr
 */
public class CreateSceneAppStateDialog extends AbstractSimpleEditorDialog {

    private static final Point DIALOG_SIZE = new Point(415, 184);

    private static final Insets THE_FIRST_OFFSET = new Insets(10, 0, 0, 0);
    private static final Insets THE_SECOND_OFFSET = new Insets(0, 0, 10, 0);

    private static final ObjectDictionary<String, EditableSceneAppState> BUILT_IN = newObjectDictionary();
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableLightingSceneAppState());
        register(new EditableSkySceneAppState());
    }

    private static final ClasspathManager CLASSPATH_MANAGER = ClasspathManager.getInstance();
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();

    private static void register(@NotNull final EditableSceneAppState appState) {
        BUILT_IN.put(appState.getName(), appState);
        BUILT_IN_NAMES.add(appState.getName());
    }

    /**
     * The list of built in states.
     */
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating state.
     */
    private CheckBox customCheckBox;

    /**
     * The full class name of creating state.
     */
    private TextField stateNameField;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    public CreateSceneAppStateDialog(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SCENE_APP_STATE_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());

        final HBox buildInContainer = new HBox(builtInLabel, builtInBox);

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        final HBox customBoxContainer = new HBox(customBoxLabel, customCheckBox);

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_LABEL);

        stateNameField = new TextField();
        stateNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        stateNameField.setId(CSSIds.CREATE_SCENE_APP_STATE_DIALOG_CONTROL);

        final HBox customNameContainer = new HBox(customNameLabel, stateNameField);

        FXUtils.addToPane(buildInContainer, root);
        FXUtils.addToPane(customBoxContainer, root);
        FXUtils.addToPane(customNameContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(stateNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(customBoxContainer, THE_FIRST_OFFSET);
        VBox.setMargin(customNameContainer, THE_SECOND_OFFSET);
    }

    @Override
    protected void processOk() {

        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final Array<SceneAppState> appStates = currentModel.getAppStates();
        final Array<SceneFilter<?>> filters = currentModel.getFilters();

        if (customCheckBox.isSelected()) {

            SceneAppState newExample = null;
            try {
                newExample = ClassUtils.newInstance(stateNameField.getText());
            } catch (final RuntimeException e) {

                final Array<URLClassLoader> classLoaders = RESOURCE_MANAGER.getClassLoaders();

                for (final URLClassLoader classLoader : classLoaders) {
                    try {
                        final Class<?> targetClass = classLoader.loadClass(stateNameField.getText());
                        newExample = ClassUtils.newInstance(targetClass);
                    } catch (final ClassNotFoundException ex) {
                        LOGGER.warning(this, e);
                    }
                }

                final URLClassLoader additionalCL = CLASSPATH_MANAGER.getAdditionalCL();
                if (additionalCL != null) {
                    try {
                        final Class<?> targetClass = additionalCL.loadClass(stateNameField.getText());
                        newExample = ClassUtils.newInstance(targetClass);
                    } catch (final ClassNotFoundException ex) {
                        LOGGER.warning(this, e);
                    }
                }
            }

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + stateNameField.getText());
            }

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddAppStateOperation(newExample, currentModel));

        } else {

            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final EditableSceneAppState example = requireNonNull(BUILT_IN.get(name));
            final SceneAppState newExample = ClassUtils.newInstance(example.getClass());

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddAppStateOperation(newExample, currentModel));
        }

        super.processOk();
    }

    private void check(@NotNull final Array<SceneAppState> appStates, @NotNull final Array<SceneFilter<?>> filters,
                       @NotNull final SceneAppState newExample) {

        if (!(newExample instanceof EditableSceneAppState)) return;

        final EditableSceneAppState editableSceneAppState = (EditableSceneAppState) newExample;

        String message = editableSceneAppState.checkStates(appStates);

        if (message != null) {
            throw new RuntimeException(message);
        }

        message = editableSceneAppState.checkFilters(filters);

        if (message != null) {
            throw new RuntimeException(message);
        }
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
