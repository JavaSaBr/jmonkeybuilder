package com.ss.editor.ui.control.property;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.FXConstants;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilderFactory;
import com.ss.editor.ui.css.CSSClasses;
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
public abstract class AbstractPropertyEditor<C extends ChangeConsumer> extends ScrollPane {


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
     * Instantiates a new Abstract property editor.
     *
     * @param changeConsumer the change consumer
     */
    public AbstractPropertyEditor(@NotNull final C changeConsumer) {
        this.changeConsumer = changeConsumer;
        createComponents();
    }

    /**
     * @return The container of controls.
     */
    @NotNull
    private VBox getContainer() {
        return notNull(container);
    }

    /**
     * Create components.
     */
    private void createComponents() {
        this.container = new VBox();
        this.container.prefWidthProperty()
                .bind(widthProperty().subtract(FXConstants.PROPERTY_LIST_OFFSET));

        final VBox wrapper = new VBox(container);

        FXUtils.addClassTo(this, CSSClasses.ABSTRACT_PARAM_CONTROL_CONTAINER);
        FXUtils.addClassTo(container, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(wrapper, CSSClasses.DEF_VBOX);

        setContent(wrapper);
    }

    /**
     * Sync all properties with controls.
     *
     * @param object the object
     */
    public void syncFor(@Nullable final Object object) {
        if (!isNeedUpdate(object)) return;

        final VBox container = getContainer();
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
    public void buildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, parent, container, changeConsumer);
        }

        container.setDisable(object == null || !canEdit(object));

        setCurrentObject(object);
    }

    /**
     * Can edit boolean.
     *
     * @param object the object to edit.
     * @return true if we can edit properties of the object.
     */
    protected boolean canEdit(@NotNull final Object object) {
        return true;
    }

    /**
     * Re-build property controls for the object.
     *
     * @param object the object
     * @param parent the parent
     */
    public void rebuildFor(@Nullable final Object object, @Nullable final Object parent) {
        if (getCurrentObject() != object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PropertyBuilderFactory.buildFor(object, parent, container, changeConsumer);
        }
    }

    /**
     * Rebuild this editor.
     */
    public void rebuild() {
        rebuildFor(getCurrentObject(), null);
    }

    /**
     * Is need update boolean.
     *
     * @param object the object
     * @return the boolean
     */
    protected boolean isNeedUpdate(@Nullable final Object object) {
        final Object currentObject = getCurrentObject();
        return currentObject == object;
    }

    /**
     * @param currentObject the current editable object.
     */
    private void setCurrentObject(@Nullable final Object currentObject) {
        this.currentObject = currentObject;
    }

    /**
     * Gets current object.
     *
     * @return the current editable object.
     */
    @Nullable
    protected Object getCurrentObject() {
        return currentObject;
    }
}
