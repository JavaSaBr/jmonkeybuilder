package com.ss.editor.ui.dialog.about;

import com.ss.editor.JfxApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.Config;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The dialog about this editor.
 *
 * @author JavaSaBr
 */
public class AboutDialog extends AbstractSimpleEditorDialog {

    private static final String PROJECT_HOME =
            "https://bitbucket.org/JavaSabr/jmonkeybuilder";

    private static final String FORUM_THREAD =
            "https://hub.jmonkeyengine.org/t/editor-jmonkeybuilder";

    private static final Point DIALOG_SIZE = new Point(600, -1);

    private static final String ICONS =
            FileUtils.readFromClasspath("/credits/icons.txt");

    private static final String LIBRARIES =
            FileUtils.readFromClasspath("/credits/libraries.txt");

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        super.createContent(root);

        var application = JfxApplication.getInstance();
        var hostServices = application.getHostServices();

        var gridPane = new GridPane();

        var applicationLabel = new Label(Config.TITLE, new ImageView(Icons.APPLICATION_64));

        var versionLabel = new Label(Messages.ABOUT_DIALOG_VERSION + ":");
        versionLabel.prefWidthProperty()
                .bind(gridPane.widthProperty().multiply(0.5));

        var versionField = new Label(Config.STRING_VERSION);

        var projectHomeLabel = new Label(Messages.ABOUT_DIALOG_PROJECT_HOME + ":");

        var projectHomeField = new Hyperlink("bitbucket.org");
        projectHomeField.setOnAction(event -> hostServices.showDocument(PROJECT_HOME));
        projectHomeField.setFocusTraversable(false);

        var forumThreadLabel = new Label(Messages.ABOUT_DIALOG_FORUM_THREAD + ":");

        var forumThreadField = new Hyperlink("hub.jmonkeyengine.org");
        forumThreadField.setOnAction(event -> hostServices.showDocument(FORUM_THREAD));
        forumThreadField.setFocusTraversable(false);

        var usedLibrariesLabel = new Label(Messages.ABOUT_DIALOG_USED_LIBRARIES + ":");
        usedLibrariesLabel.prefWidthProperty()
                .bind(gridPane.widthProperty());

        var usedIcons = new Label(Messages.ABOUT_DIALOG_USED_ICONS + ":");
        usedIcons.prefWidthProperty()
                .bind(gridPane.widthProperty());

        var librariesArea = new TextArea(LIBRARIES);
        librariesArea.setEditable(false);
        librariesArea.setFocusTraversable(false);

        var iconsArea = new TextArea(ICONS);
        iconsArea.setEditable(false);
        iconsArea.setFocusTraversable(false);

        gridPane.add(applicationLabel, 0, 0, 2, 1);
        gridPane.add(versionLabel, 0, 1, 1, 1);
        gridPane.add(versionField, 1, 1, 1, 1);
        gridPane.add(projectHomeLabel, 0, 2, 1, 1);
        gridPane.add(projectHomeField, 1, 2, 1, 1);
        gridPane.add(forumThreadLabel, 0, 3, 1, 1);
        gridPane.add(forumThreadField, 1, 3, 1, 1);
        gridPane.add(usedLibrariesLabel, 0, 4, 2, 1);
        gridPane.add(librariesArea, 0, 5, 2, 1);
        gridPane.add(usedIcons, 0, 6, 2, 1);
        gridPane.add(iconsArea, 0, 7, 2, 1);

        FxUtils.addClass(root,
                        CssClasses.ABOUT_DIALOG)
                .addClass(gridPane,
                        CssClasses.DEF_GRID_PANE)
                .addClass(usedLibrariesLabel, usedIcons,
                        CssClasses.ABOUT_DIALOG_LONG_LABEL)
                .addClass(versionLabel, projectHomeLabel, forumThreadLabel,
                        CssClasses.SPECIAL_FONT_16)
                .addClass(usedLibrariesLabel, usedIcons, versionField,
                        CssClasses.SPECIAL_FONT_16)
                .addClass(projectHomeField, forumThreadField,
                        CssClasses.SPECIAL_FONT_16)
                .addClass(applicationLabel,
                        CssClasses.ABOUT_DIALOG_TITLE_LABEL);

        FxUtils.addChild(root, gridPane);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.ABOUT_DIALOG_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getButtonOkText() {
        return Messages.SIMPLE_DIALOG_BUTTON_OK;
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
