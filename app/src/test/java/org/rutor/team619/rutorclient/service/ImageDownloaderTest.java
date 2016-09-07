package org.rutor.team619.rutorclient.service;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by BORIS on 23.08.2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImageDownloaderTest {

    @InjectMocks
    ImageDownloader imageDownloader;

    private Bitmap bitmap;
    private String imageUrl;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        imageUrl = "file://N:/TEST/RUTOR_PAGE/img/2bb4172ce74b.jpg";
        bitmap = createBitmap();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Bitmap createBitmap() throws IOException {
        String url = "http://upload.wikimedia.org/wikipedia/commons/9/9c/Image-Porkeri_001.jpg";
        Bitmap bitmap;
        try (InputStream in = new URL(url).openStream()) {
            bitmap = BitmapFactory.decodeStream(in);
        }

        return bitmap;
    }

    @Test
    public void testGetColor() throws Exception {
//        int color = imageDownloader.getColors(bitmap);
//        Assert.assertNotNull(color);
    }
}