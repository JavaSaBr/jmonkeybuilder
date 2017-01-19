package com.ss.editor.ui.control.app.state.property;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.UpdatableControl;
import com.ss.editor.ui.control.app.state.property.builder.PropertyBuilder;
import com.ss.editor.ui.css.CSSIds;
import com.ss.extension.scene.app.state.EditableSceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The component for containing property controls in the editor.
 *
 * @author JavaSaBr
 */
public class AppStatePropertyEditor extends ScrollPane {

    private static final PropertyBuilder PROPERTY_BUILDER = PropertyBuilder.getInstance();

    /**
     * The consumer of changes.
     */
    @NotNull
    private final SceneChangeConsumer changeConsumer;

    /**
     * The container of controls.
     */
    private VBox container;

    /**
     * The current editable state.
     */
    private EditableSceneAppState currentState;

    public AppStatePropertyEditor(@NotNull final SceneChangeConsumer changeConsumer) {
        this.changeConsumer = changeConsumer;
        createComponents();
    }

    /**
     * @return The container of controls.
     */
    private VBox getContainer() {
        return container;
    }

    /**
     * Create components.
     */
    private void createComponents() {
        container = new VBox();
        container.setId(CSSIds.MODEL_PARAM_CONTROL_CONTAINER);
        container.prefWidthProperty().bind(widthProperty());
        setContent(new VBox(container));
    }

    /**
     * Build property controls for the object.
     */
    public void buildFor(@Nullable final EditableSceneAppState object) {
        if (getCurrentState() == object) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.clear();

        if (object != null) {
            PROPERTY_BUILDER.buildFor(object, container, changeConsumer);
        }

        setCurrentState(object);
    }

    /**
     * Sync all properties with controls.
     */
    public void syncFor(@Nullable final EditableSceneAppState object) {
        if (!isNeedUpdate(object)) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();
        children.forEach(node -> {
            if (node instanceof UpdatableControl) {
                ((UpdatableControl) node).sync();
            }
        });
    }

    protected boolean isNeedUpdate(@Nullable final EditableSceneAppState object) {
        final Object currentObject = getCurrentState();
        return currentObject == object;
    }

    /**
     * @param currentState the current editable object.
     */
    private void setCurrentState(@Nullable final EditableSceneAppState currentState) {
        this.currentState = currentState;
    }

    /**
     * @return the current editable object.
     */
    @Nullable
    private EditableSceneAppState getCurrentState() {
        return currentState;
    }
}
