package com.ss.editor.ui.control.material;

import com.jme3.material.Material;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The base implementation of control for editing material parameter.
 *
 * @author JavaSaBr
 */
public class MaterialParamControl extends HBox {

    /**
     * The constant LOGGER.
     */
    @NotNull
    protected static final Logger LOGGER = LoggerManager.getLogger(MaterialParamControl.class);

    /**
     * The constant LABEL_PERCENT_WIDTH.
     */
    public static final double LABEL_PERCENT_WIDTH = 0.4;
    /**
     * The constant LABEL_PERCENT_WIDTH2.
     */
    public static final double LABEL_PERCENT_WIDTH2 = 0.6;
    /**
     * The constant CONTROL_PERCENT_WIDTH.
     */
    public static final double CONTROL_PERCENT_WIDTH = 0.6;
    /**
     * The constant CONTROL_PERCENT_WIDTH2.
     */
    public static final double CONTROL_PERCENT_WIDTH2 = 0.4;

    /**
     * The constant EDITOR_CONFIG.
     */
    @NotNull
    protected static final EditorConfig EDITOR_CONFIG = EditorConfig.getInstance();

    /**
     * The change handler.
     */
    @NotNull
    private final Consumer<EditorOperation> changeHandler;

    /**
     * The current material.
     */
    @NotNull
    private final Material material;

    /**
     * The parameter name.
     */
    @NotNull
    private final String parameterName;

    /**
     * The label with parameter name.
     */
    private Label paramNameLabel;

    /**
     * The flag for ignoring listeners.
     */
    private boolean ignoreListeners;

    /**
     * Instantiates a new Material param control.
     *
     * @param changeHandler the change handler
     * @param material      the material
     * @param parameterName the parameter name
     */
    protected MaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler, @NotNull final Material material,
                         @NotNull final String parameterName) {
        this.changeHandler = changeHandler;
        this.material = material;
        this.parameterName = parameterName;

        setIgnoreListeners(true);
        try {
            createComponents();
            reload();
        } finally {
            setIgnoreListeners(false);
        }

        FXUtils.addClassTo(this, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL);
    }

    /**
     * Execute.
     *
     * @param operation the new operation.
     */
    @FromAnyThread
    protected void execute(@NotNull final EditorOperation operation) {
        changeHandler.accept(operation);
    }

    /**
     * Is ignore listeners boolean.
     *
     * @return true if the listeners is ignored.
     */
    public boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
     * Sets ignore listeners.
     *
     * @param ignoreListeners true if the listeners is ignored.
     */
    public void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * Create components.
     */
    protected void createComponents() {
        paramNameLabel = new Label(getParameterName() + ":");

        bindParamNameLabel();

        FXUtils.addClassesTo(paramNameLabel, CSSClasses.MATERIAL_FILE_EDITOR_PARAM_CONTROL_NAME);
        FXUtils.addToPane(paramNameLabel, this);
    }

    /**
     * Bind param name label.
     */
    protected void bindParamNameLabel() {
        paramNameLabel.prefWidthProperty().bind(widthProperty().multiply(getLabelPercentWidth()));
    }

    /**
     * Gets label percent width.
     *
     * @return the label percent width
     */
    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH;
    }

    /**
     * Gets param name label.
     *
     * @return the label with parameter name.
     */
    protected Label getParamNameLabel() {
        return paramNameLabel;
    }

    /**
     * Reload a value of the material parameter.
     */
    public void reload() {
    }

    /**
     * Gets parameter name.
     *
     * @return the parameter name.
     */
    @NotNull
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Gets material.
     *
     * @return the current material.
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }
}
