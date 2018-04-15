package com.ss.editor.ui.control.property;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilderRegistry;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.fx.util.FXUtils;
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

    public PropertyEditor(@NotNull C changeConsumer) {
        this.changeConsumer = changeConsumer;
        createComponents();
    }

    /**
     * Get the container of controls.
     *
     * @return the container of controls.
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

        var wrapper = new VBox(container);

        FXUtils.addClassTo(this, CssClasses.ABSTRACT_PARAM_CONTROL_CONTAINER);
        FXUtils.addClassesTo(wrapper, container, CssClasses.DEF_VBOX);

        setContent(wrapper);
    }

    /**
     * Sync all property controls.
     *
     * @param object the object.
     */
    @FxThread
    public void syncFor(@Nullable Object object) {

        if (!isNeedUpdate(object)) {
            return;
        }

        var container = getContainer();
        container.setDisable(object == null || !canEdit(object, getCurrentParent()));
        container.getChildren().forEach(node -> {
            if (node instanceof UpdatableControl) {
                ((UpdatableControl) node).sync();
            }
        });
    }

    /**
     * Sync all property controls.
     */
    @FxThread
    public void refresh() {

        var object = getCurrentObject();
        if (object == null) {
            return;
        }

        var container = getContainer();
        container.setDisable(!canEdit(object, getCurrentParent()));
        container.getChildren().forEach(node -> {
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
    public void buildFor(@Nullable Object object, @Nullable Object parent) {

        if (getCurrentObject() == object) {
            return;
        }

        var container = getContainer();
        var children = container.getChildren();
        children.clear();

        if (object != null) {
            BUILDER_REGISTRY.buildFor(object, parent, container, changeConsumer);
        }

        container.setDisable(object == null || !canEdit(object, parent));

        setCurrentObject(object);
        setCurrentParent(parent);
    }

    /**
     * Return true if we can edit properties of the object.
     *
     * @param object the object to edit.
     * @param parent the parent.
     * @return true if we can edit properties of the object.
     */
    @FxThread
    protected boolean canEdit(@NotNull Object object, @Nullable Object parent) {
        return true;
    }

    /**
     * Re-build property controls for the object.
     *
     * @param object the object.
     * @param parent the parent.
     */
    @FxThread
    public void rebuildFor(@Nullable Object object, @Nullable Object parent) {

        if (getCurrentObject() != object) {
            return;
        }

        var container = getContainer();
        var children = container.getChildren();
        children.clear();

        if (object != null) {
            BUILDER_REGISTRY.buildFor(object, parent, container, changeConsumer);
        }
    }

    /**
     * Rebuild all property controls.
     */
    @FxThread
    public void rebuild() {
        rebuildFor(getCurrentObject(), null);
    }

    /**
     * Return true if need to update property controls.
     *
     * @param object the object.
     * @return true if need to update property controls.
     */
    @FxThread
    protected boolean isNeedUpdate(@Nullable Object object) {

        var currentObject = getCurrentObject();
        if (object instanceof EditableProperty) {
            return currentObject == ((EditableProperty) object).getObject();
        }

        return currentObject == object;
    }

    /**
     * Set the current editable object.
     *
     * @param currentObject the current editable object.
     */
    @FxThread
    private void setCurrentObject(@Nullable final Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * Get the current object.
     *
     * @return the current editable object.
     */
    @FxThread
    protected @Nullable Object getCurrentObject() {
        return currentObject;
    }

    /**
     * Set the current parent.
     *
     * @param currentParent the current parent.
     */
    @FxThread
    protected void setCurrentParent(@Nullable Object currentParent) {
        this.currentParent = currentParent;
    }

    /**
     * Get the current parent.
     *
     * @return the current parent.
     */
    @FxThread
    protected @Nullable Object getCurrentParent() {
        return currentParent;
    }
}
