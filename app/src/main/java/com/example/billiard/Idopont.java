package com.example.billiard;

public class Idopont {
    private String id;
    private String info;
    private String imageResource;
    private boolean foglalt;

    public Idopont(String id,String info, String imageResource, boolean foglalt) {
        this.id=id;
        this.info = info;
        this.imageResource = imageResource;
        this.foglalt = foglalt;
    }

    public Idopont() {
    }

    public String getInfo() {
        return info;
    }
    public String getImageResource() {
        return imageResource;
    }
    public boolean isFoglalt() { return foglalt; }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
