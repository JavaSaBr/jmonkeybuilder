package com.ss.editor.ui.component.editor.scripting;

import com.ss.editor.Messages;
import com.ss.editor.ui.component.scripting.GroovyEditorComponent;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import groovy.lang.GroovyShell;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

/**
 * The component to work with scripts in an editor.
 *
 * @author JavaSaBr
 */
public class EditorScriptingComponent extends GridPane {

    /**
     * The table of variables.
     */
    @NotNull
    private final ObjectDictionary<String, Object> variables;

    /**
     * The list of imports.
     */
    @NotNull
    private final Array<String> imports;

    /**
     * The shell.
     */
    @NotNull
    private final GroovyShell shell;

    /**
     * The header component.
     */
    @NotNull
    private final GroovyEditorComponent headerComponent;

    /**
     * The editor component.
     */
    @NotNull
    private final GroovyEditorComponent editorComponent;

    /**
     * The apply handler.
     */
    @NotNull
    private final Runnable applyHandler;

    /**
     * Instantiates a new Editor scripting component.
     *
     * @param applyHandler the apply handler
     */
    public EditorScriptingComponent(@NotNull final Runnable applyHandler) {
        this.applyHandler = applyHandler;

        this.editorComponent = new GroovyEditorComponent(true);
        this.editorComponent.prefHeightProperty().bind(heightProperty().multiply(0.6));
        this.editorComponent.prefWidthProperty().bind(widthProperty());
        this.headerComponent = new GroovyEditorComponent(false);
        this.headerComponent.prefHeightProperty().bind(heightProperty().multiply(0.4));
        this.headerComponent.prefWidthProperty().bind(widthProperty());
        this.shell = new GroovyShell();
        this.variables = DictionaryFactory.newObjectDictionary();
        this.imports = ArrayFactory.newArray(String.class);

        final Label headersLabel = new Label(Messages.EDITOR_SCRIPTING_COMPONENT_HEADERS + ":");
        final Label scriptBodyLabel = new Label(Messages.EDITOR_SCRIPTING_COMPONENT_BODY + ":");

        final Button runButton = new Button(Messages.EDITOR_SCRIPTING_COMPONENT_RUN);
        runButton.setOnAction(event -> run());

        add(headersLabel, 0, 0, 1, 1);
        add(headerComponent, 0, 1, 1, 1);
        add(scriptBodyLabel, 0, 2, 1, 1);
        add(editorComponent, 0, 3, 1, 1);
        add(runButton, 0, 4, 1, 1);

        FXUtils.addClassTo(this, CSSClasses.EDITOR_SCRIPTING_COMPONENT);
    }

    /**
     * Add a global variable to the script.
     *
     * @param name  the name of the variable.
     * @param value the variable.
     */
    public void addVariable(@NotNull final String name, @NotNull final Object value) {
        variables.put(name, value);
        addImport(value.getClass());
    }

    /**
     * Add an import of a some type.
     *
     * @param type the type.
     */
    public void addImport(@NotNull final Class<?> type) {
        final String name = type.getName();
        if (!imports.contains(name)) imports.add(name);
    }

    /**
     * Build a header of a script.
     */
    public void buildHeader() {

        final StringBuilder result = new StringBuilder();

        imports.forEach(result, (type, stringBuilder) -> stringBuilder.append("import ").append(type).append('\n'));

        result.append('\n');

        variables.forEach((name, value) -> result.append(value.getClass().getSimpleName())
                .append(' ')
                .append(name)
                .append(" = load_")
                .append(name)
                .append("();\n"));

        headerComponent.setCode(result.toString());
    }

    /**
     * Set an example of groovy code.
     *
     * @param example the example code.
     */
    public void setExampleCode(@NotNull final String example) {
        editorComponent.setCode(example);
    }

    /**
     * Run the current script.
     */
    private void run() {

        String code = editorComponent.getCode();

        for (final String type : imports) {
            final String check = "import " + type;
            if (code.contains(check)) {
                code = code.replace(check, "");
            }
        }

        final StringBuilder result = new StringBuilder();

        imports.forEach(result, (type, stringBuilder) -> stringBuilder.append("import ").append(type).append('\n'));
        result.append(code);

        variables.forEach(shell, GroovyShell::setVariable);

        try {
            shell.evaluate(result.toString());
        } catch (final Exception e) {
            EditorUtil.handleException(null, this, e);
            return;
        }

        applyHandler.run();
    }
}
