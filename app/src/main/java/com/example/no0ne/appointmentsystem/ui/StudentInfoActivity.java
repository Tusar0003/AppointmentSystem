package com.example.no0ne.appointmentsystem.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.no0ne.appointmentsystem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class StudentInfoActivity extends AppCompatActivity {

    private static final int GALLERY_PICK = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserReference;
    private StorageReference mStorageReference;

    private ImageView mStudentImageView;
    private EditText mUserNameEditText;
    private EditText mEmailEditText;
    private EditText mDepartmentEditText;
    private EditText mBatchEditText;
    private EditText mIdEditText;

    private String mUserName;
    private String mEmail;
    private String mDepartment;
    private String mBatch;
    private String mId;
    private String mCurrentUserId;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        mStudentImageView = findViewById(R.id.image_view_student);
        mUserNameEditText = findViewById(R.id.edit_text_user_name);
        mEmailEditText = findViewById(R.id.edit_text_email);
        mDepartmentEditText = findViewById(R.id.edit_text_department);
        mBatchEditText = findViewById(R.id.edit_text_batch);
        mIdEditText = findViewById(R.id.edit_text_id);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);

//        mStudentImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Image"), GALLERY_PICK);
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setPersonalInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveChanges();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
//            Uri imageUri = data.getData();
//            CropImage.activity(imageUri)
//                    .setAspectRatio(1, 1)
//                    .setMinCropWindowSize(500, 500)
//                    .start(this);
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setMessage("Uploading");
//            mProgressDialog.show();
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//
//            if (resultCode == RESULT_OK) {
//                Uri imageUri = result.getUri();
//                File imageFile = new File(imageUri.getPath());
//                byte[] imageByte = compressingImage(imageFile);
//
//                mStorageReference = FirebaseStorage.getInstance().getReference().child("profile_images")
//                        .child(mCurrentUserId + ".jpg");
//
//                UploadTask uploadTask = mStorageReference.putBytes(imageByte);
//
//                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Downloading the compressed image url.
//                            String imageUrl = task.getResult().getDownloadUrl().toString();
//
//                            Map map = new HashMap<String, String>();
//                            map.put("image", imageUrl);
//
//                            mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
//                                @Override
//                                public void onComplete(@NonNull Task task) {
//                                    if (task.isSuccessful()) {
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(StudentInfoActivity.this, "Profile Picture Updated Successfully!",
//                                                Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        mProgressDialog.dismiss();
//                                        Toast.makeText(StudentInfoActivity.this, "Profile Picture Updated Failed!",
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        } else {
//                            mProgressDialog.dismiss();
//                            Toast.makeText(StudentInfoActivity.this, "Profile Picture Updated Failed!", Toast.LENGTH_SHORT)
//                                    .show();
//                        }
//                    }
//                });
//            }
//        }
//    }

    private void setPersonalInfo() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserName = dataSnapshot.child("user_name").getValue().toString();
                mEmail = dataSnapshot.child("email").getValue().toString();

                mUserNameEditText.setText(mUserName);
                mEmailEditText.setText(mEmail);

                try {
                    mDepartment = dataSnapshot.child("department").getValue().toString();
                    mBatch = dataSnapshot.child("batch").getValue().toString();
                    mId = dataSnapshot.child("id").getValue().toString();

                    mDepartmentEditText.setText(mDepartment);
                    mBatchEditText.setText(mBatch);
                    mIdEditText.setText(mId);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveChanges() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        mUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        mUserName = mUserNameEditText.getText().toString();
        mEmail = mEmailEditText.getText().toString();
        mDepartment = mDepartmentEditText.getText().toString();
        mBatch = mBatchEditText.getText().toString();
        mId = mIdEditText.getText().toString();

        Log.e("USER_NAME", mUserName);

        if (TextUtils.isEmpty(mUserName)) {
            Toast.makeText(this, "User name is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mEmail)) {
            Toast.makeText(this, "Email is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mDepartment)) {
            Toast.makeText(this, "Department is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mBatch)) {
            Toast.makeText(this, "Batch is empty!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mId)) {
            Toast.makeText(this, "ID is empty!", Toast.LENGTH_SHORT).show();
        } else {
            Map map = new HashMap();
            map.put("user_name", mUserName);
            map.put("email", mEmail);
            map.put("department", mDepartment);
            map.put("batch", mBatch);
            map.put("id", mId);

            mUserReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(StudentInfoActivity.this, "Updates successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StudentInfoActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Compressing the original image
    private byte[] compressingImage(File thumbFile) {
        Bitmap thumbBitmap = null;
        try {
            thumbBitmap = new Compressor(this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(50)
                    .compressToBitmap(thumbFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        byte[] thumbByte = outputStream.toByteArray();

        return thumbByte;
    }
}
