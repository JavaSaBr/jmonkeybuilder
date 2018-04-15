package com.ss.editor.ui.dialog;

import static com.ss.editor.config.DefaultSettingsProvider.Defaults.*;
import static com.ss.editor.config.DefaultSettingsProvider.Preferences.*;
import static com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory.build;
import static com.ss.rlib.common.util.ClassUtils.unsafeCast;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.jme3.asset.AssetManager;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor;
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
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
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

    @NotNull
    private static final Point DIALOG_SIZE = new Point(-1, -1);

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
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
        FXUtils.addClassTo(getContainer(), CssClasses.SETTINGS_DIALOG);
        validate();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        vars = VarTable.newInstance();
        controls = ArrayFactory.newArray(PropertyEditorControl.class);

        messageLabel = new Label();

        final ObjectDictionary<SettingsCategory, Array<SettingsPropertyDefinition>> categoryToSettings = DictionaryFactory.newObjectDictionary();
        final Array<SettingsPropertyDefinition> definitions = SETTINGS_REGISTRY.getDefinitions();
        final Array<SettingsCategory> categories = definitions.stream()
                .map(SettingsPropertyDefinition::getCategory)
                .distinct().sorted()
                .collect(toArray(SettingsCategory.class));

        for (final SettingsPropertyDefinition definition : definitions) {
            categoryToSettings.get(definition.getCategory(), cat -> ArrayFactory.newArray(SettingsPropertyDefinition.class))
                    .add(definition);
        }

        final TabPane tabPane = new TabPane();
        tabPane.prefWidthProperty().bind(root.widthProperty());
        tabPane.prefHeightProperty().bind(heightProperty());

        messageLabel.prefWidthProperty().bind(tabPane.widthProperty());

        for (final SettingsCategory category : categories) {

            final Array<SettingsPropertyDefinition> properties = notNull(categoryToSettings.get(category));
            final Array<PropertyEditorControl<?>> tabControls = properties.stream()
                    .map(definition -> build(vars, definition, this::validate))
                    .collect(unsafeCast(toArray(PropertyEditorControl.class)));

            controls.addAll(tabControls);

            final VBox container = new VBox();
            container.prefWidthProperty().bind(tabPane.widthProperty());

            final Tab tab = new Tab(category.getLabel());
            tab.setClosable(false);
            tab.setContent(container);

            tabControls.forEach(container.getChildren(), (control, nodes) -> nodes.add(control));

            FXUtils.addClassesTo(container, CssClasses.SETTINGS_DIALOG_CONTAINER);

            tabPane.getTabs().add(tab);
        }

        FXUtils.addClassTo(messageLabel, CssClasses.SETTINGS_DIALOG_MESSAGE_LABEL);
        FXUtils.addToPane(tabPane, messageLabel, root);
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

        final Array<PropertyEditorControl<?>> controls = getControls();
        if (controls != null) {
            controls.forEach(PropertyEditorControl::checkDependency);
        }

        final PropertyEditorControl<?> requiredRestart = controls == null ? null : controls.search(control -> {
            final String propertyId = control.getPropertyId();
            final SettingsProviderRegistry registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredRestart(propertyId) && control.isNotDefault();
        });

        final Label messageLabel = getMessageLabel();

        if (requiredRestart != null) {
            messageLabel.setText(Messages.SETTINGS_DIALOG_MESSAGE);
        } else {
            messageLabel.setText(StringUtils.EMPTY);
        }
    }

    @Override
    @FxThread
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();

        final Button okButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.SIMPLE_DIALOG_BUTTON_CLOSE);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addClassTo(okButton, cancelButton, CssClasses.DIALOG_BUTTON);
        FXUtils.addClassTo(container, CssClasses.DEF_HBOX);

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);
    }

    /**
     * Save new settings.
     */
    @FxThread
    private void processOk() {

        final Array<PropertyEditorControl<?>> controls = getControls();
        if (controls == null) {
            throw new RuntimeException("Controls weren't initialized.");
        }

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final ObjectDictionary<String, Object> values = getVars().getValues();
        values.forEach(editorConfig::set);
        editorConfig.save();

        final PropertyEditorControl<?> requiredRestart = controls.search(control -> {
            final String propertyId = control.getPropertyId();
            final SettingsProviderRegistry registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredRestart(propertyId) && control.isNotDefault();
        });

        final PropertyEditorControl<?> requiredUpdateClasspath = controls.search(control -> {
            final String propertyId = control.getPropertyId();
            final SettingsProviderRegistry registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredUpdateClasspath(propertyId) && control.isNotDefault();
        });

        final PropertyEditorControl<?> reshape3DView = controls.search(control -> {
            final String propertyId = control.getPropertyId();
            final SettingsProviderRegistry registry = SettingsProviderRegistry.getInstance();
            return registry.isRequiredReshape3DView(propertyId) && control.isNotDefault();
        });

        if (reshape3DView != null) {
            final JfxApplication jfxApplication = JfxApplication.getInstance();
            final FrameTransferSceneProcessor sceneProcessor = jfxApplication.getSceneProcessor();
            sceneProcessor.reshape();
        }

        EXECUTOR_MANAGER.addJmeTask(() -> {

            final JmeApplication jmeApplication = JmeApplication.getInstance();
            final SceneEditor3DPart sceneEditor3DPart = jmeApplication.getStateManager()
                    .getState(SceneEditor3DPart.class);

            final ToneMapFilter filter = jmeApplication.getToneMapFilter();
            filter.setWhitePoint(editorConfig.getVector3f(PREF_FILTER_TONEMAP_WHITE_POINT, PREF_DEFAULT_TONEMAP_WHITE_POINT));

            if (sceneEditor3DPart != null) {
                return;
            }

            filter.setEnabled(editorConfig.getBoolean(PREF_FILTER_TONEMAP, PREF_DEFAULT_TONEMAP_FILTER));

            final FXAAFilter fxaaFilter = jmeApplication.getFXAAFilter();
            fxaaFilter.setEnabled(editorConfig.getBoolean(PREF_FILTER_FXAA, PREF_DEFAULT_FXAA_FILTER));
        });

        if (requiredUpdateClasspath != null) {

            final ClasspathManager classpathManager = ClasspathManager.getInstance();
            classpathManager.reload();

            final AssetManager assetManager = EditorUtil.getAssetManager();
            assetManager.clearCache();
        }

        final ResourceManager resourceManager = ResourceManager.getInstance();
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
