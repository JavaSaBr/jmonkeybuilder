package com.ss.editor.ui.dialog.about;

import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.Config;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import javafx.application.HostServices;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.InputStream;

/**
 * The dialog about this editor.
 *
 * @author JavaSaBr
 */
public class AboutDialog extends AbstractSimpleEditorDialog {

    private static final int AREA_HEIGHT = 200;

    @NotNull
    private static final String PROJECT_HOME = "https://bitbucket.org/JavaSabr/jme3-spaceshift-editor";

    @NotNull
    private static final String FORUM_THREAD = "https://hub.jmonkeyengine.org/t/jme3-spaceshift-editor/35179/";

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, -1);

    @NotNull
    private static final String ICONS;

    @NotNull
    private static final String LIBRARIES;


    static {
        final InputStream iconsResource = FileCreator.class.getResourceAsStream("/credits/icons.txt");
        final InputStream librariesResource = FileCreator.class.getResourceAsStream("/credits/libraries.txt");
        ICONS = FileUtils.read(iconsResource);
        LIBRARIES = FileUtils.read(librariesResource);
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        final JFXApplication application = JFXApplication.getInstance();
        final HostServices hostServices = application.getHostServices();

        final GridPane gridPane = new GridPane();
        gridPane.setId(CSSIds.ABOUT_DIALOG_GRID_PANE);

        final Label applicationLabel = new Label(Config.TITLE);
        applicationLabel.setId(CSSIds.ABOUT_DIALOG_TITLE_LABEL);
        applicationLabel.setGraphic(new ImageView(Icons.APPLICATION_64));

        final Label versionLabel = new Label(Messages.ABOUT_DIALOG_VERSION + ":");
        versionLabel.setId(CSSIds.ABOUT_DIALOG_LABEL);
        versionLabel.prefWidthProperty().bind(gridPane.widthProperty().multiply(0.5));

        final Label versionField = new Label(Config.VERSION);

        final Label projectHomeLabel = new Label(Messages.ABOUT_DIALOG_PROJECT_HOME + ":");
        projectHomeLabel.setId(CSSIds.ABOUT_DIALOG_LABEL);

        final Hyperlink projectHomeField = new Hyperlink("bitbucket.org");
        projectHomeField.setOnAction(event -> hostServices.showDocument(PROJECT_HOME));

        final Label forumThreadLabel = new Label(Messages.ABOUT_DIALOG_FORUM_THREAD + ":");
        forumThreadLabel.setId(CSSIds.ABOUT_DIALOG_LABEL);

        final Hyperlink forumThreadField = new Hyperlink("hub.jmonkeyengine.org");
        forumThreadField.setOnAction(event -> hostServices.showDocument(FORUM_THREAD));

        final Label usedLibrariesLabel = new Label(Messages.ABOUT_DIALOG_USED_LIBRARIES + ":");
        usedLibrariesLabel.prefWidthProperty().bind(gridPane.widthProperty());
        usedLibrariesLabel.setId(CSSIds.ABOUT_DIALOG_LONG_LABEL);

        final Label usedIcons = new Label(Messages.ABOUT_DIALOG_USED_ICONS + ":");
        usedIcons.prefWidthProperty().bind(gridPane.widthProperty());
        usedIcons.setId(CSSIds.ABOUT_DIALOG_LONG_LABEL);

        final TextArea librariesArea = new TextArea(LIBRARIES);
        librariesArea.setEditable(false);

        final TextArea iconsArea = new TextArea(ICONS);
        iconsArea.setEditable(false);

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

        FXUtils.addToPane(gridPane, root);

        FXUtils.setFixedHeight(librariesArea, AREA_HEIGHT);
        FXUtils.setFixedHeight(iconsArea, AREA_HEIGHT);

        FXUtils.addClassTo(versionLabel, projectHomeLabel, forumThreadLabel, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(versionField, projectHomeField, forumThreadField, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(librariesArea, iconsArea, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(usedLibrariesLabel, usedIcons, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(applicationLabel, CSSClasses.SPECIAL_FONT_22);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.ABOUT_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected String getButtonOkLabel() {
        return Messages.ABOUT_DIALOG_BUTTON_OK;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
