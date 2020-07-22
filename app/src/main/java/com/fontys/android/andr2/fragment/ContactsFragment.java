package com.fontys.android.andr2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.adapter.ContactAdapter;
import com.fontys.android.andr2.models.User;

import java.util.List;

public class ContactsFragment extends Fragment {
    private static final String TAG = "ContactsFragment";
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;
    private List<User> userList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        return view;
    }
}
