package com.ozonetech.ozochat.view.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.ozonetech.ozochat.R;
import com.ozonetech.ozochat.network.SoketService;

public abstract class BaseActivity extends AppCompatActivity implements SoketService.SocketConnectionListner {

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        SoketService.setSocketConnectionListner(this);
    }

    protected void showProgressDialog(String msg) {
        try {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setMessage(msg);
                progressDialog.show();
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showSnackbar(View view, String message, int duration) {
        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        snackbar.show();
    }


    protected void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void loadImage(Object mContext, ImageView imageView,
                          final ProgressBar pb_image, String imageUrl, int placeHolder) {
        this.loadImage(mContext, imageView, pb_image, imageUrl, placeHolder, placeHolder, placeHolder, 200);
    }
    public void loadImageSDV(Object mContext, SimpleDraweeView imageView,
                             final ProgressBar pb_image, String imageUrl, int placeHolder) {
        this.loadImageSDV(mContext, imageView, pb_image, imageUrl, placeHolder, placeHolder, placeHolder, 200);
    }


    public void loadImage(Object mContext, ImageView imageView,
                          final ProgressBar pb_image, String imageUrl,
                          int placeHolder, int error, int fallBack, int requireWidth) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                pb_image.setVisibility(View.INVISIBLE);
            }
            imageView.setImageResource(fallBack);
            return;
        }
        if (mContext == null) return;

        try {
            RequestManager requestManager = null;
            if (mContext instanceof Activity) {
                requestManager = Glide.with((Activity) mContext);
            } else if (mContext instanceof Fragment) {
                requestManager = Glide.with(((Fragment) mContext).getActivity());
            } else {
                requestManager = Glide.with((Context) mContext);
            }
            if (requestManager == null) return;
            RequestOptions options = new RequestOptions()
                    .placeholder(placeHolder)
                    .fallback(error);
            if (requireWidth > 0) {
                options = options.override(requireWidth);
            }

            options = options.error(error)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .priority(Priority.HIGH);

            if (pb_image != null && pb_image.getVisibility() != View.VISIBLE) {
                pb_image.setVisibility(View.VISIBLE);
            }
            requestManager.asBitmap().load(imageUrl).
                    apply(options).
                    listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                                pb_image.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                                pb_image.setVisibility(View.INVISIBLE);
                            }
                            return false;
                        }
                    }).centerCrop().into(imageView);

        } catch (Exception e) {

        }

    }


    public void loadImageSDV(Object mContext, SimpleDraweeView imageView,
                             final ProgressBar pb_image, String imageUrl,
                             int placeHolder, int error, int fallBack, int requireWidth) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            if (pb_image != null && pb_image.getVisibility() != View.INVISIBLE) {
                pb_image.setVisibility(View.INVISIBLE);
            }
            imageView.setImageResource(fallBack);
            return;
        }
        if (mContext == null) return;

        try {
            imageView.setController(getDraveenController(imageView , imageUrl , requireWidth));

        } catch (Exception e) {

        }

    }

    private DraweeController getDraveenController(SimpleDraweeView mImageView, String vendorImg, int requireWidth) {
        Uri uri = Uri.parse(vendorImg);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(requireWidth, requireWidth))
                .setProgressiveRenderingEnabled(false)
                .build();

        return Fresco.newDraweeControllerBuilder()
                .setOldController(mImageView.getController())
                .setImageRequest(request)
                .build();
    }


    @Override
    public void onSocketConnect(boolean flag) {

    }
}