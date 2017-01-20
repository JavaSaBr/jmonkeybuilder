package com.ss.editor.ui.control.material;

import com.jme3.material.Material;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.ui.util.FXUtils;

/**
 * The base implementation of control for editing material parameter.
 *
 * @author JavaSaBr
 */
public class MaterialParamControl extends HBox {

    protected static final Logger LOGGER = LoggerManager.getLogger(MaterialParamControl.class);

    public static final double LABEL_PERCENT_WIDTH = 0.4;
    public static final double LABEL_PERCENT_WIDTH2 = 0.6;
    public static final double CONTROL_PERCENT_WIDTH = 0.6;
    public static final double CONTROL_PERCENT_WIDTH2 = 0.4;

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

    public MaterialParamControl(@NotNull final Consumer<EditorOperation> changeHandler, @NotNull final Material material,
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

        FXUtils.addClassTo(this, CSSClasses.MATERIAL_PARAM_CONTROL);
    }

    /**
     * @param operation the new operation.
     */
    @FromAnyThread
    protected void execute(@NotNull final EditorOperation operation) {
        changeHandler.accept(operation);
    }

    /**
     * @return true if the listeners is ignored.
     */
    public boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    /**
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
        paramNameLabel.setId(CSSIds.MATERIAL_PARAM_CONTROL_PARAM_NAME);
        bindParamNameLabel();

        FXUtils.addClassTo(paramNameLabel, CSSClasses.SPECIAL_FONT_13);
        FXUtils.addToPane(paramNameLabel, this);
    }

    protected void bindParamNameLabel() {
        paramNameLabel.prefWidthProperty().bind(widthProperty().multiply(getLabelPercentWidth()));
    }

    protected double getLabelPercentWidth() {
        return LABEL_PERCENT_WIDTH;
    }

    /**
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
     * @return the parameter name.
     */
    @NotNull
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @return the current material.
     */
    @NotNull
    public Material getMaterial() {
        return material;
    }
}
