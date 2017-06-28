package com.ss.editor.ui.component.creator.impl.material.definition;

import static com.ss.editor.FileExtensions.JME_MATERIAL_DEFINITION;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Character.toUpperCase;
import static java.nio.file.Files.readAllBytes;
import static java.util.Objects.requireNonNull;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.Caps;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.Utils;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        AVAILABLE_GLSL.add(Caps.GLSL100.name());
        AVAILABLE_GLSL.add(Caps.GLSL110.name());
        AVAILABLE_GLSL.add(Caps.GLSL120.name());
        AVAILABLE_GLSL.add(Caps.GLSL130.name());
        AVAILABLE_GLSL.add(Caps.GLSL150.name());
        AVAILABLE_GLSL.add(Caps.GLSL330.name());
        AVAILABLE_GLSL.add(Caps.GLSL400.name());
    }

    @NotNull
    private static final String MD_TEMPLATE;

    @NotNull
    private static final String FRAG_TEMPLATE;

    @NotNull
    private static final String VERT_TEMPLATE;

    static {
        final URL mdResource = FileCreator.class.getResource("/template/matdef/empty.j3md");
        final URL fragResourcesource = FileCreator.class.getResource("/template/frag/empty.frag");
        final URL vertResource = FileCreator.class.getResource("/template/vert/empty.vert");
        MD_TEMPLATE = requireNonNull(Utils.get(() -> new String(readAllBytes(Paths.get(mdResource.toURI())), "UTF-8")));
        FRAG_TEMPLATE = requireNonNull(Utils.get(() -> new String(readAllBytes(Paths.get(fragResourcesource.toURI())), "UTF-8")));
        VERT_TEMPLATE = requireNonNull(Utils.get(() -> new String(readAllBytes(Paths.get(vertResource.toURI())), "UTF-8")));
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
        glslLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        glslLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        glslComboBox = new ComboBox<>();
        glslComboBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        glslComboBox.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final TextField editor = glslComboBox.getEditor();
        editor.setId(CSSIds.EDITOR_DIALOG_FIELD);

        final ObservableList<String> items = glslComboBox.getItems();
        items.clear();
        items.addAll(AVAILABLE_GLSL);

        final SingleSelectionModel<String> selectionModel = glslComboBox.getSelectionModel();
        selectionModel.select(Caps.GLSL150.name());

        selectionModel.selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validateFileName());

        root.add(glslLabel, 0, 1);
        root.add(glslComboBox, 1, 1);

        FXUtils.addClassTo(glslLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(glslComboBox, CSSClasses.SPECIAL_FONT_14);
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
