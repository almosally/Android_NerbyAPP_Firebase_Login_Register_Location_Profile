package com.fontys.android.andr2.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.models.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class BitmapCustomMarker {
    private static ImageView userStatusOnline;
    private static ImageView userStatusOffline;

    public BitmapCustomMarker() {

    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String status) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_profile_image, null);

        userStatusOnline = marker.findViewById(R.id.userStatusOnline);
        userStatusOffline = marker.findViewById(R.id.userStatusOffline);

        if (status.equals("Online")) {
            userStatusOnline.setVisibility(View.VISIBLE);
            userStatusOffline.setVisibility(View.INVISIBLE);
        }
        if (status.equals("Offline")) {
            userStatusOnline.setVisibility(View.INVISIBLE);
            userStatusOffline.setVisibility(View.VISIBLE);
        }

        CircleImageView markerImage = marker.findViewById(R.id.mapProfileImage);
        markerImage.setImageResource(resource);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = marker.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        marker.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap createCustomMarker(Context context, User user, String status) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_profile_image, null);

        userStatusOnline = marker.findViewById(R.id.userStatusOnline);
        userStatusOffline = marker.findViewById(R.id.userStatusOffline);

        if (status.equals("Online")) {
            userStatusOnline.setVisibility(View.VISIBLE);
            userStatusOffline.setVisibility(View.INVISIBLE);
        }
        if (status.equals("Offline")) {
            userStatusOnline.setVisibility(View.INVISIBLE);
            userStatusOffline.setVisibility(View.VISIBLE);
        }

        int imageId = context.getResources().getIdentifier("" + R.drawable.default_image, null, null);
        CircleImageView markerImage = marker.findViewById(R.id.mapProfileImage);

        if (!user.getUserImage().isEmpty()) {
            Picasso.get().load(user.getUserImage()).transform(new CropCircleTransformation()).fit().centerCrop().error(R.drawable.default_image).into(markerImage);
        } else {
            markerImage.setImageResource(imageId);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = marker.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        marker.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap createCustomMarker(Context context, Bitmap bitmap, String status) {
        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_profile_image, null);

        CircleImageView markerImage = marker.findViewById(R.id.mapProfileImage);
        markerImage.setImageBitmap(bitmap);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, marker.getMeasuredWidth(), marker.getMeasuredHeight());
        marker.buildDrawingCache();

        Bitmap returnedBitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = marker.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        marker.draw(canvas);
        return returnedBitmap;
    }
}
