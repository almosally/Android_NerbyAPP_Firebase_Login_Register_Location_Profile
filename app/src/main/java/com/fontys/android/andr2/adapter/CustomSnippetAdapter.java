package com.fontys.android.andr2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.models.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static org.greenrobot.eventbus.EventBus.TAG;

public class CustomSnippetAdapter implements GoogleMap.InfoWindowAdapter {

    private final View window;
    private Context context;
    private String UID;

    public CustomSnippetAdapter(Context context, String UID) {
        this.context = context;
        this.UID = UID;
        window = LayoutInflater.from(context).inflate(R.layout.custom_snippet, null);
    }

    private void renderWindowText(Marker marker, View view) {
        TextView snippetTitle = view.findViewById(R.id.snippetTitle);
        TextView snippetPhoneNumber = view.findViewById(R.id.snippetPhoneNumber);
        TextView snippetEmileAddress = view.findViewById(R.id.snippetEmileAddress);
        ImageView snippetUserImage = view.findViewById(R.id.snippetUserImage);

        HashMap<String, User> userInfoHashMap = (HashMap<String, User>) marker.getTag();
        for (Map.Entry<String, User> entry : userInfoHashMap.entrySet()) {
            String uid = entry.getKey();
            User value = entry.getValue();

            int imageId = context.getResources().getIdentifier("" + R.drawable.default_image, null, null);
            try {
                if (value.getEmail().equals(marker.getSnippet())) {
                    snippetTitle.setText(marker.getTitle());
                    snippetPhoneNumber.setText(value.getPhoneNumber());
                    snippetEmileAddress.setText(value.getEmail());

                    if (!value.getUserImage().isEmpty()) {
                        Picasso.get().load(value.getUserImage()).transform(new CropCircleTransformation()).fit().centerCrop().error(R.drawable.default_image).into(snippetUserImage);
                    } else {
                        snippetUserImage.setImageResource(imageId);
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "render data: get user information from db ", ex);

            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, window);
        return window;
    }
}
