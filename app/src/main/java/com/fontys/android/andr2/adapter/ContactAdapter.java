package com.fontys.android.andr2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fontys.android.andr2.R;
import com.fontys.android.andr2.models.User;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;

    public ContactAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ContactAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.contact_username.setText(user.getDisplayName());

        if (user.getUserImage().equals("")) {
            holder.contact_profile_image.setImageResource(R.drawable.default_image);
        } else {
            Glide.with(context).load(user.getUserImage()).into(holder.contact_profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView contact_username;
        private ImageView contact_profile_image;

        public ViewHolder(View itemView) {
            super(itemView);

            contact_username = itemView.findViewById(R.id.contact_username);
            contact_profile_image = itemView.findViewById(R.id.contact_profile_image);

        }
    }
}
