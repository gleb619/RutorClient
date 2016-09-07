package org.rutor.team619.rutorclient.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.rutor.team619.rutorclient.R;
import org.rutor.team619.rutorclient.model.Image;
import org.rutor.team619.rutorclient.model.TargetWithUrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by BORIS on 19.11.2015.
 */
public class ImageSliderAdapter extends PagerAdapter {

    private static final String TAG = ImageSliderAdapter.class.getName() + ":";

    private final Context context;
    private final List<Image> images;
    private final Picasso picasso;
    @Bind(R.id.page_detail_row_image_holder)
    LinearLayout linearLayout;
    @Bind(R.id.page_detail_row_image_view)
    ImageView imageView;
    private boolean isFirstTime = true;

    public ImageSliderAdapter(Context context, List<Image> images, Picasso picasso) {
        this.context = context;
        this.images = images;
        this.picasso = picasso;
    }

    @Override
    public int getCount() {
        if (isFirstTime) {
            return (images != null && images.size() > 0) ? 1 : 0;
        } else {
            return images != null ? images.size() : 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_slide, container, false);
        try {
            ButterKnife.bind(this, view);
        } catch (RuntimeException e) {
            Log.e(TAG, "instantiateItem#Error: ", e);
        }
        Log.d(TAG, "instantiateItem[" + position + "] start: " + new SimpleDateFormat("hh:mm:ss").format(new Date()));
        Log.d(TAG, "instantiateItem#container: " + container);

        imageView.setImageResource(R.drawable.background);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "onClick#v: " + v);
            }
        });

//        Picasso picasso = Picasso.with(context);
//        picasso.setIndicatorsEnabled(true);
//        picasso.setLoggingEnabled(true);

        picasso.load(images.get(position).getSrc())
                .placeholder(R.drawable.background_loader)
                .error(R.drawable.background_error)
                .into(new TargetWithUrl(images.get(position).getSrc()) {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        Log.d(TAG, "onBitmapLoaded#bitmap: " + bitmap.getHeight() + ", from: " + from);
//                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        if (bitmap.getHeight() >= 100) {
//                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                            imageView.setAdjustViewBounds(true);
//                            imageView.setAdjustViewBounds(false);
//                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//                            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                            layoutParams.height

//                            Picasso.with(context)
                            picasso.load(getUrl())
                                    .fit()
                                    .centerInside()
                                    .into(imageView);
                        } else if (isFirstTime) {
                            picasso.load(getUrl())
                                    .into(imageView);
                        } else {
                            int height = bitmap.getHeight() * 2;
                            int width = bitmap.getWidth() * 2;

                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                            layoutParams.height = (int) convertDpToPixel(height, context);
                            layoutParams.width = (int) convertDpToPixel(width, context);
//                            layoutParams.height = height;
//                            layoutParams.width = width;
                            imageView.setAdjustViewBounds(true);

//                            Picasso.with(context)
                            picasso.load(getUrl())
//                                    .resize(width, height)
//                                    .fit()
//                                    .centerCrop()
//                                    .centerInside()
                                    .into(imageView);
                        }

                        if (isFirstTime) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isFirstTime = false;
                                    notifyDataSetChanged();
                                }
                            }, 20);
                        }

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imageView.setImageDrawable(placeHolderDrawable);
                    }
                });

        container.addView(linearLayout);
        Log.e(TAG, "***");

        return view;
    }

    public float convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public float convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        Picasso.with(context).cancelRequest(target);
        container.removeView((View) object);
    }

}
