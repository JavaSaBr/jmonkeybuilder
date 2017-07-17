package com.ss.editor.ui.control.model.property.control.particle.influencer;

import static com.ss.rlib.util.ObjectUtils.notNull;
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
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.ui.util.UIUtils;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tonegod.emitter.influencers.impl.PhysicsInfluencer;

import java.util.function.BiConsumer;

/**
 * The control for editing geometry list in the {@link PhysicsInfluencer}.
 *
 * @author JavaSaBr
 */
public class PhysicsNodeListControl extends VBox implements UpdatableControl {

    /**
     * The constant JFX_APPLICATION.
     */
    @NotNull
    public static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The consumer of changes.
     */
    @NotNull
    private final ModelChangeConsumer modelChangeConsumer;

    /**
     * The physics influencer.
     */
    @NotNull
    private final PhysicsInfluencer influencer;

    /**
     * The parent.
     */
    @NotNull
    private final Object parent;

    /**
     * The element container.
     */
    @Nullable
    private VBox elementContainer;

    /**
     * Instantiates a new Physics node list control.
     *
     * @param modelChangeConsumer the model change consumer
     * @param influencer          the influencer
     * @param parent              the parent
     */
    public PhysicsNodeListControl(@NotNull final ModelChangeConsumer modelChangeConsumer,
                                  @NotNull final PhysicsInfluencer influencer,
                                  @NotNull final Object parent) {
        this.modelChangeConsumer = modelChangeConsumer;
        this.parent = parent;
        this.influencer = influencer;
        createControls();
        FXUtils.addClassesTo(this, CSSClasses.DEF_VBOX, CSSClasses.ABSTRACT_PARAM_CONTROL_INFLUENCER,
                CSSClasses.PHYSICS_NODE_LIST_CONTROL);
    }

    /**
     * Create controls.
     */
    private void createControls() {

        final Label propertyNameLabel = new Label(getControlTitle() + ":");

        elementContainer = new VBox();

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_16));
        addButton.setOnAction(event -> processAdd());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemove());
        removeButton.setDisable(true);

        final HBox buttonContainer = new HBox(addButton, removeButton);

        final ObservableList<Node> children = elementContainer.getChildren();
        children.addListener((ListChangeListener<Node>) c -> removeButton.setDisable(children.size() < (getMinElements() + 1)));

        FXUtils.addToPane(propertyNameLabel, this);
        FXUtils.addToPane(elementContainer, this);
        FXUtils.addToPane(buttonContainer, this);

        FXUtils.addClassTo(propertyNameLabel, CSSClasses.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        FXUtils.addClassTo(addButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);
        FXUtils.addClassTo(buttonContainer, CSSClasses.DEF_HBOX);
        FXUtils.addClassTo(elementContainer, CSSClasses.DEF_VBOX);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Get the count of minimum elements.
     *
     * @return the count of minimum elements.
     */
    protected int getMinElements() {
        return 0;
    }

    /**
     * Gets control title.
     *
     * @return the control title
     */
    @NotNull
    protected String getControlTitle() {
        return Messages.MODEL_PROPERTY_GEOMETRY_LIST;
    }

    /**
     * Gets influencer.
     *
     * @return the influencer.
     */
    @NotNull
    public PhysicsInfluencer getInfluencer() {
        return influencer;
    }

    /**
     * Gets element container.
     *
     * @return the element container.
     */
    @NotNull
    protected VBox getElementContainer() {
        return notNull(elementContainer);
    }

    /**
     * Gets model change consumer.
     *
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

    /**
     * Is need rebuild boolean.
     *
     * @param influencer   the influencer
     * @param currentCount the current count
     * @return the boolean
     */
    protected boolean isNeedRebuild(@NotNull final PhysicsInfluencer influencer, final int currentCount) {
        return influencer.getGeometries().size() != currentCount;
    }

    /**
     * Fill this control.
     *
     * @param influencer the influencer
     * @param root       the root
     */
    protected void fillControl(@NotNull final PhysicsInfluencer influencer, @NotNull final VBox root) {

        final GeometryList geometries = influencer.getGeometries();

        for (int i = 0, length = geometries.size(); i < length; i++) {

            final Geometry geometry = geometries.get(i);
            final Label label = new Label(Messages.MODEL_PROPERTY_GEOMETRY + ": " + geometry.getName());
            label.prefWidthProperty().bind(widthProperty());

            FXUtils.addToPane(label, root);
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
     * @param <T>          the type of value.
     * @param newValue     the new value.
     * @param oldValue     the old value.
     * @param applyHandler the apply handler.
     */
    protected <T> void execute(@Nullable final T newValue, @Nullable final T oldValue,
                               @NotNull final BiConsumer<PhysicsInfluencer, T> applyHandler) {

        final ParticleInfluencerPropertyOperation<PhysicsInfluencer, T> operation =
                new ParticleInfluencerPropertyOperation<>(influencer, parent, getPropertyName(), newValue, oldValue);
        operation.setApplyHandler(applyHandler);

        final ModelChangeConsumer modelChangeConsumer = getModelChangeConsumer();
        modelChangeConsumer.execute(operation);
    }

    /**
     * Gets property name.
     *
     * @return the property name
     */
    @NotNull
    protected String getPropertyName() {
        return getControlTitle();
    }

    @Override
    public void sync() {
        reload();
    }
}
