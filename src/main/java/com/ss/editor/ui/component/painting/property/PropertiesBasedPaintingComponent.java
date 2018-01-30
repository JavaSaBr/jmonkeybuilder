package com.ss.editor.ui.component.painting.property;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.control.painting.PaintingControl;
import com.ss.editor.plugin.api.property.control.PropertyEditorControl;
import com.ss.editor.plugin.api.property.control.PropertyEditorControlFactory;
import com.ss.editor.ui.component.painting.PaintingComponentContainer;
import com.ss.editor.ui.component.painting.impl.AbstractPaintingComponent;
import com.ss.editor.ui.component.painting.impl.AbstractPaintingStateWithEditorTool;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

/**
 * The properties based implementation of painting component.
 *
 * @param <T> the painted object's type.
 * @param <S> the component state's type.
 * @param <C> the painting control's type.
 * @author JavaSaBr
 */
public abstract class PropertiesBasedPaintingComponent<T, S extends AbstractPaintingStateWithEditorTool, C extends PaintingControl> extends
        AbstractPaintingComponent<T, S, C> {

    @NotNull
    protected static final Array<PaintingPropertyDefinition> EMPTY_PROPERTIES = ArrayFactory.newArray(PaintingPropertyDefinition.class);

    /**
     * The map of category to properties container.
     */
    @NotNull
    private ObjectDictionary<String, VBox> propertyContainers;

    /**
     * The property variables.
     */
    @NotNull
    private VarTable vars;

    public PropertiesBasedPaintingComponent(@NotNull final PaintingComponentContainer container) {
        super(container);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        this.propertyContainers = DictionaryFactory.newObjectDictionary();
        this.vars = VarTable.newInstance();

        final Runnable callback = this::syncValues;
        final GridPane settings = createBrushSettings();

        for (final PaintingPropertyDefinition definition : getPaintingProperties()) {

            final PropertyEditorControl<?> control = buildControl(definition, callback);
            final VBox container = propertyContainers.get(definition.getCategory(), this::createContainer);

            FXUtils.addToPane(control, container);
        }

        FXUtils.addToPane(settings, this);
    }

    /**
     * Show properties of the category.
     *
     * @param category the category.
     */
    @FxThread
    protected void showCategory(@NotNull final String category) {
        final ObservableList<Node> children = getChildren();
        children.removeIf(VBox.class::isInstance);
        children.add(propertyContainers.get(category));
    }

    /**
     * Get the property variables.
     *
     * @return the property variables.
     */
    @FxThread
    protected @NotNull VarTable getVars() {
        return vars;
    }

    /**
     * Synchronize values from properties.
     */
    @FxThread
    protected void syncValues() {

    }

    /**
     * Create a container for the category.
     *
     * @param category the category.
     */
    @FxThread
    private VBox createContainer(@NotNull final String category) {
        final VBox container = new VBox();
        return container;
    }

    /**
     * Build a property control for the definition.
     *
     * @param definition the property definition.
     * @return the property control.
     */
    @FxThread
    protected @NotNull PropertyEditorControl<?> buildControl(@NotNull final PaintingPropertyDefinition definition,
                                                             @NotNull final Runnable callback) {
        switch (definition.getPropertyType()) {
            default: {
                return PropertyEditorControlFactory.build(vars, definition, callback);
            }
        }
    }

    /**
     * Get the list of painting properties.
     *
     * @return the list of painting properties.
     */
    @FxThread
    protected @NotNull Array<PaintingPropertyDefinition> getPaintingProperties() {
        return EMPTY_PROPERTIES;
    }
}
