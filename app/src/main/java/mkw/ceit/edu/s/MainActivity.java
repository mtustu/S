package mkw.ceit.edu.s;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private RecyclerView test1;
    private DatabaseReference mdatabase;
    private Context mctx;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseRecyclerAdapter<testclass, testViewHolder> FRA;
    Parcelable recylerViewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_act);

        test1 = (RecyclerView) findViewById(R.id.list_re);
        test1.setItemViewCacheSize(20);
        test1.setHasFixedSize(true);
        test1.setLayoutManager(new LinearLayoutManager(this));
        mdatabase = FirebaseDatabase.getInstance().getReference().child("test1");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, Login.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                }
            }
        };
        FirebaseRecyclerOptions<testclass> options =
                new FirebaseRecyclerOptions.Builder<testclass>()
                        .setQuery(mdatabase, testclass.class)
                        .build();
        FRA = new FirebaseRecyclerAdapter<testclass, testViewHolder>(options) {
            @NonNull
            @Override
            public testViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test1_row, parent, false);
                return new testViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull testViewHolder viewHolder, int position, @NonNull testclass model) {
                final String post_key = getRef(position).getKey().toString();
                viewHolder.setTitle(model.getTitle());

                viewHolder.setPImage(getApplicationContext(), model.getProfileImage());
                viewHolder.setImage(viewHolder, model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, SimpleSingletest.class);
                        intent.putExtra("Post id", post_key);
                        startActivity(intent);
                    }
                });
            }
        };



    }

    @Override
    protected void onPause() {
        super.onPause();
        recylerViewState = test1.getLayoutManager().onSaveInstanceState();   }

    @Override
    protected void onResume()
    {
        super.onResume();
        test1.getLayoutManager().onRestoreInstanceState(recylerViewState);
    }
    public void onBackPressed() {
        finish();
        // System.exit(0);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.addOp) {
            Intent intent = new Intent(MainActivity.this, content_activity.class);
            startActivity(intent);

        } else if (id == R.id.logout) {
            mAuth.signOut();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
        FRA.startListening();
        test1.setAdapter(FRA);
    }


    public class testViewHolder extends RecyclerView.ViewHolder {


        View mview;

        testViewHolder(View itemView) {
            super(itemView);
            mview = itemView;
        }

        public void setTitle(String title) {
            TextView post_title = mview.findViewById(R.id.textTitle);
            post_title.setText(title);
        }


        public void setImage(RecyclerView.ViewHolder holder, final String image) {
            ImageView post_imageA =(ImageView) mview.findViewById(R.id.postimage);
            Picasso.get().load(image).into(post_imageA);
        }



        public void setUsername(String userName) {
            TextView username = mview.findViewById(R.id.username);
            username.setText(userName);
        }

        public void setPImage(Context ctx, String profileImage) {
            ImageView profile_img = (ImageView) mview.findViewById(R.id.postProfileimg);
            Picasso.get().load(profileImage).transform(new CircleTransform()).into(profile_img);

        }


    }

}

class CircleTransform implements Transformation {

    boolean mCircleSeparator = false;

    public CircleTransform() {
    }

    public CircleTransform(boolean circleSeparator) {
        mCircleSeparator = circleSeparator;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setShader(shader);
        float r = size / 2f;
        canvas.drawCircle(r, r, r - 1, paint);
        // Make the thin border:
        Paint paintBorder = new Paint();
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(Color.argb(84,0,0,0));
        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(1);
        canvas.drawCircle(r, r, r-1, paintBorder);

        // Optional separator for stacking:
        if (mCircleSeparator) {
            Paint paintBorderSeparator = new Paint();
            paintBorderSeparator.setStyle(Paint.Style.STROKE);
            paintBorderSeparator.setColor(Color.parseColor("#ffffff"));
            paintBorderSeparator.setAntiAlias(true);
            paintBorderSeparator.setStrokeWidth(4);
            canvas.drawCircle(r, r, r+1, paintBorderSeparator);
        }
        squaredBitmap.recycle();
        return bitmap;
    }


    @Override
    public String key() {
        return "circle";
    }}



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