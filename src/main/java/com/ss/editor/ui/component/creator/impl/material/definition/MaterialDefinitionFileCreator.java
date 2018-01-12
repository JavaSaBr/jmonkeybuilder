package com.ss.editor.ui.component.creator.impl.material.definition;

import static com.ss.editor.FileExtensions.JME_MATERIAL_DEFINITION;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.lang.Character.toUpperCase;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.extension.property.EditablePropertyType;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.creator.FileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.VarTable;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

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
public class MaterialDefinitionFileCreator extends GenericFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    @NotNull
    private static final String PROP_GLSL_VERSION = "glslVersion";

    static {
        DESCRIPTION.setFileDescription(Messages.MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION);
        DESCRIPTION.setConstructor(MaterialDefinitionFileCreator::new);
    }

    @NotNull
    private static final Array<String> AVAILABLE_GLSL;

    static {
        AVAILABLE_GLSL = ArrayFactory.newArray(String.class);

        final Renderer renderer = JME_APPLICATION.getRenderer();

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

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.MATERIAL_DEFINITION_FILE_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return JME_MATERIAL_DEFINITION;
    }

    @Override
    @FXThread
    protected boolean validate(@NotNull final VarTable vars) {

        final String glslVersion = vars.get(PROP_GLSL_VERSION, String.class, StringUtils.EMPTY);

        if (glslVersion.isEmpty() || !AVAILABLE_GLSL.contains(glslVersion)) {
            return false;
        }

        return super.validate(vars);
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        final Array<PropertyDefinition> result = ArrayFactory.newArray(PropertyDefinition.class);
        result.add(new PropertyDefinition(EditablePropertyType.STRING_FROM_LIST,
                Messages.MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL, PROP_GLSL_VERSION, Caps.GLSL150.name(), AVAILABLE_GLSL));

        return result;
    }

    @Override
    @FXThread
    protected void processOk() {
        super.hide();

        final Path matDefFile = notNull(getFileToCreate());
        final String filename = FileUtils.getNameWithoutExtension(matDefFile);

        final Path parent = matDefFile.getParent();
        final Path fragmentFile = parent.resolve(filename + "." + FileExtensions.GLSL_FRAGMENT);
        final Path vertexFile = parent.resolve(filename + "." + FileExtensions.GLSL_VERTEX);

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path assetFolder = notNull(editorConfig.getCurrentAsset());

        final Path pathToFragment = assetFolder.relativize(fragmentFile);
        final Path pathToVertex = assetFolder.relativize(vertexFile);

        final VarTable vars = getVars();
        final String glslVersion = vars.getString(PROP_GLSL_VERSION);

        final String mdName = filename.length() > 1 ?
                toUpperCase(filename.charAt(0)) + filename.substring(1, filename.length()) : filename;

        String mdContent = MD_TEMPLATE.replace("${matdef-name}", mdName);
        mdContent = mdContent.replace("${light-mode}", TechniqueDef.LightMode.SinglePass.name());
        mdContent = mdContent.replace("${GLSL-version}", glslVersion);
        mdContent = mdContent.replace("${vertex-path}", pathToVertex.toString());
        mdContent = mdContent.replace("${fragment-path}", pathToFragment.toString());

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(matDefFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(mdContent);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(fragmentFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(FRAG_TEMPLATE);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (final PrintWriter out = new PrintWriter(Files.newOutputStream(vertexFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
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
