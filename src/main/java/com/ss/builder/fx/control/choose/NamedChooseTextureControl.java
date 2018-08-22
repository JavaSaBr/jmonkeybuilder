package com.ss.builder.ui.control.choose;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.ui.control.property.PropertyControl;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.control.property.PropertyControl;
import com.ss.rlib.fx.util.FXUtils;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The named control to choose textures.
 *
 * @author JavaSaBr
 */
public class NamedChooseTextureControl extends ChooseTextureControl {

    private static final double LABEL_PERCENT = 1D - PropertyControl.CONTROL_WIDTH_PERCENT;
    private static final double FIELD_PERCENT = PropertyControl.CONTROL_WIDTH_PERCENT;

    /**
     * The name of this control.
     */
    @Nullable
    private final String name;

    /**
     * The label with name.
     */
    @Nullable
    private Label nameLabel;

    public NamedChooseTextureControl(@NotNull final String name) {
        this.name = name + ":";
    }

    @Override
    @FxThread
    protected void createComponents() {

        nameLabel = new Label();
        nameLabel.maxWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(LABEL_PERCENT));

        FXUtils.addToPane(nameLabel, this);

        super.createComponents();

        final HBox wrapper = getWrapper();
        wrapper.maxWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
        wrapper.prefWidthProperty().bind(widthProperty().multiply(FIELD_PERCENT));
    }

    /**
     * Set the width percent of this control.
     *
     * @param percent the width percent.
     */
    @FxThread
    public void setControlWidthPercent(final double percent) {

        final Label nameLabel = getNameLabel();
        nameLabel.maxWidthProperty().bind(widthProperty().multiply(1D - percent));
        nameLabel.prefWidthProperty().bind(widthProperty().multiply(1D - percent));

        final HBox wrapper = getWrapper();
        wrapper.maxWidthProperty().bind(widthProperty().multiply(percent));
        wrapper.prefWidthProperty().bind(widthProperty().multiply(percent));
    }

    /**
     * GEt the label with name.
     *
     * @return the label with name.
     */
    @FxThread
    private @NotNull Label getNameLabel() {
        return notNull(nameLabel);
    }

    @Override
    @FxThread
    protected void reload() {
        super.reload();

        final Label nameLabel = getNameLabel();
        nameLabel.setText(getName());
    }

    /**
     * Get the name of this control.
     *
     * @return the name of this control.
     */
    @FxThread
    private @NotNull String getName() {
        return name == null ? StringUtils.EMPTY : name;
    }
}
