package mkw.ceit.edu.s;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

public class SetupActivity extends AppCompatActivity {

    private ImageButton imgBtn;
    private EditText editDisplayName;
    private static final int GALL_REQU=1;
    private Uri mImageUri = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mdatabaseUser;
    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        imgBtn=findViewById(R.id.setUpImgBtn);
        editDisplayName=findViewById(R.id.displayName);
        mdatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_image");

    }

    public void profileImgClick(View view) {
        Intent galleryInt=new Intent(Intent.ACTION_GET_CONTENT);
        galleryInt.setType("image/*");
        startActivityForResult(galleryInt,GALL_REQU);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALL_REQU && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);




        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                mImageUri =result.getUri();
                imgBtn.setImageURI(mImageUri);

            } else if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)

             {
                Exception error = result.getError();
            }
        }




    }

    public void doneBtnClick(View view) {

        final String Uname=editDisplayName.getText().toString().trim();
        final String user_id = mAuth.getCurrentUser().getUid();
        if(!TextUtils.isEmpty(Uname)&& mImageUri != null)
        {

            final StorageReference ref = mStorageRef.child("Post_mage").child(Objects.requireNonNull(mImageUri.getLastPathSegment()));
            UploadTask uploadTask = ref.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadURL = downloadUri.toString();
                        Toast.makeText(SetupActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();

                        DatabaseReference newPost = mdatabaseUser;
                        newPost.child(user_id).child("name").setValue(Uname);
                        newPost.child(user_id).child("image").setValue(downloadURL.toString());
                        Intent intent = new Intent(SetupActivity.this,Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }




    }
}
