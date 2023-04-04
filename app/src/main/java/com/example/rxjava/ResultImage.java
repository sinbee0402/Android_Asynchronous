package com.example.rxjava;

public class ResultImage {
    private String id;
    private String previewURL;

    public ResultImage(String id, String previewURL) {
        this.id = id;
        this.previewURL = previewURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    @Override
    public String toString() {
        return "ResultImage{" +
                "id='" + id + '\'' +
                ", previewURL='" + previewURL + '\'' +
                '}';
    }
}
