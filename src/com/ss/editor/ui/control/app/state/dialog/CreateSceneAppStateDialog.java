package com.ss.editor.ui.control.app.state.dialog;

import static com.ss.editor.util.EditorUtil.tryToCreateUserObject;
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
import com.ss.extension.scene.app.state.impl.bullet.EditableBulletSceneAppState;
import com.ss.extension.scene.filter.SceneFilter;
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
 * The dialog to create a scene app state.
 *
 * @author JavaSaBr
 */
public class CreateSceneAppStateDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, 184);

    @NotNull
    private static final Insets SETTINGS_CONTAINER = new Insets(10, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    private static final ObjectDictionary<String, EditableSceneAppState> BUILT_IN = newObjectDictionary();
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableLightingSceneAppState());
        register(new EditableSkySceneAppState());
        register(new EditableBulletSceneAppState());
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
    @Nullable
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating state.
     */
    @Nullable
    private CheckBox customCheckBox;

    /**
     * The full class name of creating state.
     */
    @Nullable
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

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        stateNameField = new TextField();
        stateNameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        stateNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        stateNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final GridPane settingsContainer = new GridPane();
        settingsContainer.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
        settingsContainer.add(builtInLabel, 0, 0);
        settingsContainer.add(builtInBox, 1, 0);
        settingsContainer.add(customBoxLabel, 0, 1);
        settingsContainer.add(customCheckBox, 1, 1);
        settingsContainer.add(customNameLabel, 0, 2);
        settingsContainer.add(stateNameField, 1, 2);

        FXUtils.addToPane(settingsContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(stateNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(settingsContainer, SETTINGS_CONTAINER);
    }

    /**
     * @return the check box to chose an option of creating state.
     */
    @NotNull
    private CheckBox getCustomCheckBox() {
        return requireNonNull(customCheckBox);
    }

    /**
     * @return the full class name of creating state.
     */
    @NotNull
    private TextField getStateNameField() {
        return requireNonNull(stateNameField);
    }

    /**
     * @return the list of built in states.
     */
    @NotNull
    private ComboBox<String> getBuiltInBox() {
        return requireNonNull(builtInBox);
    }

    @Override
    protected void processOk() {

        final CheckBox customCheckBox = getCustomCheckBox();
        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final Array<SceneAppState> appStates = currentModel.getAppStates();
        final Array<SceneFilter<?>> filters = currentModel.getFilters();

        if (customCheckBox.isSelected()) {

            final TextField stateNameField = getStateNameField();
            final SceneAppState newExample = tryToCreateUserObject(this, stateNameField.getText(), SceneAppState.class);

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + this.stateNameField.getText());
            }

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddAppStateOperation(newExample, currentModel));

        } else {

            final ComboBox<String> builtInBox = getBuiltInBox();
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
