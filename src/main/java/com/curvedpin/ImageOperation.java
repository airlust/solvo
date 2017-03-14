package com.curvedpin;

import java.awt.image.BufferedImage;

/**
 * Created by ben on 3/13/17.
 */
@FunctionalInterface
interface ImageOperation<E> {
    void doOp(BufferedImage bf, int currentCol, int cellNumber);
}
