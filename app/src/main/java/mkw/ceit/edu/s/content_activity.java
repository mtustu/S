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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class content_activity extends AppCompatActivity {
    ImageButton imgbtn;
    EditText t1;
    EditText t2;
    private static final int GAllERY_REQUEST = 2;
    private Uri uri = null;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference mDatabaseUser;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=(EditText) findViewById(R.id.editText);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = database.getInstance().getReference().child("test1");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

    }

    public void ImageButtonClicked(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,GAllERY_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GAllERY_REQUEST && resultCode == RESULT_OK) {
            uri = data.getData();
            imgbtn = (ImageButton) findViewById(R.id.imageButton);
            imgbtn.setImageURI(uri);

        }
    }

    public void OnClick(View view) {
        final String tValue = t1.getText().toString().trim();

        if(!TextUtils.isEmpty(tValue))
        {

            final StorageReference ref = storageReference.child("Post Image").child(Objects.requireNonNull(uri.getLastPathSegment()));
            UploadTask uploadTask = ref.putFile(uri);

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
                        final String downloadURL = downloadUri.toString();
                        Toast.makeText(content_activity.this,"Upload Complete",Toast.LENGTH_LONG).show();

                       final DatabaseReference newPost = databaseReference.push();
                       mDatabaseUser.addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               newPost.child("title").setValue(tValue);
                               newPost.child("image").setValue(downloadURL.toString());
                               newPost.child("uid").setValue(mCurrentUser.getUid());

                               newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            Intent intent = new Intent(content_activity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                   }
                               });
                               newPost.child("profileImage").setValue(dataSnapshot.child("image").getValue());
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });


                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
           /* StorageReference filepath = storageReference.child("Post Image").child(Objects.requireNonNull(uri.getLastPathSegment()));
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Changed
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(MainActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();
                    DatabaseReference newPost = databaseReference.push();
                    newPost.child("title").setValue(tValue);
                    newPost.child("desc").setValue(dtitle);
                    newPost.child("image").setValue(downloadUrl.toString());

                }
            });   */

        }
        Intent intent = new Intent(content_activity.this,MainActivity.class);
       startActivity(intent);
        finish();
    }

    public void CancelOnClick(View view) {
        Intent intent = new Intent(content_activity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
        finish();
    }



  /*  private RecyclerView test1;
    private DatabaseReference mdatabase;
    private Context mctx;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_main);
        test1 = (RecyclerView) findViewById(R.id.list_re);
        test1.setHasFixedSize(true);
        test1.setLayoutManager(new LinearLayoutManager(this));
        mdatabase = FirebaseDatabase.getInstance().getReference().child("test1");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent = new Intent(content_activity.this,RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };



    }


    @Override
    protected void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
         FirebaseRecyclerOptions<testclass> options =
                new FirebaseRecyclerOptions.Builder<testclass>()
                        .setQuery(mdatabase, testclass.class)
                        .build();
        FirebaseRecyclerAdapter<testclass,testViewHolder> FRA= new FirebaseRecyclerAdapter<testclass, testViewHolder>(options) {
            @NonNull
            @Override
            public testViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test1_row,parent,false);
                return new testViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull testViewHolder viewHolder, int position, @NonNull testclass model) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                System.out.println(model.getImage());
                viewHolder.setImage(getApplicationContext(), model.getImage());


            }
        };
        FRA.startListening();
        test1.setAdapter(FRA);


  }



    public static class testViewHolder extends RecyclerView.ViewHolder{



        testViewHolder(View itemView)
        {
            super(itemView);
            View mview =itemView;
        }
        public void setTitle(String title)
        {
            TextView post_title = itemView.findViewById(R.id.textTitle);
            post_title.setText(title);
        }
        public void setDesc(String Desc)
        {
            TextView post_desc = itemView.findViewById(R.id.textDesc);
            post_desc.setText(Desc);
        }
        public void setImage(Context ctx,String image)
        {
            ImageView post_imageA = (ImageView) itemView.findViewById(R.id.postimage);
            Picasso.get().load(image).into(post_imageA);

        }



    }  */

}
