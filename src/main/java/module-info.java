module com.lab {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.postgresql.jdbc;

    opens com.lab.controller to javafx.fxml;
    opens com.lab.domain to javafx.base;
    exports com.lab;
}