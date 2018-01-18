package com.ss.editor.ui.control.property;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The component to contains property controls in the editor.
 *
 * @param <C> the type of {@link ChangeConsumer}
 * @author JavaSaBr
 */
public class PropertyEditor<C extends ChangeConsumer> extends ScrollPane {

    @NotNull
    private static final PropertyBuilderRegistry BUILDER_REGISTRY = PropertyBuilderRegistry.getInstance();

    /**
     * The consumer of changes.
     */
    @NotNull
    private final C changeConsumer;

    /**
     * The container of controls.
     */
    @Nullable
    private VBox container;

    /**
     * The current editable object.
     */
    @Nullable
    private Object currentObject;

    /**
     * The current parent.
     */
    @Nullable
    private Object currentParent;

    /**
     * Instantiates a new Abstract property editor.
     *
     * @param changeConsumer the change consumer
     */
    public PropertyEditor(@NotNull final C changeConsumer) {
        this.changeConsumer = changeConsumer;
        createComponents();
    }

    /**
     * @return The container of controls.
     */
    @FxThread
    private @NotNull VBox getContainer() {
        return notNull(container);
    }

    /**
     * Create components.
     */
    @FxThread
    private void createComponents() {
        this.container = new VBox();
        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FXConstants.PROPERTY_LIST_OFFSET));

        final VBox wrapper = new VBox(container);

        FXUtils.addClassTo(this, CssClasses.ABSTRACT_PARAM_CONTROL_CONTAINER);
        FXUtils.addClassTo(container, CssClasses.DEF_VBOX);
        FXUtils.addClassTo(wrapper, CssClasses.DEF_VBOX);

        setContent(wrapper);
    }

    /**
     * Sync all properties with controls.
     *
     * @param object the object
     */
    @FxThread
    public void syncFor(@Nullable final Object object) {
        if (!isNeedUpdate(object)) return;

        final VBox container = getContainer();
        container.setDisable(object == null || !canEdit(object, getCurrentParent()));

        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {
            if (node instanceof UpdatableControl) {
                ((UpdatableControl) node).sync();
            }
        });
    }

    /**
     * Sync all properties with controls.
     */
    @FxThread
    public void refresh() {

        final Object object = getCurrentObject();
        if (object == null) return;

        final VBox container = getContainer();
        container.setDisable(!canEdit(object, getCurrentParent()));

        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {
            if (node instanceof UpdatableControl) {
                ((UpdatableControl) node).sync();
            }
        });
    }

    /**
     * Build property controls for the object.
     *
     * @param object the object
     * @param parent the parent
     */
    @FxThread
    public void buildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            BUILDER_REGISTRY.buildFor(object, parent, container, changeConsumer);
        }

        container.setDisable(object == null || !canEdit(object, parent));

        setCurrentObject(object);
        setCurrentParent(parent);
    }

    /**
     * Can edit boolean.
     *
     * @param object the object to edit.
     * @param parent the parent.
     * @return true if we can edit properties of the object.
     */
    @FxThread
    protected boolean canEdit(@NotNull final Object object, @Nullable final Object parent) {
        return true;
    }

    /**
     * Re-build property controls for the object.
     *
     * @param object the object
     * @param parent the parent
     */
    @FxThread
    public void rebuildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() != object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            BUILDER_REGISTRY.buildFor(object, parent, container, changeConsumer);
        }
    }

    /**
     * Rebuild this editor.
     */
    @FxThread
    public void rebuild() {
        rebuildFor(getCurrentObject(), null);
    }

    /**
     * Is need update boolean.
     *
     * @param object the object
     * @return the boolean
     */
    @FxThread
    protected boolean isNeedUpdate(@Nullable final Object object) {
        final Object currentObject = getCurrentObject();
        if (object instanceof EditableProperty) {
            return currentObject == ((EditableProperty) object).getObject();
        }
        return currentObject == object;
    }

    /**
     * @param currentObject the current editable object.
     */
    @FxThread
    private void setCurrentObject(@Nullable final Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * Gets current object.
     *
     * @return the current editable object.
     */
    @FxThread
    protected @Nullable Object getCurrentObject() {
        return currentObject;
    }

    /**
     * Sets the current parent.
     *
     * @param currentParent the current parent.
     */
    @FxThread
    protected void setCurrentParent(@Nullable final Object currentParent) {
        this.currentParent = currentParent;
    }

    /**
     * Gets the current parent.
     *
     * @return the current parent.
     */
    @FxThread
    protected @Nullable Object getCurrentParent() {
        return currentParent;
    }
}
