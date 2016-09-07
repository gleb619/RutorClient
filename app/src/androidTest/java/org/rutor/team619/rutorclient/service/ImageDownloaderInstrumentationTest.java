package org.rutor.team619.rutorclient.service;

import android.graphics.Bitmap;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by BORIS on 23.08.2016.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class ImageDownloaderInstrumentationTest {

    private static final String TAG = ImageDownloaderInstrumentationTest.class.getName() + ":";

    ImageDownloader imageDownloader;

    private Bitmap bitmap;
    private String imageUrl;
    private String imageUrl2;

    @Before
    public void setUp() throws Exception {
        imageDownloader = new ImageDownloader("");

        imageUrl = "file://N:/TEST/RUTOR_PAGE/img/2bb4172ce74b.jpg";
        imageUrl2 = "http://lorempicsum.com/futurama/350/200/1";
        bitmap = imageDownloader.getBitmapFromURL(imageUrl2);
        Assert.assertNotNull(bitmap);
    }

    @Test
    public void testGetColor() throws Exception {
//        int color = imageDownloader.getColors(bitmap);
//        Assert.assertNotNull(color);
    }

    @Test
    public void testDetectColor() throws Exception {
//        de.androidpit.colorthief.ColorThief colorThief2;
//        int[] color = imageDownloader.detectColors(bitmap);
//        Log.e(TAG, "COLOR: " + Arrays.toString(color));
//        System.out.println("ImageDownloaderInstrumentationTest.testDetectColor#COLOR: " + Arrays.toString(color));
//        Assert.assertEquals(color[0], 39);
//        Assert.assertEquals(color[1], 46);
//        Assert.assertEquals(color[2], 55);
    }
}