package com.ss.editor.ui.control.model.property.control.particle.influencer;

import com.jme3.renderer.queue.GeometryList;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.model.property.operation.ParticleInfluencerPropertyOperation;
import com.ss.editor.ui.control.model.tree.dialog.geometry.GeometrySelectorDialog;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.UIUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;
import tonegod.emitter.influencers.impl.PhysicsInfluencer;

import java.util.function.BiConsumer;

/**
 * The control for editing geometry list in the {@link PhysicsInfluencer}.
 *
 * @author JavaSaBr
 */
public class PhysicsNodeListControl extends VBox implements UpdatableControl {

    public static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    /**
     * The consumer of changes.
     */
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The physics influencer.
     */
    private final PhysicsInfluencer influencer;

    /**
     * The parent.
     */
    private final Object parent;

    /**
     * The element container.
     */
    private VBox elementContainer;

    public PhysicsNodeListControl(@NotNull final ModelChangeConsumer modelChangeConsumer,
                                  @NotNull final PhysicsInfluencer influencer,
                                  @NotNull final Object parent) {
        setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_CONTROL);
        this.modelChangeConsumer = modelChangeConsumer;
        this.parent = parent;
        this.influencer = influencer;
        createControls();
    }

    protected void createControls() {

        final Label propertyNameLabel = new Label(getControlTitle() + ":");
        propertyNameLabel.setId(CSSIds.ABSTRACT_PARAM_CONTROL_PARAM_NAME);

        elementContainer = new VBox();

        final Button addButton = new Button();
        addButton.setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_ICON_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_24));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.MODEL_PARAM_CONTROL_INFLUENCER_ICON_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.setDisable(true);

        final HBox buttonContainer = new HBox(addButton, removeButton);
        buttonContainer.setAlignment(Pos.CENTER);

        final ObservableList<Node> children = elementContainer.getChildren();
        children.addListener((ListChangeListener<Node>) c -> removeButton.setDisable(children.size() < (getMinElements() + 1)));

        FXUtils.addToPane(propertyNameLabel, this);
        FXUtils.addToPane(elementContainer, this);
        FXUtils.addToPane(buttonContainer, this);

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addClassTo(addButton, CSSClasses.SPECIAL_FONT_13);
    }

    /**
     * Get the count of minimum elements.
     *
     * @return the count of minimum elements.
     */
    protected int getMinElements() {
        return 0;
    }

    @NotNull
    protected String getControlTitle() {
        return Messages.PARTICLE_EMITTER_INFLUENCER_GEOMETRY_LIST;
    }

    /**
     * @return the influencer.
     */
    @NotNull
    public PhysicsInfluencer getInfluencer() {
        return influencer;
    }

    /**
     * @return the element container.
     */
    @NotNull
    protected VBox getElementContainer() {
        return elementContainer;
    }

    /**
     * @return the consumer of changes.
     */
    @NotNull
    protected ModelChangeConsumer getModelChangeConsumer() {
        return modelChangeConsumer;
    }

    /**
     * Reload this control.
     */
    public void reload() {

        final PhysicsInfluencer influencer = getInfluencer();
        final VBox root = getElementContainer();
        final ObservableList<Node> children = root.getChildren();

        if (isNeedRebuild(influencer, children.size())) {
            UIUtils.clear(root);
            fillControl(influencer, root);
        }
    }

    protected boolean isNeedRebuild(@NotNull final PhysicsInfluencer influencer, final int currentCount) {
        return influencer.getGeometries().size() != currentCount;
    }

    /**
     * Fill this control.
     */
    protected void fillControl(@NotNull final PhysicsInfluencer influencer, @NotNull final VBox root) {

        final GeometryList geometries = influencer.getGeometries();

        for (int i = 0, length = geometries.size(); i < length; i++) {

            final Geometry geometry = geometries.get(i);
            final Label label = new Label(Messages.PARTICLE_EMITTER_INFLUENCER_GEOMETRY + ": " + geometry.getName());
            label.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(label, root);

            FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_13);
        }
    }

    /**
     * Handle removing last interpolation.
     */
    protected void processRemove() {

        final PhysicsInfluencer influencer = getInfluencer();
        final GeometryList geometries = influencer.getGeometries();

        final Geometry geometry = geometries.get(geometries.size() - 1);

        execute(true, false, (physicsInfluencer, needRemove) -> {
            if (needRemove) {
                physicsInfluencer.removeLast();
            } else {
                physicsInfluencer.addCollidable(geometry);
            }
        });
    }

    /**
     * Handle adding new interpolation.
     */
    protected void processAdd() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        final Spatial model = modelChangeConsumer.getCurrentModel();

        final GeometrySelectorDialog dialog = new GeometrySelectorDialog(model, this::processAdd);
        dialog.show(scene.getWindow());
    }

    /**
     * Handle adding new interpolation.
     *
     * @param geometry the selected geometry.
     */
    protected void processAdd(@NotNull final Geometry geometry) {
        execute(true, false, (physicsInfluencer, needAdd) -> {
            if (needAdd) {
                physicsInfluencer.addCollidable(geometry);
            } else {
                physicsInfluencer.removeLast();
            }
        });
    }

    /**
     * Execute change operation.
     *
     * @param newValue     the new value.
     * @param oldValue     the old value.
     * @param applyHandler the apply handler.
     * @param <T>          the type of value.
     */
    protected <T> void execute(@Nullable final T newValue, @Nullable final T oldValue,
                               @NotNull final BiConsumer<PhysicsInfluencer, T> applyHandler) {

        final ParticleInfluencerPropertyOperation<PhysicsInfluencer, T> operation =
                new ParticleInfluencerPropertyOperation<>(influencer, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(applyHandler);

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    @NotNull
    protected String getPropertyName() {
        return getControlTitle();
    }

    @Override
    public void sync() {
        reload();
    }
}
