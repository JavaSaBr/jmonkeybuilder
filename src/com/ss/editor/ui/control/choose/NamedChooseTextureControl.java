package com.ss.editor.ui.control.choose;

import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.css.CSSIds;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.StringUtils;

import javax.annotation.Nullable;

/**
 * The named control to choose textures.
 *
 * @author JavaSaBr
 */
public class NamedChooseTextureControl extends ChooseTextureControl {

    private static final double LABEL_PERCENT = 1D - AbstractPropertyControl.CONTROL_WIDTH_PERCENT;

    /**
     * The name of this control.
     */
    @Nullable
    private final String name;

    /**
     * Instantiates a new Named choose texture control.
     *
     * @param name the name
     */
    public NamedChooseTextureControl(@NotNull final String name) {
        this.name = name + ":";
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        final Label textureLabel = getTextureLabel();
        textureLabel.setId(CSSIds.ABSTRACT_PARAM_CONTROL_PARAM_NAME_SINGLE_ROW);
        textureLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));
        textureLabel.maxWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));
    }

    @Override
    protected void reload() {
        super.reload();

        final Label textureLabel = getTextureLabel();
        textureLabel.setText(getName());
    }

    /**
     * @return the name of this control.
     */
    @NotNull
    private String getName() {
        return name == null ? StringUtils.EMPTY : name;
    }
}
