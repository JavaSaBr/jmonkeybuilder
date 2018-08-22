package com.ss.builder.ui.component.editor.scripting;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.ui.component.scripting.GroovyEditorComponent;
import com.ss.builder.ui.css.CssClasses;
import com.ss.builder.util.EditorUtils;
import com.ss.editor.Messages;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.scripting.GroovyEditorComponent;
import com.ss.editor.ui.css.CssClasses;
import com.ss.editor.util.EditorUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import com.ss.rlib.fx.util.FxUtils;
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
     * The table of variable to type.
     */
    @NotNull
    private final ObjectDictionary<String, Class<?>> variableToType;

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

    public EditorScriptingComponent(@NotNull Runnable applyHandler) {
        this.applyHandler = applyHandler;

        this.editorComponent = new GroovyEditorComponent(true);
        this.editorComponent.setFocusTraversable(true);
        this.editorComponent.prefHeightProperty().bind(heightProperty().multiply(0.6));
        this.editorComponent.prefWidthProperty().bind(widthProperty());
        this.headerComponent = new GroovyEditorComponent(false);
        this.headerComponent.prefHeightProperty().bind(heightProperty().multiply(0.4));
        this.headerComponent.prefWidthProperty().bind(widthProperty());
        this.shell = new GroovyShell();
        this.variables = ObjectDictionary.ofType(String.class, Object.class);
        this.variableToType = ObjectDictionary.ofType(String.class, Class.class);
        this.imports = ArrayFactory.newArray(String.class);

        var headersLabel = new Label(Messages.EDITOR_SCRIPTING_COMPONENT_HEADERS + ":");
        var scriptBodyLabel = new Label(Messages.EDITOR_SCRIPTING_COMPONENT_BODY + ":");

        var runButton = new Button(Messages.EDITOR_SCRIPTING_COMPONENT_RUN);
        runButton.setOnAction(event -> run());

        add(headersLabel, 0, 0, 1, 1);
        add(headerComponent, 0, 1, 1, 1);
        add(scriptBodyLabel, 0, 2, 1, 1);
        add(editorComponent, 0, 3, 1, 1);
        add(runButton, 0, 4, 1, 1);

        FxUtils.addClass(this, CssClasses.EDITOR_SCRIPTING_COMPONENT);
    }

    /**
     * Add a global variable to the script.
     *
     * @param name  the name of the variable.
     * @param value the variable.
     */
    @FromAnyThread
    public void addVariable(@NotNull String name, @NotNull Object value) {
        variables.put(name, value);
        addImport(value.getClass());
    }

    /**
     * Add a global variable to the script.
     *
     * @param name  the name of the variable.
     * @param value the variable.
     * @param type  the expected type of the variable.
     */
    @FromAnyThread
    public <T> void addVariable(@NotNull String name, @NotNull T value, @NotNull Class<T> type) {
        variables.put(name, value);
        variableToType.put(name, type);
        addImport(type.getClass());
    }


    /**
     * Add an import of a some type.
     *
     * @param type the type.
     */
    @FromAnyThread
    public void addImport(@NotNull Class<?> type) {

        var name = type.getName();

        if (!imports.contains(name)) {
            imports.add(name);
        }
    }

    /**
     * Build a header of a script.
     */
    @FromAnyThread
    public void buildHeader() {

        var result = new StringBuilder();

        imports.forEach(result, (type, builder) -> builder.append("import ")
                .append(type)
                .append('\n'));

        result.append('\n');

        variables.forEach((name, value) -> {

            Class<?> type = variableToType.get(name);

            if (type == null) {
                type = value.getClass();
            }

            result.append(type.getSimpleName())
                    .append(' ')
                    .append(name)
                    .append(" = load_")
                    .append(name)
                    .append("();\n");
        });

        headerComponent.setCode(result.toString());
    }

    /**
     * Set an example of groovy code.
     *
     * @param example the example code.
     */
    public void setExampleCode(@NotNull String example) {
        editorComponent.setCode(example);
    }

    /**
     * Run the current script.
     */
    private void run() {

        String code = editorComponent.getCode();

        for (var type : imports) {
            var check = "import " + type;
            if (code.contains(check)) {
                code = code.replace(check, "");
            }
        }

        var result = new StringBuilder();

        imports.forEach(result, (type, builder) -> builder.append("import ")
                .append(type)
                .append('\n'));

        result.append(code);

        variables.forEach(shell, GroovyShell::setVariable);
        try {
            shell.evaluate(result.toString());
        } catch (Exception e) {
            EditorUtils.handleException(null, this, e);
            return;
        }

        applyHandler.run();
    }
}
