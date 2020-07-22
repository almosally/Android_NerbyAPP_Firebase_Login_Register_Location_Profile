package com.fontys.android.andr2.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fontys.android.andr2.R;
import com.fontys.android.andr2.activities.ProfileActivity;
import com.fontys.android.andr2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    String DISPLAY_NAME = null, DISPLAY_NUMBER;
    int DISPLAY_RANGE;
    private EditText editProfileName;
    private EditText editProfilePhoneNumber;
    private EditText editProfileRange;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String UID;
    private DatabaseReference mRootRef, databaseReference, userLastLocation;
    private String username, phoneNumber;
    private int range;
    private TextView emailView;
    private ImageView editUserImage;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private StorageReference mStorageRef;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mStorageRef = FirebaseStorage.getInstance().getReference("Image");

        editProfileName = view.findViewById(R.id.editUserName);
        editProfilePhoneNumber = view.findViewById(R.id.editPhoneNumber);
        editProfileRange = view.findViewById(R.id.editRange);
        emailView = view.findViewById(R.id.textViewEmail);
        editUserImage = view.findViewById(R.id.edit_user_profile_pic);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        UID = firebaseUser.getUid();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        databaseReference = mRootRef.child("Users").child(UID);
        userLastLocation = mRootRef.child("Location").child(UID);
        if (databaseReference != null) {
            getUserInfo();
        }

        //update button
        Button saveEditButton = view.findViewById(R.id.save_edit_btn);

        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String profileName = editProfileName.getText().toString();
                String profilePhoneNumber = editProfilePhoneNumber.getText().toString();
                String profileRange = editProfileRange.getText().toString();

                // update the user profile information in Firebase database.
                if ((!profileName.isEmpty()) && (!profilePhoneNumber.isEmpty()) && (!profileRange.isEmpty())) {
                    updateProfile();
                    Intent firebaseUserIntent = new Intent(getContext(), ProfileActivity.class);
                    startActivity(firebaseUserIntent);
                } else {
                }
            }
        });

        //return button
        Button home_return = view.findViewById(R.id.return_btn);
        home_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firebaseUserIntent = new Intent(getContext(), ProfileActivity.class);
                startActivity(firebaseUserIntent);
            }
        });

        //upload user image btn
        Button uploadPicture = view.findViewById(R.id.upload_pic_btn);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileChooser();
            }
        });

        return view;
    }

    private void getUserInfo() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: User: " + dataSnapshot);
                User info = dataSnapshot.getValue(User.class);
                //  if(!info.getUserImage().equals(""))

                if (info.getUserImage().isEmpty()) {
                    Picasso.get().load(R.drawable.ic_person_profile).transform(new CropCircleTransformation()).fit().centerCrop().error(R.drawable.ic_person_profile).into(editUserImage);
                } else {
                    Picasso.get().load(info.getUserImage()).transform(new CropCircleTransformation()).fit().centerCrop().error(R.drawable.ic_person_profile).into(editUserImage);
                }
                range = info.getRange();
                username = info.getDisplayName();
                phoneNumber = info.getPhoneNumber();
                String email = info.getEmail();
                editProfileName.setText(username);
                editProfileRange.setText(String.valueOf(range));
                editProfilePhoneNumber.setText(phoneNumber);
                emailView.setText(email);
                // Picasso.get().load(info.getUserImage()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void updateProfile() {
        // [START update_profile]
        uploadImage();
        DISPLAY_NAME = editProfileName.getText().toString();
        DISPLAY_NUMBER = editProfilePhoneNumber.getText().toString().trim();
        DISPLAY_RANGE = Integer.parseInt(editProfileRange.getText().toString());

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference myref = db.getReference();
        myref.child("Users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                dataSnapshot.getRef().child("displayName").setValue(DISPLAY_NAME);
                dataSnapshot.getRef().child("phoneNumber").setValue(DISPLAY_NUMBER);
                dataSnapshot.getRef().child("range").setValue(DISPLAY_RANGE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });
    }
    // [END update_profile]

    //Chose Image
    private void FileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null && data.getData() != null) {
            mImageUri = data.getData();
            editUserImage.setImageURI(mImageUri);
            Log.d(TAG, "--------------------------------------------------------------");
            Log.d(TAG, "mImageUri: Location: " + mImageUri);
            Log.d(TAG, "--------------------------------------------------------------");


        }

    }

    private void uploadImage() {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(UID);
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // mProgressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                            fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downUri = task.getResult();
                                        String imageUrl = downUri.toString();
                                        Log.d(TAG, "--------------------------------------------------------------");
                                        Log.d(TAG, "usrImageLink: " + imageUrl);
                                        Log.d(TAG, "--------------------------------------------------------------");
                                        databaseReference.child("userImage").setValue(imageUrl);
                                        Toast.makeText(getContext(), imageUrl, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getContext(), "No New image selected", Toast.LENGTH_SHORT).show();
        }
    }


}
