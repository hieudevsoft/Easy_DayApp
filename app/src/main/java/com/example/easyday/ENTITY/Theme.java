package com.example.easyday.ENTITY;

public class Theme {
    private String id;
    private String urlTheme;

    public Theme(String id, String urlTheme) {
        this.id = id;
        this.urlTheme = urlTheme;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrlTheme() {
        return urlTheme;
    }

    public void setUrlTheme(String urlTheme) {
        this.urlTheme = urlTheme;
    }
}
