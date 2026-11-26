module com.lab {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;

    opens com.lab to javafx.fxml;
    exports com.lab;
}