package com.ss.editor.ui.component.bar;

import static javafx.util.Duration.millis;

import com.ss.editor.JFXApplication;
import com.ss.editor.config.Config;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.builder.EditorFXSceneBuilder;
import com.ss.editor.ui.component.ScreenComponent;
import com.ss.editor.ui.component.bar.action.CloseEditorAction;
import com.ss.editor.ui.component.bar.action.OpenAssetAction;
import com.ss.editor.ui.component.bar.action.OpenSettingsAction;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rlib.ui.hanlder.WindowDragHandler;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The toolbar of the Editor.
 *
 * @author JavaSaBr
 */
public class EditorBarComponent extends StackPane implements ScreenComponent {

    public static final String COMPONENT_ID = "EditorBarComponent";

    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    private static final int MENU_ANIMATION_DURATION = 200;

    /**
     * The animation of opening the menu.
     */
    private TranslateTransition openMenuAnimation;

    /**
     * The animation of closing the menu.
     */
    private TranslateTransition closeMenuAnimation;

    /**
     * The animation of showing the background.
     */
    protected FadeTransition showBackgroundAnimation;

    /**
     * The animation of hiding the background.
     */
    protected FadeTransition hideBackgroundAnimation;

    /**
     * The background.
     */
    private StackPane drawerContainer;

    /**
     * The menu.
     */
    private VBox drawer;

    /**
     * The button for switching fullscreen mode.
     */
    private Button fullscreenButton;

    public EditorBarComponent() {
        super();
        setId(CSSIds.EDITOR_BAR_COMPONENT);
        createComponents();
        setOnMouseClicked(this::processMaximize);
        WindowDragHandler.install(this);
    }

    private void createComponents() {

        final HBox menuContainer = new HBox();
        menuContainer.setPickOnBounds(false);
        menuContainer.setAlignment(Pos.CENTER_LEFT);

        final HBox actionsContainer = new HBox();
        actionsContainer.setPickOnBounds(false);
        actionsContainer.setAlignment(Pos.CENTER_RIGHT);

        final Button menuButton = new Button();
        menuButton.setId(CSSIds.EDITOR_BAR_COMPONENT_MENU_BUTTON);
        menuButton.setOnAction(event -> processMenu());

        final Label titleLabel = new Label(Config.TITLE + " " + Config.VERSION);
        titleLabel.setId(CSSIds.EDITOR_BAR_COMPONENT_TITLE_LABEL);

        fullscreenButton = new Button();
        fullscreenButton.setId(CSSIds.EDITOR_BAR_COMPONENT_FULLSCREEN_BUTTON);
        fullscreenButton.setOnAction(event -> processMaximize());
        fullscreenButton.setGraphic(new ImageView());

        final Button closeButton = new Button();
        closeButton.setId(CSSIds.EDITOR_BAR_COMPONENT_CLOSE_BUTTON);
        closeButton.setOnAction(event -> JFX_APPLICATION.onExit());

        FXUtils.addClassTo(menuButton, CSSClasses.EDITOR_BAR_BUTTON);
        FXUtils.addClassTo(titleLabel, CSSClasses.MAIN_FONT_15);
        FXUtils.addClassTo(fullscreenButton, CSSClasses.EDITOR_BAR_BUTTON);
        FXUtils.addClassTo(closeButton, CSSClasses.EDITOR_BAR_BUTTON);

        FXUtils.addToPane(menuButton, menuContainer);
        FXUtils.addToPane(titleLabel, menuContainer);
        FXUtils.addToPane(fullscreenButton, actionsContainer);
        FXUtils.addToPane(closeButton, actionsContainer);
        FXUtils.addToPane(menuContainer, this);
        FXUtils.addToPane(actionsContainer, this);
    }

