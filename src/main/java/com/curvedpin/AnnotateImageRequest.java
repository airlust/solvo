package com.curvedpin;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by ben on 3/3/17.
 */
public class AnnotateImageRequest {
    CharacterImage image;
    VisionFeature[] features;

    public CharacterImage getImage() {
        return image;
    }

    public VisionFeature[] getFeatures() {
        return features;
    }

    public void setFeatures(VisionFeature[] features) {
        this.features = features;
    }

    public void setImage(CharacterImage image) {
        this.image = image;
    }

    public AnnotateImageRequest(BufferedImage characterImage) throws IOException {
        this.image = new CharacterImage(characterImage);
        this.features = new VisionFeature[1];
        this.features[0] = new VisionFeature("TEXT_DETECTION");
    }
}
