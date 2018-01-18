package com.ss.editor.ui.dialog.plugin;

import static com.ss.editor.util.EditorUtil.toWeb;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.Messages;
import com.ss.editor.analytics.google.GAEvent;
import com.ss.editor.analytics.google.GAnalytics;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.PluginManager;
import com.ss.editor.plugin.EditorPlugin;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.dialog.ConfirmDialog;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.rlib.plugin.Version;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * The implementation of a dialog to work with plugins.
 *
 * @author JavaSaBr
 */
public class PluginsDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(1200, -1);

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

    /**
     * The description area.
     */
    @Nullable
    private WebView descriptionArea;

    /**
     * The remove button.
     */
    @Nullable
    private Button removeButton;

    /**
     * The background color.
     */
    @Nullable
    private Color backgroundColor;

    /**
     * The font color.
     */
    @Nullable
    private Color fontColor;

    /**
     * The link color.
     */
    @Nullable
    private Color linkColor;

    public PluginsDialog() {
        this.originalIds = ArrayFactory.newArray(String.class);
        refreshPlugins();
        PLUGIN_MANAGER.handlePlugins(plugin -> originalIds.add(plugin.getId()));
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        pluginListView = new ListView<>();
        pluginListView.setCellFactory(param -> new PluginListCell());
        pluginListView.setFixedCellSize(FXConstants.LIST_CELL_HEIGHT);
        pluginListView.prefWidthProperty().bind(root.widthProperty().divide(2));

        descriptionArea = new WebView();

        final BorderPane descriptionContainer = new BorderPane(descriptionArea);
        descriptionContainer.setVisible(false);
        descriptionContainer.prefWidthProperty().bind(root.widthProperty().divide(2));
        descriptionContainer.backgroundProperty().addListener((observable, oldValue, newValue) -> takeColors(newValue));

        final HBox buttonContainer = new HBox();

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAdd());

        removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.setDisable(true);

        pluginListView.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::onSelected);

        FXUtils.addToPane(addButton, removeButton, buttonContainer);
        DynamicIconSupport.addSupport(addButton, removeButton);

        root.add(pluginListView, 0, 0, 1, 1);
        root.add(descriptionContainer, 1, 0, 1, 1);
        root.add(buttonContainer, 0, 1, 1, 1);

        FXUtils.addClassTo(buttonContainer, CssClasses.DEF_HBOX);
        FXUtils.addClassTo(addButton, CssClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CssClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(root, CssClasses.PLUGINS_DIALOG);
        FXUtils.addClassesTo(descriptionContainer, CssClasses.WEBVIEW_TEXT_AREA);
    }

    /**
     * Take CSS colors from background.
     *
     * @param newValue the new background.
     */
    private void takeColors(@Nullable final Background newValue) {

        if (newValue == null) {
            setBackgroundColor(null);
            setFontColor(null);
            return;
        }

        final List<BackgroundFill> fills = newValue.getFills();
        if (fills.size() < 3) {
            setBackgroundColor(null);
            setFontColor(null);
            return;
        }

        final Paint background = fills.get(0).getFill();
        final Paint font = fills.get(1).getFill();
        final Paint link = fills.get(2).getFill();

        if (background instanceof Color) {
            setBackgroundColor((Color) background);
        }

        if (font instanceof Color) {
            setFontColor((Color) font);
        }

        if (link instanceof Color) {
            setLinkColor((Color) link);
        }
    }

    /**
     * Get the background color.
     *
     * @return the background color.
     */
    private @Nullable Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set the background color.
     *
     * @param backgroundColor the background color.
     */
    private void setBackgroundColor(@Nullable final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Get the font color.
     *
     * @return the font color.
     */
    private @Nullable Color getFontColor() {
        return fontColor;
    }

    /**
     * Set the font color.
     *
     * @param fontColor the font color.
     */
    private void setFontColor(@Nullable final Color fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Get the link color.
     *
     * @return the link color.
     */
    private @Nullable Color getLinkColor() {
        return linkColor;
    }

    /**
     * Set the link color.
     *
     * @param linkColor the link color.
     */
    private void setLinkColor(@Nullable final Color linkColor) {
        this.linkColor = linkColor;
    }

    /**
     * Get the remove button.
     *
     * @return the remove button.
     */
    private @NotNull Button getRemoveButton() {
        return notNull(removeButton);
    }

    /**
     * Get the description area.
     *
     * @return the description area.
     */
    private @NotNull WebView getDescriptionArea() {
        return notNull(descriptionArea);
    }

    /**
     * Handle the selected plugin.
     *
     * @param observable the observable property.
     * @param oldValue   the old selected plugin.
     * @param newValue   the new selected plugin.
     */
    @FxThread
    private void onSelected(@NotNull final ObservableValue<? extends EditorPlugin> observable,
                            @Nullable final EditorPlugin oldValue, @Nullable final EditorPlugin newValue) {
        getRemoveButton().setDisable(newValue == null || newValue.isEmbedded());
        final WebView descriptionArea = getDescriptionArea();
        descriptionArea.getParent().setVisible(newValue != null);
        descriptionArea.getEngine().loadContent(generateDescription(newValue));
    }

    /**
     * Generate HTML description.
     *
     * @param plugin the plugin.
     * @return the html description.
     */
    private @NotNull String generateDescription(@Nullable final EditorPlugin plugin) {
        if (plugin == null) return StringUtils.EMPTY;

        final String name = plugin.getName();
        final Version version = plugin.getVersion();
        final String description = plugin.getDescription();
        final URL homePageUrl = plugin.getHomePageUrl();
        final String usedGradleDependencies = plugin.getUsedGradleDependencies();
        final String usedMavenDependencies = plugin.getUsedMavenDependencies();

        final StringBuilder result = new StringBuilder("<html>");

        final Color backgroundColor = getBackgroundColor();
        final Color fontColor = getFontColor();
        final Color linkColor = getLinkColor();

        if (backgroundColor != null && fontColor != null && linkColor != null) {
            result.append("<head><style type=\"text/css\">");
            result.append("body{background-color:").append(toWeb(backgroundColor)).append(";}");
            result.append("h2{color:").append(toWeb(fontColor)).append(";}");
            result.append("h3{color:").append(toWeb(fontColor)).append(";}");
            result.append("h4{color:").append(toWeb(fontColor)).append(";}");
            result.append("p{color:").append(toWeb(fontColor)).append(";}");
            result.append("pre{color:").append(toWeb(fontColor)).append(";}");
            result.append("a{color:").append(toWeb(linkColor)).append(";}");
            result.append("</style>");
            result.append("</head>");
        }

        result.append("<body>");
        result.append("<h2>").append(name).append("</h2>");
        result.append("<h3>").append(Messages.PLUGINS_DIALOG_VERSION).append(": ").append(version).append("</h3>");
        result.append("<p>").append(description).append("</p>");

        if (homePageUrl != null) {
            result.append("<p><a href=\"").append(homePageUrl.toExternalForm())
                    .append("\">").append(Messages.PLUGINS_DIALOG_HOME_PAGE).append("</a></p>");
        }

        if (StringUtils.isNotEmpty(usedGradleDependencies) || StringUtils.isNotEmpty(usedMavenDependencies)) {
            result.append("<h4>").append(Messages.PLUGINS_DIALOG_USED_DEPENDENCIES).append(":").append("</h4>");

            if (StringUtils.isNotEmpty(usedGradleDependencies)) {
                result.append("<h4>").append("Gradle:").append("</h4>");
                result.append(usedGradleDependencies);
            }

            if (StringUtils.isNotEmpty(usedMavenDependencies)) {
                result.append("<h4>").append("Maven:").append("</h4>");
                result.append(usedMavenDependencies);
            }
        }

        result.append("</body></html>");

        return result.toString();
    }

    @Override
    @FromAnyThread
    protected boolean isGridStructure() {
        return true;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.PLUGINS_DIALOG_TITLE;
    }

    /**
     * @return the list of installed plugins.
     */
    @FxThread
    private @NotNull ListView<EditorPlugin> getPluginListView() {
        return notNull(pluginListView);
    }

    /**
     * Refresh the list of installed plugins.
     */
    @FxThread
    private void refreshPlugins() {

        final ObservableList<EditorPlugin> items = pluginListView.getItems();
        items.clear();

        PLUGIN_MANAGER.handlePlugins(items::add);
    }

    /**
     * Process remove.
     */
    @FxThread
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
    @FxThread
    private @NotNull Array<String> getOriginalIds() {
        return originalIds;
    }

    @Override
    @FxThread
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
            if (Boolean.TRUE.equals(result)) Platform.exit();
        }, Messages.PLUGINS_DIALOG_QUESTION);
        dialog.show(getDialog());
    }

    /**
     * Process install a plugin.
     */
    @FxThread
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
    @FromAnyThread
    protected boolean needOkButton() {
        return false;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonCloseText() {
        return Messages.SIMPLE_DIALOG_BUTTON_CLOSE;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
