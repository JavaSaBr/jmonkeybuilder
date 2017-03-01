package com.ss.editor.ui.control.filter.dialog;

import static com.ss.editor.util.EditorUtil.tryToCreateUserObject;
import static java.util.Objects.requireNonNull;
import static rlib.util.dictionary.DictionaryFactory.newObjectDictionary;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.filter.operation.AddSceneFilterOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.filter.EditableSceneFilter;
import com.ss.extension.scene.filter.SceneFilter;
import com.ss.extension.scene.filter.impl.*;
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
 * The dialog to create a {@link SceneFilter}.
 *
 * @author JavaSaBr
 */
public class CreateSceneFilterDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(415, 184);

    @NotNull
    private static final Insets SETTINGS_CONTAINER = new Insets(10, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);

    private static final ObjectDictionary<String, SceneFilter<?>> BUILT_IN = newObjectDictionary();
    private static final Array<String> BUILT_IN_NAMES = ArrayFactory.newArray(String.class);

    static {
        register(new EditableDirectionLightFromSceneShadowFilter());
        register(new EditablePointLightFromSceneShadowFilter());
        register(new EditableCartoonEdgeFilter());
        register(new EditableColorOverlayFilter());
        register(new EditableDepthOfFieldFilter());
        register(new EditableFogFilter());
        register(new EditableFXAAFilter());
        register(new EditablePosterizationFilter());
        register(new EditableToneMapFilter());
        register(new EditableRadialBlurFilter());
        register(new EditableSceneBloomFilter());
        register(new EditableObjectsBloomFilter());
        register(new EditableSceneAndObjectsBloomFilter());
        register(new EditableLightingStateShadowFilter());
    }

    private static void register(@NotNull final SceneFilter<?> sceneFilter) {
        BUILT_IN.put(sceneFilter.getName(), sceneFilter);
        BUILT_IN_NAMES.add(sceneFilter.getName());
    }

    /**
     * The list of built in filters.
     */
    @Nullable
    private ComboBox<String> builtInBox;

    /**
     * The check box to chose an option of creating filter.
     */
    @Nullable
    private CheckBox customCheckBox;

    /**
     * The full class name of creating filter.
     */
    @Nullable
    private TextField filterNameField;

    /**
     * The changes consumer.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    public CreateSceneFilterDialog(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SCENE_FILTER_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final Label customBoxLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_BOX + ":");
        customBoxLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customBoxLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        customCheckBox = new CheckBox();
        customCheckBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        customCheckBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label builtInLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_BUILT_IN + ":");
        builtInLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        builtInLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        builtInBox = new ComboBox<>();
        builtInBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        builtInBox.disableProperty().bind(customCheckBox.selectedProperty());
        builtInBox.getItems().addAll(BUILT_IN_NAMES);
        builtInBox.getSelectionModel().select(BUILT_IN_NAMES.first());
        builtInBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final Label customNameLabel = new Label(Messages.CREATE_SCENE_FILTER_DIALOG_CUSTOM_FIELD + ":");
        customNameLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        customNameLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT2));

        filterNameField = new TextField();
        filterNameField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        filterNameField.disableProperty().bind(customCheckBox.selectedProperty().not());
        filterNameField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT2));

        final GridPane settingsContainer = new GridPane();
        settingsContainer.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
        settingsContainer.add(builtInLabel, 0, 0);
        settingsContainer.add(builtInBox, 1, 0);
        settingsContainer.add(customBoxLabel, 0, 1);
        settingsContainer.add(customCheckBox, 1, 1);
        settingsContainer.add(customNameLabel, 0, 2);
        settingsContainer.add(filterNameField, 1, 2);

        FXUtils.addToPane(settingsContainer, root);

        FXUtils.addClassTo(builtInLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(builtInBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customBoxLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(customNameLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(filterNameField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(settingsContainer, SETTINGS_CONTAINER);
    }

    /**
     * @return the check box to chose an option of creating filter.
     */
    @NotNull
    private CheckBox getCustomCheckBox() {
        return requireNonNull(customCheckBox);
    }

    /**
     * @return the full class name of creating filter.
     */
    @NotNull
    private TextField getFilterNameField() {
        return requireNonNull(filterNameField);
    }

    /**
     * @return the list of built in filters.
     */
    @NotNull
    private ComboBox<String> getBuiltInBox() {
        return requireNonNull(builtInBox);
    }

    @Override
    protected void processOk() {

        final SceneNode currentModel = changeConsumer.getCurrentModel();
        final Array<SceneAppState> appStates = currentModel.getAppStates();
        final Array<SceneFilter<?>> filters = currentModel.getFilters();

        final CheckBox customCheckBox = getCustomCheckBox();
        final TextField filterNameField = getFilterNameField();
        final ComboBox<String> builtInBox = getBuiltInBox();

        if (customCheckBox.isSelected()) {

            final SceneFilter<?> newExample = tryToCreateUserObject(this, filterNameField.getText(), SceneFilter.class);

            if (newExample == null) {
                throw new RuntimeException("Can't create a state of the class " + filterNameField.getText());
            }

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));

        } else {

            final SingleSelectionModel<String> selectionModel = builtInBox.getSelectionModel();
            final String name = selectionModel.getSelectedItem();

            final SceneFilter<?> example = requireNonNull(BUILT_IN.get(name));
            final SceneFilter<?> newExample = ClassUtils.newInstance(example.getClass());

            check(appStates, filters, newExample);

            changeConsumer.execute(new AddSceneFilterOperation(newExample, currentModel));
        }

        super.processOk();
    }

    private void check(@NotNull final Array<SceneAppState> appStates, @NotNull final Array<SceneFilter<?>> filters,
                       @NotNull final SceneFilter<?> newExample) {

        if (!(newExample instanceof EditableSceneFilter<?>)) return;

        final EditableSceneFilter<?> editableSceneFilter = (EditableSceneFilter<?>) newExample;

        String message = editableSceneFilter.checkStates(appStates);

        if (message != null) {
            throw new RuntimeException(message);
        }

        message = editableSceneFilter.checkFilters(filters);

        if (message != null) {
            throw new RuntimeException(message);
        }
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
