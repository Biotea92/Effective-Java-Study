package org.example.chapter02.item01;

public class DataSource {

    private String url;
    private String username;
    private String password;

    private DataSource() {
    }

    private static final DataSource INSTANCE = new DataSource();

    public static DataSource getInstance() {
        return INSTANCE;
    }
}
