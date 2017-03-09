package com.curvedpin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ben on 3/3/17.
 */
public class CharacterImage {
    byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public CharacterImage(BufferedImage characterImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(characterImage, "png", baos);
        this.content = baos.toByteArray();
    }
}
