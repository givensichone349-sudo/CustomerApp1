module org.example.customerapp1 {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.customerapp1 to javafx.fxml;
    exports org.example.customerapp1;
}