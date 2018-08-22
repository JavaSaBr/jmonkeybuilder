package com.ss.builder.fx.control.property;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.FxConstants;
import com.ss.builder.fx.control.property.builder.PropertyBuilderRegistry;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.fx.FxConstants;
import com.ss.builder.fx.control.UpdatableControl;
import com.ss.builder.fx.control.property.builder.PropertyBuilderRegistry;
import com.ss.builder.fx.css.CssClasses;
import com.ss.rlib.fx.util.FxUtils;
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

    /**
     * The consumer of changes.
     */
    @NotNull
    private final C changeConsumer;

    /**
     * The container of controls.
     */
    @NotNull
    private final VBox container;

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
        this.container = new VBox();
        construct();
    }

    /**
     * Finish constructing this property editor.
     */
    @FxThread
    public void construct() {

        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FxConstants.PROPERTY_LIST_OFFSET));

        var wrapper = new VBox(container);

        FxUtils.addClass(this, CssClasses.PROPERTY_EDITOR)
                .addClass(wrapper, container, CssClasses.DEF_VBOX, CssClasses.PROPERTY_EDITOR_CONTAINER);

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

        var children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderRegistry.getInstance()
                    .buildFor(object, parent, container, changeConsumer);
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

        var children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderRegistry.getInstance()
                    .buildFor(object, parent, container, changeConsumer);
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
     * @param changedObject the changed object.
     * @return true if need to update property controls.
     */
    @FxThread
    protected boolean isNeedUpdate(@Nullable Object changedObject) {

        var currentObject = getCurrentObject();
        if (changedObject instanceof EditableProperty) {
            return currentObject == ((EditableProperty) changedObject).getObject();
        }

        return currentObject == changedObject;
    }

    /**
     * Set the current editable object.
     *
     * @param currentObject the current editable object.
     */
    @FxThread
    private void setCurrentObject(@Nullable Object currentObject) {
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
