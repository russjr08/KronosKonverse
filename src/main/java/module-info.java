module KonverseClient {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires gson;
    requires commons.io;
    requires core;
    requires fluent.hc;
    requires guava;
    requires java.sql;
    exports com.kronosad.konverse.client;
    exports com.kronosad.konverse.client.window;

    opens com.kronosad.konverse.client.window;
}