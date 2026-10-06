package org.example.customerapp1;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        // ----- Form -----
        Label nameLabel = new Label("_Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Full name");
        nameLabel.setMnemonicParsing(true);
        nameLabel.setLabelFor(nameField);

        Label provinceLabel = new Label("_Province:");
        ObservableList<String> provinces = FXCollections.observableArrayList(
                "Central", "Copperbelt", "Eastern", "Luapula", "Lusaka",
                "Muchinga", "Northern", "North-Western", "Southern", "Western");
        ComboBox<String> provinceBox = new ComboBox<>(provinces);
        provinceBox.setPromptText("Select province");
        provinceLabel.setMnemonicParsing(true);
        provinceLabel.setLabelFor(provinceBox);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        // ----- Data and table -----
        ObservableList<Customer> customers = FXCollections.observableArrayList();

        TableView<Customer> table = new TableView<>(customers);
        table.setPlaceholder(new Label("No customers yet"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());

        TableColumn<Customer, String> provinceCol = new TableColumn<>("Province");
        provinceCol.setCellValueFactory(data -> data.getValue().provinceProperty());

        table.getColumns().add(nameCol);
        table.getColumns().add(provinceCol);

        // ----- Add button -----
        Button addButton = new Button("_Add Customer");
        addButton.setDefaultButton(true);   // Enter key triggers this button
        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String province = provinceBox.getValue();

            String error = validate(name, province);
            if (error != null) {
                errorLabel.setText(error);
                // Send focus to the field that needs fixing
                if (error.contains("province")) {
                    provinceBox.requestFocus();
                } else {
                    nameField.requestFocus();
                }
                return;
            }

            customers.add(new Customer(name, province));
            errorLabel.setText("");
            nameField.clear();
            provinceBox.getSelectionModel().clearSelection();
            nameField.requestFocus();
        });

        // ----- Delete button -----
        Button deleteButton = new Button("_Delete Selected");
        deleteButton.disableProperty().bind(
                table.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete " + selected.getName() + " (" + selected.getProvince() + ")?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirm deletion");
            confirm.setHeaderText(null);

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                customers.remove(selected);
            }
            table.requestFocus();
        });

        // Delete key on the keyboard triggers the same confirmed delete
        table.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                deleteButton.fire();
            }
        });

        // ----- Layout -----
        HBox buttons = new HBox(10, addButton, deleteButton);

        VBox root = new VBox(8, nameLabel, nameField, provinceLabel, provinceBox,
                buttons, errorLabel, table);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root, 400, 480);
        stage.setTitle("Customer Manager");
        stage.setScene(scene);
        stage.show();
        nameField.requestFocus();
    }

    // Returns an error message, or null if the input is valid
    private static String validate(String name, String province) {
        if (name.isEmpty()) {
            return "Name is required.";
        }
        if (name.length() < 2) {
            return "Name must be at least 2 characters.";
        }
        if (!name.matches("[\\p{L} .'-]+")) {
            return "Name may only contain letters, spaces, . ' and -";
        }
        if (province == null) {
            return "Please select a province.";
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}