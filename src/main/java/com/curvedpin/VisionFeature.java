package com.curvedpin;

/**
 * Created by ben on 3/3/17.
 */
public class VisionFeature {
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public VisionFeature(String text_detection) {
        this.type = text_detection;
    }
}
