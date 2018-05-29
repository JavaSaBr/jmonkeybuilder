package com.ss.editor.ui.dialog;

import static com.ss.editor.util.EditorUtil.tryToCreateUserObject;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.dictionary.DictionaryFactory.newObjectDictionary;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.extension.scene.app.state.SceneAppState;
import com.ss.editor.extension.scene.app.state.impl.bullet.EditableBulletSceneAppState;
import com.ss.editor.extension.scene.app.state.impl.pbr.StaticLightProbeSceneAppState;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.model.undo.impl.AddAppStateOperation;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.ClassUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

/**
 * The dialog to create a scene app state.
 *
 * @author JavaSaBr
 */
public class CreateSceneAppStateDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, 0);

    @NotNull
    private static final ObjectDictionary<String, EditableSceneAppState> BUILT_IN = newObjectDictionary();

    @NotNull
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableBulletSceneAppState());
        register(new StaticLightProbeSceneAppState());
    }

    /**
     * Register the new editable scene app state.
     *
     * @param appState the new editable scene app state.
     */
    @FxThread
    public static void register(@NotNull final EditableSceneAppState appState) {
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

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.CREATE_SCENE_APP_STATE_DIALOG_TITLE;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_BUILT_IN + ":");
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_APP_STATE_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        stateNameField = new TextField();
        stateNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        stateNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final GridPane settingsContainer = new GridPane();
        root.add(builtInLabel, 0, 0);
        root.add(builtInBox, 1, 0);
        root.add(customBoxLabel, 0, 1);
        root.add(customCheckBox, 1, 1);
        root.add(customNameLabel, 0, 2);
        root.add(stateNameField, 1, 2);

        FXUtils.addToPane(settingsContainer, root);

        FXUtils.addClassTo(builtInLabel, customBoxLabel, customNameLabel, CssClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(builtInBox, customCheckBox, stateNameField, CssClasses.DIALOG_FIELD);
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    /**
     * Get the check box to chose an option of creating state.
     *
     * @return the check box to chose an option of creating state.
     */
    @FxThread
    private @NotNull CheckBox getCustomCheckBox() {
        return notNull(customCheckBox);
    }

    /**
     * Get the full class name of creating state.
     *
     * @return the full class name of creating state.
     */
    @FxThread
    private @NotNull TextField getStateNameField() {
        return notNull(stateNameField);
    }

    /**
     * Get the list of built in states.
     *
     * @return the list of built in states.
     */
    @FxThread
    private @NotNull ComboBox<String> getBuiltInBox() {
        return notNull(builtInBox);
    }

    @Override
    @FxThread
    protected void processOk() {

        final CheckBox customCheckBox = getCustomCheckBox();
        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final List<SceneAppState> appStates = currentModel.getAppStates();
        final List<SceneFilter> filters = currentModel.getFilters();

        if (customCheckBox.isSelected()) {

            final TextField stateNameField = getStateNameField();
            final SceneAppState newExample = tryToCreateUserObject(this, stateNameField.getText(), SceneAppState.class);

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + stateNameField.getText());
            }

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddAppStateOperation(newExample, currentModel));

        } else {

            final ComboBox<String> builtInBox = getBuiltInBox();
            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final EditableSceneAppState example = notNull(BUILT_IN.get(name));
            final SceneAppState newExample = ClassUtils.newInstance(example.getClass());

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddAppStateOperation(newExample, currentModel));
        }

        super.processOk();
    }

    @FxThread
    private void check(@NotNull final List<SceneAppState> appStates, @NotNull final List<SceneFilter> filters,
                       @NotNull final SceneAppState newExample) {

        if (!(newExample instanceof EditableSceneAppState)) {
            return;
        }

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
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
