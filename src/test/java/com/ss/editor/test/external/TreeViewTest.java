package com.ss.editor.test.external;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class TreeViewTest extends Application {

    List<Employee> employees = Arrays.asList(
            new Employee("Ethan Williams", "Sales Department", "Ethan Williams", "Sales Department"),
            new Employee("Emma Jones", "Sales Department", "Emma Jones", "Sales Department"),
            new Employee("Michael Brown", "Sales Department", "Michael Brown", "Sales Department"),
            new Employee("Anna Black", "Sales Department", "Anna Black", "Sales Department"),
            new Employee("Rodger York", "Sales Department", "Rodger York", "Sales Department"),
            new Employee("Susan Collins", "Sales Department", "Susan Collins", "Sales Department"),
            new Employee("Mike Graham", "IT Support", "Mike Graham", "IT Support"),
            new Employee("Judy Mayer", "IT Support", "Judy Mayer", "IT Support"),
            new Employee("Gregory Smith", "IT Support", "Gregory Smith", "IT Support"),
            new Employee("Jacob Smith", "Accounts Department", "Jacob Smith", "Accounts Department"),
            new Employee("Isabella Johnson", "Accounts Departmentllzzz", "Isabella Johnson", "Accounts Departmentllzzz"),
            new Employee("Isabella Johnson", "Accounts Departmentllzzz", "Isabella Johnson", "Accounts Departmentllzzz"),
            new Employee("Isabella Johnson", "Accounts Departmentllzzz", "Isabella Johnson", "Accounts Departmentllzzz"));

    TreeItem<String> rootNode = new TreeItem<>("MyCompany Human Resources");

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Tree View Sample");
        stage.setWidth(1024);
        stage.setHeight(768);

        rootNode.setExpanded(true);

        for (Employee employee : employees) {

            TreeItem<String> empLeaf = new TreeItem<>(employee.getName());

            boolean found = false;

            for (TreeItem<String> depNode : rootNode.getChildren()) {
                if (depNode.getValue().contentEquals(employee.getDepartment())) {
                    depNode.getChildren().add(empLeaf);
                    found = true;
                    break;
                }
            }

            if (!found) {
                TreeItem<String> depNode = new TreeItem<>(employee.getDepartment());
                rootNode.getChildren().add(depNode);
                depNode.getChildren().add(empLeaf);
            }
        }

        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setShowRoot(true);
        treeView.setEditable(false);
        treeView.setCellFactory(p -> new TextFieldTreeCellImpl());

        VBox emptyBox = new VBox();
        emptyBox.prefWidthProperty().bind(stage.widthProperty().multiply(0.5));

        SplitPane splitPane = new SplitPane();
        splitPane.getItems()
                .addAll(treeView, emptyBox);

        Scene scene = new Scene(splitPane, 400, 300);
        scene.setFill(Color.LIGHTGRAY);

        stage.setScene(scene);
        stage.show();
    }

    private final class TextFieldTreeCellImpl extends TreeCell<String> {

        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();

        public TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("Add Employee");
            addMenu.getItems().add(addMenuItem);
            addMenuItem.setOnAction(t -> {
                TreeItem<String> newEmployee = new TreeItem<>("New Employee");
                getTreeItem().getChildren().add(newEmployee);
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (!getTreeItem().isLeaf() && getTreeItem().getParent() != null) {
                        setContextMenu(addMenu);
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    public static class Employee {

        private final SimpleStringProperty name;
        private final SimpleStringProperty department;
        private final SimpleStringProperty test;
        private final SimpleStringProperty testtwo;


        private Employee(String name, String department, String test, String testtwo) {
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
            this.test = new SimpleStringProperty(test);
            this.testtwo = new SimpleStringProperty(testtwo);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String fName) {
            name.set(fName);
        }

        public String getDepartment() {
            return department.get();
        }

        public void setDepartment(String fName) {
            department.set(fName);
        }


        public String getTest() {
            return test.get();
        }

        public void setTest(String testd) {
            test.set(testd);
        }


        public String getTesttwo() {
            return testtwo.get();
        }

        public void setTesttwo(String fNdme) {
            testtwo.set(fNdme);
        }
    }
}
