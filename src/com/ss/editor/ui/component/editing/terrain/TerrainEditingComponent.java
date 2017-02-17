package com.ss.editor.ui.component.editing.terrain;

import static rlib.util.array.ArrayFactory.toArray;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editing.impl.AbstractEditingComponent;
import com.ss.editor.ui.css.CSSClasses;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The implementation of a terrain editor.
 *
 * @author JavaSaBr
 */
public class TerrainEditingComponent extends AbstractEditingComponent<TerrainQuad> {

    /**
     * The list of all tool controls.
     */
    @NotNull
    private final Array<TerrainToolControl> toolControls;

    /**
     * The list of all toggle buttons.
     */
    @NotNull
    private final Array<ToggleButton> toggleButtons;

    /**
     * The map with mapping toggle button to terrain control.
     */
    @NotNull
    private final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl;

    /**
     * The control to raise/lowe terrain.
     */
    @NotNull
    private final RaiseLowerTerrainToolControl raiseLowerToolControl;

    /**
     * The control to smooth terrain.
     */
    @NotNull
    private final SmoothTerrainToolControl smoothToolControl;

    /**
     * The control to make rough surface terrain.
     */
    @NotNull
    private final RoughTerrainToolControl roughToolControl;

    /**
     * The control to make some levels terrain.
     */
    @NotNull
    private final LevelTerrainToolControl levelToolControl;

    /**
     * The control to slope terrain.
     */
    @NotNull
    private final SlopeTerrainToolControl slopeToolControl;

    /**
     * The control to paint on terrain.
     */
    @NotNull
    private final PaintTerrainToolControl paintToolControl;

    /**
     * The button to enable/disable raise terrain mode.
     */
    @Nullable
    private ToggleButton raiseLowerButton;

    @Nullable
    private ToggleButton smoothButton;

    @Nullable
    private ToggleButton roughButton;

    @Nullable
    private ToggleButton levelButton;

    @Nullable
    private ToggleButton slopeButton;

    @Nullable
    private ToggleButton paintButton;

    /**
     * The current tool control.
     */
    @Nullable
    private TerrainToolControl toolControl;

    public TerrainEditingComponent() {
        this.buttonToControl = DictionaryFactory.newObjectDictionary();
        this.raiseLowerToolControl = new RaiseLowerTerrainToolControl(this);
        this.smoothToolControl = new SmoothTerrainToolControl(this);
        this.roughToolControl = new RoughTerrainToolControl(this);
        this.levelToolControl = new LevelTerrainToolControl(this);
        this.slopeToolControl = new SlopeTerrainToolControl(this);
        this.paintToolControl = new PaintTerrainToolControl(this);
        this.toolControls = ArrayFactory.newArray(TerrainToolControl.class);
        this.toggleButtons = ArrayFactory.newArray(ToggleButton.class);
        this.toolControls.addAll(toArray(raiseLowerToolControl, smoothToolControl, roughToolControl,
                levelToolControl, slopeToolControl, paintToolControl));
        this.toggleButtons.addAll(toArray(raiseLowerButton, smoothButton, roughButton, levelButton,
                slopeButton, paintButton));

        buttonToControl.put(raiseLowerButton, raiseLowerToolControl);
        buttonToControl.put(smoothButton, smoothToolControl);
        buttonToControl.put(roughButton, roughToolControl);
        buttonToControl.put(levelButton, levelToolControl);
        buttonToControl.put(slopeButton, slopeToolControl);
        buttonToControl.put(paintButton, paintToolControl);

        raiseLowerButton.setSelected(true);

        setToolControl(raiseLowerToolControl);
    }

    /**
     * @return the list of all tool controls.
     */
    @NotNull
    private Array<TerrainToolControl> getToolControls() {
        return toolControls;
    }

    /**
     * @return the list of all toggle buttons.
     */
    @NotNull
    private Array<ToggleButton> getToggleButtons() {
        return toggleButtons;
    }

    /**
     * @return the map with mapping toggle button to terrain control.
     */
    @NotNull
    private ObjectDictionary<ToggleButton, TerrainToolControl> getButtonToControl() {
        return buttonToControl;
    }

    /**
     * @return the current tool control.
     */
    @Nullable
    private TerrainToolControl getToolControl() {
        return toolControl;
    }

    /**
     * @param toolControl the current tool control.
     */
    private void setToolControl(@Nullable final TerrainToolControl toolControl) {
        this.toolControl = toolControl;
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        raiseLowerButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_UP_16));
        raiseLowerButton.setOnAction(this::switchMode);

        smoothButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SMOOTH_16));
        smoothButton.setOnAction(this::switchMode);

        roughButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_ROUGH_16));
        roughButton.setOnAction(this::switchMode);

        levelButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_LEVEL_16));
        levelButton.setOnAction(this::switchMode);

        slopeButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_SLOPE_16));
        slopeButton.setOnAction(this::switchMode);

        paintButton = new ToggleButton(StringUtils.EMPTY, new ImageView(Icons.TERRAIN_PAINT_16));
        paintButton.setOnAction(this::switchMode);

        final GridPane buttonsContainer = new GridPane();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setPadding(new Insets(2, 4, 2, 4));
        buttonsContainer.add(raiseLowerButton, 0, 0);
        buttonsContainer.add(smoothButton, 1, 0);
        buttonsContainer.add(roughButton, 2, 0);
        buttonsContainer.add(levelButton, 3, 0);
        buttonsContainer.add(slopeButton, 4, 0);
        buttonsContainer.add(paintButton, 5, 0);
        buttonsContainer.prefWidthProperty().bind(widthProperty());

        FXUtils.addToPane(buttonsContainer, this);

        FXUtils.addClassTo(raiseLowerButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(raiseLowerButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(smoothButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(smoothButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(roughButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(roughButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(levelButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(levelButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(slopeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(slopeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
        FXUtils.addClassTo(paintButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(paintButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);
    }

    /**
     * Switch editing mode.
     */
    private void switchMode(@NotNull final ActionEvent event) {

        final ToggleButton source = (ToggleButton) event.getSource();

        getToggleButtons().forEach(source, (button, arg) -> button !=
                arg, (toggleButton, arg) -> toggleButton.setSelected(false));

        final ObjectDictionary<ToggleButton, TerrainToolControl> buttonToControl = getButtonToControl();
        final TerrainToolControl toolControl = buttonToControl.get(source);

        setToolControl(toolControl);

        if (!isShowed()) return;

        final Node cursorNode = getCursorNode();
        cursorNode.removeControl(TerrainToolControl.class);
        cursorNode.addControl(toolControl);
    }

    @Override
    public void stopEditing() {
        super.stopEditing();
    }

    @Override
    public void startEditing(@NotNull final Object object) {
        super.startEditing(object);
    }

    @Override
    public boolean isSupport(@NotNull final Object object) {
        return object instanceof TerrainQuad;
    }

    @Override
    public void notifyShowed() {
        super.notifyShowed();
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCursorNode().addControl(getToolControl()));
    }

    @Override
    public void notifyHided() {
        super.notifyHided();
        EXECUTOR_MANAGER.addEditorThreadTask(() -> getCursorNode().removeControl(TerrainToolControl.class));
    }

    @NotNull
    @Override
    public String getName() {
        return "Terrain editor";
    }
}
