package org.rutor.team619.colorthief;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Arrays;

import de.androidpit.colorthief.MMCQ;

/**
 * Created by BORIS on 22.08.2016.
 */
public class ColorThief {

    private static final int DEFAULT_QUALITY = 10;
    private static final boolean DEFAULT_IGNORE_WHITE = true;

    public ColorThief() {
    }

    public static int[] getColor(Bitmap sourceImage) {
        if (sourceImage == null) {
            return null;
        }

        int[][] palette = getPalette(sourceImage, 5);
        if (palette == null) {
            return null;
        } else {
            int[] dominantColor = palette[0];
            return dominantColor;
        }
    }

    public static int[] getColor(Bitmap sourceImage, int quality, boolean ignoreWhite) {
        int[][] palette = getPalette(sourceImage, 5, quality, ignoreWhite);
        if (palette == null) {
            return null;
        } else {
            int[] dominantColor = palette[0];
            return dominantColor;
        }
    }

    public static int[][] getPalette(Bitmap sourceImage) {
        return getPalette(sourceImage, 5);
    }

    public static int[][] getPalette(Bitmap sourceImage, int colorCount) {
        MMCQ.CMap cmap = getColorMap(sourceImage, colorCount);
        return cmap == null ? null : cmap.palette();
    }

    public static int[][] getPalette(Bitmap sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        MMCQ.CMap cmap = getColorMap(sourceImage, colorCount, quality, ignoreWhite);
        return cmap == null ? null : cmap.palette();
    }

    public static MMCQ.CMap getColorMap(Bitmap sourceImage, int colorCount) {
        return getColorMap(sourceImage, colorCount, DEFAULT_QUALITY, DEFAULT_IGNORE_WHITE);
    }

    public static MMCQ.CMap getColorMap(Bitmap sourceImage, int colorCount, int quality, boolean ignoreWhite) {
        if (sourceImage == null || sourceImage.getConfig() == null) {
            return null;
        }

        int[][] pixelArray;

        switch (sourceImage.getConfig()) {
            case ARGB_8888:
                pixelArray = getPixelsSlow(sourceImage, quality, ignoreWhite);
                break;
            default:
                throw new IllegalArgumentException("Unknown type of image, , type: " + sourceImage.getConfig());
        }

        MMCQ.CMap cmap = MMCQ.quantize(pixelArray, colorCount);
        return cmap;
    }

    private static int[][] getPixelsSlow(Bitmap sourceImage, int quality, boolean ignoreWhite) {
        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight() / 2;
        int pixelCount = width * height;
        int numRegardedPixels = (pixelCount + quality - 1) / quality;
        int numUsedPixels = 0;
        int[][] res = new int[numRegardedPixels][];

        for (int i = 0; i < pixelCount; i += quality) {
            int row = i / width;
            int col = i % width;

            int colour = sourceImage.getPixel(col, row);
            int r = Color.red(colour);
            int b = Color.blue(colour);
            int g = Color.green(colour);
            int a = Color.alpha(colour);

//            int rgb = sourceImage.getRGB(col, row);
//            int r = rgb >> 16 & 255;
//            int g = rgb >> 8 & 255;
//            int b = rgb & 255;

            if (a >= 125 && (!ignoreWhite || r <= 250 || r <= 250 || r <= 250)) {
                res[numUsedPixels] = new int[]{r, g, b};
                ++numUsedPixels;
            }
        }

        sourceImage.recycle();
        return Arrays.copyOfRange(res, 0, numUsedPixels);
    }

}
