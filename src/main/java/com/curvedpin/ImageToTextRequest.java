package com.curvedpin;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by ben on 3/3/17.
 */
public class ImageToTextRequest {

    public AnnotateImageRequest[] getRequests() {
        return requests;
    }

    public void setRequests(AnnotateImageRequest[] requests) {
        this.requests = requests;
    }

    private AnnotateImageRequest[] requests;

    public ImageToTextRequest(BufferedImage characterImage) throws IOException {
        requests = new AnnotateImageRequest[1];
        requests[0] = new AnnotateImageRequest(characterImage);
    }
}
