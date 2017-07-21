package com.ss.editor.ui.dialog.plugin;

import static com.ss.editor.ui.FXConstants.DIALOG_LIST_WIDTH_PERCENT;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.manager.PluginManager;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

/**
 * The implementation of a dialog to work with plugins.
 *
 * @author JavaSaBr
 */
public class PluginsDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, -1);

    @NotNull
    private static final PluginManager PLUGIN_MANAGER = PluginManager.getInstance();

    /**
     * The original list of installed plugins.
     */
    @NotNull
    private final Array<String> originalIds;

    /**
     * The list of installed plugins.
     */
    @Nullable
    private ListView<EditorPlugin> pluginListView;

    public PluginsDialog() {
        this.originalIds = ArrayFactory.newArray(String.class);
        refreshPlugins();
        PLUGIN_MANAGER.handlePlugins(plugin -> originalIds.add(plugin.getId()));
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        pluginListView = new ListView<>();
        pluginListView.setCellFactory(param -> new PluginListCell());
        pluginListView.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);
        pluginListView.prefWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));
        pluginListView.maxWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));

        final HBox buttonContainer = new HBox();
        buttonContainer.prefWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));
        buttonContainer.maxWidthProperty().bind(widthProperty().multiply(DIALOG_LIST_WIDTH_PERCENT));

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.setDisable(true);

        pluginListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        removeButton.setDisable(newValue == null || newValue.isEmbedded()));

        FXUtils.addToPane(pluginListView, root);
        FXUtils.addToPane(addButton, buttonContainer);
        FXUtils.addToPane(removeButton, buttonContainer);
        FXUtils.addToPane(buttonContainer, root);

        FXUtils.addClassTo(buttonContainer, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(addButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(root, CSSClasses.PLUGINS_DIALOG);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.PLUGINS_DIALOG_TITLE;
    }

    /**
     * @return the list of installed plugins.
     */
    @NotNull
    private ListView<EditorPlugin> getPluginListView() {
        return notNull(pluginListView);
    }

    /**
     * Refresh the list of installed plugins.
     */
    private void refreshPlugins() {

        final ObservableList<EditorPlugin> items = pluginListView.getItems();
        items.clear();

        PLUGIN_MANAGER.handlePlugins(items::add);
    }

    /**
     * Process remove.
     */
    private void processRemove() {

        final ListView<EditorPlugin> pluginListView = getPluginListView();
        final EditorPlugin toRemove = pluginListView.getSelectionModel()
                .getSelectedItem();

        if(toRemove.isEmbedded()) {
            return;
        }

        PLUGIN_MANAGER.removePlugin(toRemove);

        refreshPlugins();
    }

    /**
     * @return the list of original plugin ids.
     */
    @NotNull
    private Array<String> getOriginalIds() {
        return originalIds;
    }

    @Override
    protected void processClose() {
        super.processClose();

        final Array<String> newIds = ArrayFactory.newArray(String.class);

        PLUGIN_MANAGER.handlePlugins(plugin -> newIds.add(plugin.getId()));

        final String[] original = newIds.toArray(String.class);
        final String[] toCompare = originalIds.toArray(String.class);

        if (Arrays.equals(original, toCompare)) {
            return;
        }

        final ConfirmDialog dialog = new ConfirmDialog(result -> {
            if (result) Platform.exit();
        }, Messages.PLUGINS_DIALOG_QUESTION);
        dialog.show(getDialog());
    }

    /**
     * Process install a plugin.
     */
    private void processAdd() {

        GAnalytics.sendPageView("PluginChooseDialog", null, "/dialog/PluginChooseDialog");
        GAnalytics.sendEvent(GAEvent.Category.DIALOG, GAEvent.Action.DIALOG_OPENED, "PluginChooseDialog");

        final FileChooser chooser = new FileChooser();
        chooser.setTitle(Messages.PLUGINS_DIALOG_FILE_CHOOSER_TITLE);
        chooser.setSelectedExtensionFilter(new ExtensionFilter(Messages.PLUGINS_DIALOG_FILE_CHOOSER_FILTER, "*.zip"));
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));

        final File result = chooser.showOpenDialog(getDialog());

        if (result == null) {
            return;
        }

        PLUGIN_MANAGER.installPlugin(result.toPath());

        refreshPlugins();
    }

    @Override
    protected boolean needOkButton() {
        return false;
    }

    @NotNull
    @Override
    protected String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CLOSE;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
