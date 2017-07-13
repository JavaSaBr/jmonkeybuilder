package com.ss.editor.ui.component.creator.impl.material.definition;

import static com.ss.editor.FileExtensions.JME_MATERIAL_DEFINITION;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Character.toUpperCase;
import static java.util.Objects.requireNonNull;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

/**
 * The creator to create a new material definition.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionFileCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialDefinitionFileCreator::new);
    }

    @NotNull
    private static final Array<String> AVAILABLE_GLSL;

    static {
        AVAILABLE_GLSL = ArrayFactory.newArray(String.class);

        final Renderer renderer = EDITOR.getRenderer();

        final EnumSet<Caps> caps = renderer.getCaps();
        caps.stream().filter(cap -> cap.name().startsWith("GLSL"))
                .map(Enum::name)
                .sorted(StringUtils::compareIgnoreCase)
                .forEach(AVAILABLE_GLSL::add);
    }

    @NotNull
    private static final String MD_TEMPLATE;

    @NotNull
    private static final String FRAG_TEMPLATE;

    @NotNull
    private static final String VERT_TEMPLATE;

    static {
        final InputStream mdResource = FileCreator.class.getResourceAsStream("/template/matdef/empty.j3md");
        final InputStream fragResource = FileCreator.class.getResourceAsStream("/template/frag/empty.frag");
        final InputStream vertResource = FileCreator.class.getResourceAsStream("/template/vert/empty.vert");
        MD_TEMPLATE = FileUtils.read(mdResource);
        FRAG_TEMPLATE = FileUtils.read(fragResource);
        VERT_TEMPLATE = FileUtils.read(vertResource);
    }

    /**
     * The combo box.
     */
    @Nullable
    private ComboBox<String> glslComboBox;

    private MaterialDefinitionFileCreator() {
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.MATERIAL_DEFINITION_FILE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return JME_MATERIAL_DEFINITION;
    }

    @Override
    protected void createSettings(@NotNull final GridPane root) {
        super.createSettings(root);

        final Label glslLabel = new Label(Messages.MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL + ":");
        glslLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        glslComboBox = new ComboBox<>();
        glslComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final TextField editor = glslComboBox.getEditor();

        final ObservableList<String> items = glslComboBox.getItems();
        items.clear();
        items.addAll(AVAILABLE_GLSL);

        final SingleSelectionModel<String> selectionModel = glslComboBox.getSelectionModel();
        selectionModel.select(Caps.GLSL150.name());

        selectionModel.selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validateFileName());

        root.add(glslLabel, 0, 1);
        root.add(glslComboBox, 1, 1);

        FXUtils.addClassTo(glslLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(glslComboBox, editor, CSSClasses.DIALOG_FIELD);
    }

    @NotNull
    private ComboBox<String> getGlslComboBox() {
        return notNull(glslComboBox);
    }

    @Override
    protected void processOk() {
        super.processOk();

        final Path matDefFile = notNull(getFileToCreate());
        final String filename = FileUtils.getNameWithoutExtension(matDefFile);

        final Path parent = matDefFile.getParent();
        final Path fragmentFile = parent.resolve(filename + "." + FileExtensions.GLSL_FRAGMENT);
        final Path vertexFile = parent.resolve(filename + "." + FileExtensions.GLSL_VERTEX);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path assetFolder = requireNonNull(editorConfig.getCurrentAsset());

        final Path pathToFragment = assetFolder.relativize(fragmentFile);
        final Path pathToVertex = assetFolder.relativize(vertexFile);

        final ComboBox<String> glslComboBox = getGlslComboBox();
        final SingleSelectionModel<String> selectionModel = glslComboBox.getSelectionModel();
        final String glslVersion = selectionModel.getSelectedItem();

        final String mdName = filename.length() > 1 ?
                toUpperCase(filename.charAt(0)) + filename.substring(1, filename.length()) : filename;

        String mdContent = MD_TEMPLATE.replace("${matdef-name}", mdName);
        mdContent = mdContent.replace("${light-mode}", TechniqueDef.LightMode.SinglePass.name());
        mdContent = mdContent.replace("${GLSL-version}", glslVersion);
        mdContent = mdContent.replace("${vertex-path}", pathToVertex.toString());
        mdContent = mdContent.replace("${fragment-path}", pathToFragment.toString());

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(matDefFile))) {
            out.print(mdContent);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(fragmentFile))) {
            out.print(FRAG_TEMPLATE);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(vertexFile))) {
            out.print(VERT_TEMPLATE);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(matDefFile, true);
        notifyFileCreated(fragmentFile, false);
        notifyFileCreated(vertexFile, false);
    }
}
