package mkw.ceit.edu.s;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import mkw.ceit.edu.s.R;

public class MainActivity extends AppCompatActivity {
    private RecyclerView test1;
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
                    Intent loginIntent = new Intent(MainActivity.this,Login.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };



    }

    @Override
    public void onBackPressed() {
        finish();
        // System.exit(0);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.addOp)
        {
            Intent intent = new Intent(MainActivity.this,content_activity.class);
            startActivity(intent);

        }
        else if(id == R.id.logout)
        {
            mAuth.signOut();
        }


        return super.onOptionsItemSelected(item);
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
                final String post_key = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());

                viewHolder.setPImage(getApplicationContext(),model.getProfileImage());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,SimpleSingletest.class);
                        intent.putExtra("Post id",post_key);
                        startActivity(intent);
                    }
                });
            }
        };
        FRA.startListening();
        test1.setAdapter(FRA);


    }

    public static class testViewHolder extends RecyclerView.ViewHolder{


        View mview;
        testViewHolder(View itemView)
        {
            super(itemView);
            mview =itemView;
        }
        public void setTitle(String title)
        {
            TextView post_title = mview.findViewById(R.id.textTitle);
            post_title.setText(title);
        }
        public void setDesc(String Desc)
        {
            TextView post_desc = mview.findViewById(R.id.textDesc);
            post_desc.setText(Desc);
        }
        public void setImage(Context ctx, String image)
        {
            ImageView post_imageA = (ImageView) mview.findViewById(R.id.postimage);
            Picasso.get().load(image).into(post_imageA);

        }
        public  void setUsername(String userName)
        {
            TextView username = mview.findViewById(R.id.username);
            username.setText(userName);
        }
        public void setPImage(Context ctx, String profileImage)
        {
            ImageView profile_img = (ImageView) mview.findViewById(R.id.postProfileimg);
            Picasso.get().load(profileImage).into(profile_img);

        }




    }



 /*   ImageButton imgbtn;
    EditText t1;
    EditText t2;
    private static final int GAllERY_REQUEST = 2;
    private Uri uri = null;
    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t1=(EditText) findViewById(R.id.editText);
        t2=(EditText) findViewById(R.id.editText2);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = database.getInstance().getReference().child("test1");

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
        final String dtitle = t2.getText().toString().trim();

        if(!TextUtils.isEmpty(tValue) && !TextUtils.isEmpty(dtitle))
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
                        String downloadURL = downloadUri.toString();
                        Toast.makeText(MainActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();
                        DatabaseReference newPost = databaseReference.push();
                        newPost.child("title").setValue(tValue);
                        newPost.child("desc").setValue(dtitle);
                        newPost.child("image").setValue(downloadURL.toString());

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
            });

        }
        Intent intent = new Intent(MainActivity.this,content_activity.class);
        startActivity(intent);

    }

    public void CancelOnClick(View view) {
        Intent intent = new Intent(MainActivity.this,content_activity.class);
        startActivity(intent);
    }*/
}