    /**
     * Create a drawer.
     */
    public void createDrawer(final StackPane container, final Stage stage) {

        heightProperty().addListener((observable, oldValue, newValue) ->
                StackPane.setMargin(drawerContainer, new Insets(getHeight(), 0, 0, 0)));

        drawerContainer = new StackPane();
        drawerContainer.setId(CSSIds.EDITOR_BAR_DRAWER_CONTAINER);
        drawerContainer.prefHeightProperty().bind(container.heightProperty().subtract(heightProperty()));
        drawerContainer.maxHeightProperty().bind(container.heightProperty().subtract(heightProperty()));
        drawerContainer.prefWidthProperty().bind(container.widthProperty());
        drawerContainer.setOnMouseClicked(event -> processMenu());
        drawerContainer.setVisible(false);

        drawer = new VBox();
        drawer.setId(CSSIds.EDITOR_BAR_DRAWER);
        drawer.prefHeightProperty().bind(drawerContainer.heightProperty());
        drawer.setOnMouseClicked(Event::consume);

        final Array<Node> nodes = ArrayFactory.newArray(Node.class);
        createAssetMenu(nodes);
        createSettingsMenu(nodes);
        nodes.add(new CloseEditorAction());

        nodes.forEach(node -> FXUtils.addToPane(node, drawer));
        nodes.stream().filter(node -> node instanceof Control)
                .map(toControl -> (Control) toControl)
                .forEach(this::prepareControl);

        createOpenMenuAnimation();
        createCloseMenuAnimation();
        createShowBackgroundAnimation();
        createHideBackgroundAnimation();

        FXUtils.addToPane(drawerContainer, container);
        FXUtils.addToPane(drawer, drawerContainer);

        stage.maximizedProperty().addListener((observable, oldValue, newValue) ->
                updateFullscreenButton(newValue));

        updateFullscreenButton(stage.isMaximized());

        StackPane.setMargin(drawerContainer, EditorFXSceneBuilder.BAR_OFFSET);
    }

    /**
     * Update an icon of the fullscreen button.
     *
     * @param newValue the new value of maximizing.
     */
    protected void updateFullscreenButton(final Boolean newValue) {
        final ImageView imageView = (ImageView) fullscreenButton.getGraphic();
        if (newValue) {
            imageView.setImage(Icons.FROM_FULLSCREEN_24);
        } else {
            imageView.setImage(Icons.TO_FULLSCREEN_24);
        }
    }

    /**
     * Prepate a drawer's control.
     */
    protected void prepareControl(final Control control) {

        control.prefWidthProperty().bind(drawer.widthProperty());
        control.addEventHandler(ActionEvent.ACTION, event -> processMenu());

        FXUtils.addClassTo(control, CSSClasses.DRAWER_BUTTON);
        FXUtils.addClassTo(control, CSSClasses.SPECIAL_FONT_15);
    }

    private void createCloseMenuAnimation() {
        closeMenuAnimation = new TranslateTransition();
        closeMenuAnimation.setNode(drawer);
        closeMenuAnimation.setDuration(millis(MENU_ANIMATION_DURATION));
        closeMenuAnimation.setFromX(0);
        closeMenuAnimation.toXProperty().bind(drawer.widthProperty().negate());
        closeMenuAnimation.setOnFinished(event -> drawerContainer.setVisible(false));
    }

    private void createOpenMenuAnimation() {
        openMenuAnimation = new TranslateTransition();
        openMenuAnimation.setNode(drawer);
        openMenuAnimation.setDuration(millis(MENU_ANIMATION_DURATION));
        openMenuAnimation.fromXProperty().bind(drawer.widthProperty().negate());
        openMenuAnimation.setToX(0);
    }

    protected void createShowBackgroundAnimation() {
        showBackgroundAnimation = new FadeTransition();
        showBackgroundAnimation.setNode(drawerContainer);
        showBackgroundAnimation.setDuration(millis(MENU_ANIMATION_DURATION));
        showBackgroundAnimation.setFromValue(0);
        showBackgroundAnimation.setToValue(1);
    }

    protected void createHideBackgroundAnimation() {
        hideBackgroundAnimation = new FadeTransition();
        hideBackgroundAnimation.setNode(drawerContainer);
        hideBackgroundAnimation.setDuration(millis(MENU_ANIMATION_DURATION));
        hideBackgroundAnimation.setFromValue(1);
        hideBackgroundAnimation.setToValue(0);
    }

    private void createAssetMenu(final Array<Node> nodes) {
        final Button openAssetItem = new OpenAssetAction();
        //FIXME final MenuItem reopenAssetItem = new ReopenAssetMenu();
        nodes.add(openAssetItem);
    }

    private void createSettingsMenu(final Array<Node> nodes) {
        final Button graphicSettings = new OpenSettingsAction();
        nodes.add(graphicSettings);
    }

    private void processMenu() {
        if (drawerContainer.isVisible()) {
            hideBackgroundAnimation.play();
            closeMenuAnimation.play();
        } else {
            openMenuAnimation.play();
            showBackgroundAnimation.play();
            drawerContainer.setVisible(true);
        }
    }

    private void processMaximize(final MouseEvent event) {
        if (event.getClickCount() != 2) return;
        processMaximize();
    }

    private void processMaximize() {
        final Stage window = (Stage) getScene().getWindow();
        window.setMaximized(!window.isMaximized());
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }
}
