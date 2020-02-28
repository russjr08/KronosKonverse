open module KonverseClient {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires gson;
    requires commons.io;
    requires org.apache.httpcomponents.httpclient.fluent;
    requires okhttp3;
    requires core;
    requires guava;
    requires java.sql;
    exports com.kronosad.konverse.client;
    exports com.kronosad.konverse.client.window;
    exports com.kronosad.konverse.common.auth;

}