module com.example.javachatapplication {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                        requires org.kordamp.bootstrapfx.core;
            
    opens com.example.javachatapplication to javafx.fxml;
    exports com.example.javachatapplication;
}
