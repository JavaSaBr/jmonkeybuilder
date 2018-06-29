package com.ss.editor.ui.dialog;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.ss.editor.JfxApplication;
import com.ss.editor.JmeApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.part3d.editor.impl.scene.SceneEditor3DPart;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.editor.plugin.api.settings.SettingsCategory;
import com.ss.editor.plugin.api.settings.SettingsPropertyDefinition;
import com.ss.editor.plugin.api.settings.SettingsProviderRegistry;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.fx.util.FxUtils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * The dialog with settings.
 *
 * @author JavaSaBr
 */
public class SettingsDialog extends EditorDialog {

    private static final Point DIALOG_SIZE = new Point(-1, -1);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final SettingsProviderRegistry SETTINGS_REGISTRY = SettingsProviderRegistry.getInstance();

    /**
     * The all created controls.
     */
    @Nullable
    private Array<PropertyEditorControl<?>> controls;

    /**
     * The map of all variables.
     */
    @Nullable
    private VarTable vars;

    /**
     * The message label.
     */
    @Nullable
    private Label messageLabel;

    public SettingsDialog() {
        FxUtils.addClass(getContainer(), CssClasses.SETTINGS_DIALOG);
    }

    @Override
    @FxThread
    public void construct() {
        super.construct();
        validate();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);

        vars = VarTable.newInstance();
        controls = ArrayFactory.newArray(PropertyEditorControl.class);

        messageLabel = new Label();

        var categoryToSettings = DictionaryFactory.<SettingsCategory, Array<SettingsPropertyDefinition>>newObjectDictionary();

        var definitions = SETTINGS_REGISTRY.getDefinitions();
        var categories = definitions.stream()
                .map(SettingsPropertyDefinition::getCategory)
                .distinct()
                .sorted()
                .collect(toArray(SettingsCategory.class));

        for (var definition : definitions) {
            categoryToSettings.get(definition.getCategory(), cat -> newCategoryArray())
                    .add(definition);
        }

        var tabPane = new TabPane();
        tabPane.prefWidthProperty().bind(root.widthProperty());
        tabPane.prefHeightProperty().bind(heightProperty());

        messageLabel.prefWidthProperty()
                .bind(tabPane.widthProperty());

        for (var category : categories) {

            var properties = notNull(categoryToSettings.get(category));

            Array<PropertyEditorControl<?>> tabControls = properties.stream()
                    .map(definition -> build(vars, definition, this::validate))
                    .collect(unsafeCast(toArray(PropertyEditorControl.class)));

            controls.addAll(tabControls);

            var container = new VBox();
            container.prefWidthProperty()
                    .bind(tabPane.widthProperty());

            var tab = new Tab(category.getLabel());
            tab.setClosable(false);
            tab.setContent(container);

            tabControls.forEach(container.getChildren(),
                    (control, nodes) -> nodes.add(control));

            FxUtils.addClass(container, CssClasses.SETTINGS_DIALOG_CONTAINER);

            tabPane.getTabs().add(tab);
        }

        FxUtils.addClass(messageLabel, CssClasses.SETTINGS_DIALOG_MESSAGE_LABEL);
        FxUtils.addChild(root, tabPane, messageLabel);
    }

    private @NotNull Array<SettingsPropertyDefinition> newCategoryArray() {
        return ArrayFactory.newArray(SettingsPropertyDefinition.class);
    }

    /**
     * Get the message label.
     *
     * @return the message label.
     */
    @FxThread
    private @NotNull Label getMessageLabel() {
        return notNull(messageLabel);
    }

    /**
     * Get the all created controls.
     *
     * @return the all created controls.
     */
    @FxThread
    private @Nullable Array<PropertyEditorControl<?>> getControls() {
        return controls;
    }

    /**
     * Get the vars.
     *
     * @return the vars.
     */
    @FxThread
    private @NotNull VarTable getVars() {
        return notNull(vars);
    }

    /**
     * Validate changes.
     */
    @FxThread
    private void validate() {

        var controls = getControls();
        if (controls != null) {
            controls.forEach(PropertyEditorControl::checkDependency);
        }

        var requiredRestart = controls == null ? null : controls.findAny(control -> {
            var propertyId = control.getPropertyId();
            var registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredRestart(propertyId) && control.isNotDefault();
        });

        var messageLabel = getMessageLabel();

        if (requiredRestart != null) {
            messageLabel.setText(Messages.SETTINGS_DIALOG_MESSAGE);
        } else {
            messageLabel.setText(StringUtils.EMPTY);
        }
    }

    @Override
    @FxThread
    protected void createActions(@NotNull VBox root) {
        super.createActions(root);

        var container = new HBox();

        var okButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        var cancelButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_CLOSE);
        cancelButton.setOnAction(event -> hide());

        FxUtils.addClass(okButton, cancelButton, CssClasses.DIALOG_BUTTON)
                .addClass(container, CssClasses.DEF_HBOX);

        FxUtils.addChild(container, okButton, cancelButton)
                .addChild(root, container);
    }

    /**
     * Save new settings.
     */
    @FxThread
    private void processOk() {

        var controls = getControls();
        if (controls == null) {
            throw new RuntimeException("Controls weren't initialized.");
        }

        var editorConfig = EditorConfig.getInstance();

        var values = getVars().getValues();
        values.forEach(editorConfig::set);

        editorConfig.save();

        var requiredRestart = controls.findAny(control -> {
            var propertyId = control.getPropertyId();
            var registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredRestart(propertyId) && control.isNotDefault();
        });

        var requiredUpdateClasspath = controls.findAny(control -> {
            var propertyId = control.getPropertyId();
            var registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredUpdateClasspath(propertyId) && control.isNotDefault();
        });

        var reshape3DView = controls.findAny(control -> {
            var propertyId = control.getPropertyId();
            var registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredReshape3DView(propertyId) && control.isNotDefault();
        });

        if (reshape3DView != null) {
            var jfxApplication = JfxApplication.getInstance();
            var sceneProcessor = jfxApplication.getSceneProcessor();
            sceneProcessor.reshape();
        }

        EXECUTOR_MANAGER.addJmeTask(() -> {

            var jmeApplication = JmeApplication.getInstance();
            var sceneEditor3DPart = jmeApplication.getStateManager()
                    .getState(SceneEditor3DPart.class);

            var filter = jmeApplication.getToneMapFilter();
            filter.setWhitePoint(editorConfig.getVector3f(PREF_FILTER_TONEMAP_WHITE_POINT,
                    PREF_DEFAULT_TONEMAP_WHITE_POINT));

            if (sceneEditor3DPart != null) {
                return;
            }

            filter.setEnabled(editorConfig.getBoolean(PREF_FILTER_TONEMAP, PREF_DEFAULT_TONEMAP_FILTER));

            var fxaaFilter = jmeApplication.getFXAAFilter();
            fxaaFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_FXAA, PREF_DEFAULT_FXAA_FILTER));
        });

        if (requiredUpdateClasspath != null) {

            var classpathManager = ClasspathManager.getInstance();
            classpathManager.reload();

            var assetManager = EditorUtil.getAssetManager();
            assetManager.clearCache();
        }

        var resourceManager = ResourceManager.getInstance();
        resourceManager.updateAdditionalEnvs();

        if (requiredRestart != null) {
            Platform.exit();
        } else {
            hide();
        }
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.SETTINGS_DIALOG_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
