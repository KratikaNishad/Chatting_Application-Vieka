package com.example.veika;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.veika.databinding.ActivityUserProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class UserProfile extends AppCompatActivity {


    private static final String TAG = "UserProfile";
    private ActivityUserProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImageGallery;
    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // for uploading time
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile....");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        getSupportActionBar().hide();

        //jump to main screen activity
        binding.contiue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.UserName.getText().toString();
                if (name.isEmpty()) {
                    binding.UserName.setError("Please type your name");
                    return;
                }
                dialog.show();
                if (selectedImageGallery != null) {
                    StorageReference reference = storage.getReference().child("ProfilesImagesFromGallery").child(auth.getUid());
                    reference.putFile(selectedImageGallery).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    //user profile link
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        String uid = auth.getUid();
                                        String phone = auth.getCurrentUser().getPhoneNumber();
                                        String name = binding.UserName.getText().toString();

                                        User user = new User(uid, name, phone, imageUrl);

                                        database.getReference().child("users").child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                binding.contiue.setVisibility(View.GONE);
                                                binding.pb3.setVisibility(View.VISIBLE);
                                                Intent intent = new Intent(UserProfile.this, MainScreenActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }); //if user not select any image.
                }     else {

                    if (selectedImageGallery == null) {

                        String uid = auth.getUid();
                        String phone = auth.getCurrentUser().getPhoneNumber();
                        User user = new User(uid, name, phone, "No Image");
                        database.getReference().child("users").child(uid).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                dialog.dismiss();
                                binding.contiue.setVisibility(View.GONE);
                                binding.pb3.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(UserProfile.this, MainScreenActivity.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        });

                    }
                }
            }
        });
        
        // add image for profile
        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseProfilePic();

            }
        });
    }
    
    //for choosing image upload options
    private void chooseProfilePic(){
        AlertDialog.Builder builder=new AlertDialog.Builder(UserProfile.this);
        LayoutInflater inflater=getLayoutInflater();
        View dialogView=inflater.inflate(R.layout.alert_dialog_profile_image,null);
        builder.setCancelable(false);
        builder.setView(dialogView);
        
        ImageView imageViewCamera=dialogView.findViewById(R.id.ADPI_Camera);
        ImageView imageViewGallery=dialogView.findViewById(R.id.ADPI_Gallery);
        
        final AlertDialog alertDialogProfilePic=builder.create();
        alertDialogProfilePic.show();
        
        //for camera
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAndRequestPermissions()) {
                    takePicFromCamera();
                    alertDialogProfilePic.cancel();
                }
            }
        });
        
        //for gallery
        imageViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicFromGallery();
                alertDialogProfilePic.cancel();
            }
        });
    }
    
    private void takePicFromGallery(){
        Intent pickPhoto=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto,1);
    }

    private void takePicFromCamera(){
        Intent takePic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePic, 2);
        }
    }

    //set uploaded image to the imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if(resultCode==RESULT_OK){
                    Uri selectedImageUri=data.getData();
                    binding.profileImage.setImageURI(selectedImageUri);
   //                 selectedImageGallery= data.getData();
                }
                break;
            case 2:
                if(resultCode== RESULT_OK){
                    Bundle bundle=data.getExtras();
                    Bitmap bitmap=(Bitmap) bundle.get("data");
                    binding.profileImage.setImageBitmap(bitmap);
      //              handleUpload(bitmap);
                }
                break;
        }
    }
    //For converting bitmap(thumbnail) to jpeg(Uri)

    private void handleUpload(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("ProfileImagesFromCamera")
                .child(uid + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ",e.getCause() );
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        String name = binding.UserName.getText().toString();
        if (name.isEmpty()) {
            binding.UserName.setError("Please type your name");
            return;
        }
        dialog.show();
        binding.contiue.setVisibility(View.GONE);
        binding.pb3.setVisibility(View.VISIBLE);
        dialog.dismiss();

        Intent intent = new Intent(UserProfile.this, MainScreenActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private boolean checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT>=23){
            int cameraPermission= ActivityCompat.checkSelfPermission(UserProfile.this, Manifest.permission.CAMERA);
            if(cameraPermission== PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(UserProfile.this,new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==20 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(UserProfile.this,"permission granted",Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(UserProfile.this, "permission not granted", Toast.LENGTH_SHORT).show();
    }
}



