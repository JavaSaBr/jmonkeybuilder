package com.ss.editor.ui.control.property;

import static java.util.Objects.requireNonNull;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.property.builder.PropertyBuilderFactory;
import com.ss.editor.ui.css.CSSIds;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPropertyEditor<C extends ChangeConsumer> extends ScrollPane {

    private static final int WIDTH_OFFSET = 4;

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

    public AbstractPropertyEditor(@NotNull final C changeConsumer) {
        this.changeConsumer = changeConsumer;
        createComponents();
    }

    /**
     * @return The container of controls.
     */
    @NotNull
    private VBox getContainer() {
        return requireNonNull(container);
    }

    /**
     * Create components.
     */
    private void createComponents() {
        container = new VBox();
        container.setId(CSSIds.ABSTRACT_PARAM_CONTROL_CONTAINER);
        container.prefWidthProperty().bind(widthProperty().subtract(WIDTH_OFFSET));
        setContent(new VBox(container));
    }

    /**
     * Sync all properties with controls.
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
     * @param object the object to edit.
     * @return true if we can edit properties of the object.
     */
    protected boolean canEdit(@NotNull final Object object) {
        return true;
    }

    /**
     * Re-build property controls for the object.
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
     * @return the current editable object.
     */
    @Nullable
    protected Object getCurrentObject() {
        return currentObject;
    }
}
