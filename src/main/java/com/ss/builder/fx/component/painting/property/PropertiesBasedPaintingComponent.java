package com.ss.builder.fx.component.painting.property;

import static com.ss.editor.extension.property.EditablePropertyType.FLOAT;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.fx.component.painting.PaintingComponentContainer;
import com.ss.builder.fx.component.painting.impl.AbstractPaintingComponent;
import com.ss.builder.fx.component.painting.impl.AbstractPaintingStateWithEditorTool;
import com.ss.builder.jme.control.painting.PaintingControl;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.plugin.api.property.control.PropertyEditorControl;
import com.ss.builder.plugin.api.property.control.PropertyEditorControlFactory;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * The properties based implementation of painting component.
 *
 * @param <T> the painted object's type.
 * @param <S> the component state's type.
 * @param <C> the painting control's type.
 * @author JavaSaBr
 */
public abstract class PropertiesBasedPaintingComponent<T, S extends AbstractPaintingStateWithEditorTool,
        C extends PaintingControl> extends AbstractPaintingComponent<T, S, C> {

    private static final String PROPERTY_BRUSH_SIZE = "brushSize";
    private static final String PROPERTY_BRUSH_POWER = "brushPower";

    protected static final Array<PaintingPropertyDefinition> EMPTY_PROPERTIES =
            ArrayFactory.newArray(PaintingPropertyDefinition.class);

    protected static class AdditionalPropertyContainer extends VBox {

        private final String category;

        public AdditionalPropertyContainer(@NotNull String category) {
            this.category = category;
        }
    }

    /**
     * The map of category to properties container.
     */
    @NotNull
    private ObjectDictionary<String, AdditionalPropertyContainer> propertyContainers;

    /**
     * The container of brush settings.
     */
    @NotNull
    private VBox brushSettings;

    /**
     * The property variables.
     */
    @NotNull
    private VarTable vars;

    public PropertiesBasedPaintingComponent(@NotNull PaintingComponentContainer container) {
        super(container);
    }

    @Override
    @FxThread
    protected void createComponents() {
        super.createComponents();

        this.propertyContainers = DictionaryFactory.newObjectDictionary();
        this.vars = VarTable.newInstance();
        this.brushSettings = new VBox();

        Function<String, AdditionalPropertyContainer> containerFactory = this::createContainer;
        Runnable callback = this::syncValues;

        for (var definition : getBrushProperties()) {
            var control = PropertyEditorControlFactory.build(vars, definition, callback);
            FxUtils.addChild(brushSettings, control);
        }

        FxUtils.addChild(this, brushSettings);

        for (var definition : getPaintingProperties()) {
            var control = PropertyEditorControlFactory.build(vars, definition, callback);
            var container = propertyContainers.get(definition.getCategory(), containerFactory);
            FxUtils.addChild(container, control);
        }

        propertyContainers.forEach((category, propertyContainer) ->
                FxUtils.addChild(this, propertyContainer));
    }

    @Override
    @FxThread
    protected void readState(@NotNull S state) {
        super.readState(state);
        readState(state, getVars());
        refreshPropertyControls();
        syncValues();
    }

    @FxThread
    protected void readState(@NotNull S state, @NotNull VarTable vars) {
        vars.set(PROPERTY_BRUSH_POWER, state.getBrushPower());
        vars.set(PROPERTY_BRUSH_SIZE, state.getBrushSize());
    }

    /**
     * Refresh property controls.
     */
    @FxThread
    protected void refreshPropertyControls() {

        brushSettings.getChildren()
                .stream()
                .map(node -> (PropertyEditorControl<?>) node)
                .forEach(PropertyEditorControl::reload);

        propertyContainers.forEach(container -> container.getChildren()
                .stream()
                .map(node -> (PropertyEditorControl<?>) node)
                .forEach(PropertyEditorControl::reload));
    }

    /**
     * Show properties of the category.
     *
     * @param category the category.
     */
    @FxThread
    protected void showCategory(@NotNull String category) {
        getChildren().stream()
                .filter(AdditionalPropertyContainer.class::isInstance)
                .peek(node -> node.setManaged(false))
                .peek(node -> node.setVisible(false))
                .map(AdditionalPropertyContainer.class::cast)
                .filter(container -> container.category.equals(category))
                .peek(container -> container.setManaged(true))
                .forEach(container -> container.setVisible(true));
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
        syncValues(getVars(), getState());
    }

    /**
     * Synchronize values from properties.
     *
     * @param vars  the variable's table.
     * @param state the state.
     */
    @FxThread
    protected void syncValues(@NotNull VarTable vars, @NotNull S state) {

        var brushSize = vars.getFloat(PROPERTY_BRUSH_SIZE);
        var brushPower = vars.getFloat(PROPERTY_BRUSH_POWER);

        state.setBrushPower(brushPower);
        state.setBrushSize(brushSize);

        var toolControl = getToolControl();

        EXECUTOR_MANAGER.addJmeTask(() -> syncValues(state, toolControl));
    }

    /**
     * Synchronize values from properties with the current tool control.
     *
     * @param state       the state.
     * @param toolControl the tool control.
     */
    @JmeThread
    protected void syncValues(@NotNull S state, @NotNull C toolControl) {
        toolControl.setBrushPower(state.getBrushPower());
        toolControl.setBrushSize(state.getBrushSize());
    }

    /**
     * Create a container for the category.
     *
     * @param category the category.
     */
    @FxThread
    private AdditionalPropertyContainer createContainer(@NotNull String category) {
        return new AdditionalPropertyContainer(category);
    }

    /**
     * Get the list of brush properties.
     *
     * @return the list of brush properties.
     */
    @FxThread
    protected @NotNull Array<PropertyDefinition> getBrushProperties() {

        var result = ArrayFactory.<PropertyDefinition>newArray(PropertyDefinition.class);
        result.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_BRUSH_SIZE,
                PROPERTY_BRUSH_SIZE, 1F, 0.0001F, Integer.MAX_VALUE));
        result.add(new PropertyDefinition(FLOAT, Messages.MODEL_PROPERTY_BRUSH_POWER,
                PROPERTY_BRUSH_POWER, 1F, 0.0001F, Integer.MAX_VALUE));

        return result;
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
