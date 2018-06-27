package com.ss.editor.ui.component.creator.impl.material.definition;

import static com.ss.editor.FileExtensions.JME_MATERIAL_DEFINITION;
import static com.ss.editor.extension.property.EditablePropertyType.STRING_FROM_LIST;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import static java.lang.Character.toUpperCase;
import static java.nio.file.StandardOpenOption.*;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.renderer.Caps;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.creator.FileCreatorDescriptor;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayCollectors;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

/**
 * The creator to create a new material definition.
 *
 * @author JavaSaBr
 */
public class MaterialDefinitionFileCreator extends GenericFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.MATERIAL_DEFINITION_FILE_CREATOR_FILE_DESCRIPTION,
            MaterialDefinitionFileCreator::new
    );

    private static final String PROP_GLSL_VERSION = "glslVersion";
    private static final String MD_TEMPLATE;
    private static final String FRAG_TEMPLATE;
    private static final String VERT_TEMPLATE;

    static {
        MD_TEMPLATE = FileUtils.readFromClasspath("/template/matdef/empty.j3md");
        FRAG_TEMPLATE = FileUtils.readFromClasspath("/template/frag/empty.frag");
        VERT_TEMPLATE = FileUtils.readFromClasspath("/template/vert/empty.vert");
    }

    /**
     * The list of available GLSL versions.
     */
    @NotNull
    private final Array<String> availableGlsl;

    private MaterialDefinitionFileCreator() {
        this.availableGlsl = EditorUtil.getRenderer()
            .getCaps()
            .stream()
            .filter(cap -> cap.name().startsWith("GLSL"))
            .map(Enum::name)
            .sorted(StringUtils::compareIgnoreCase)
            .collect(ArrayCollectors.toArray(String.class));
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
    @FxThread
    protected boolean validate(@NotNull VarTable vars) {

        var glslVersion = vars.get(PROP_GLSL_VERSION, String.class, StringUtils.EMPTY);

        if (glslVersion.isEmpty() || !availableGlsl.contains(glslVersion)) {
            return false;
        }

        return super.validate(vars);
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {

        var result = Array.<PropertyDefinition>ofType(PropertyDefinition.class);
        result.add(new PropertyDefinition(STRING_FROM_LIST, Messages.MATERIAL_DEFINITION_FILE_CREATOR_GLSL_LABEL,
                PROP_GLSL_VERSION, Caps.GLSL150.name(), availableGlsl));

        return result;
    }

    @Override
    @FxThread
    protected void processOk() {
        super.hide();

        var matDefFile = notNull(getFileToCreate());
        var filename = FileUtils.getNameWithoutExtension(matDefFile);

        var parent = matDefFile.getParent();
        var fragmentFile = parent.resolve(filename + "." + FileExtensions.GLSL_FRAGMENT);
        var vertexFile = parent.resolve(filename + "." + FileExtensions.GLSL_VERTEX);

        var editorConfig = EditorConfig.getInstance();
        var assetFolder = notNull(editorConfig.getCurrentAsset());

        var pathToFragment = assetFolder.relativize(fragmentFile);
        var pathToVertex = assetFolder.relativize(vertexFile);

        var vars = getVars();
        var glslVersion = vars.getString(PROP_GLSL_VERSION);

        var mdName = filename.length() > 1 ?
                toUpperCase(filename.charAt(0)) + filename.substring(1, filename.length()) : filename;

        var mdContent = MD_TEMPLATE.replace("${matdef-name}", mdName);
        mdContent = mdContent.replace("${light-mode}", LightMode.SinglePass.name());
        mdContent = mdContent.replace("${GLSL-version}", glslVersion);
        mdContent = mdContent.replace("${vertex-path}", pathToVertex.toString());
        mdContent = mdContent.replace("${fragment-path}", pathToFragment.toString());

        try (var out = new PrintWriter(Files.newOutputStream(matDefFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(mdContent);
        } catch (IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (var out = new PrintWriter(Files.newOutputStream(fragmentFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(FRAG_TEMPLATE);
        } catch (IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        try (var out = new PrintWriter(Files.newOutputStream(vertexFile, WRITE, TRUNCATE_EXISTING, CREATE))) {
            out.print(VERT_TEMPLATE);
        } catch (IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        notifyFileCreated(matDefFile, true);
        notifyFileCreated(fragmentFile, false);
        notifyFileCreated(vertexFile, false);
    }
}
