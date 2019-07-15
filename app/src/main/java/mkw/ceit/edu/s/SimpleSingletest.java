package mkw.ceit.edu.s;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SimpleSingletest extends AppCompatActivity {
    private ImageView singlePostImage;
    private TextView singletitle;
    private TextView singleDesc;
    private String Post_key = null;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private Button deletebtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_singletest);
        Post_key = getIntent().getExtras().getString("Post id");
        singlePostImage = (ImageView) findViewById(R.id.singleImageView);
        singletitle = (TextView) findViewById(R.id.singletitle);

        singleDesc = (TextView) findViewById(R.id.singleDesc);
        deletebtn = (Button) findViewById(R.id.deleteImage);
        deletebtn.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mdatabase = FirebaseDatabase.getInstance().getReference().child("test1");
        mdatabase.child(Post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post_title =(String) dataSnapshot.child("title").getValue();
                String post_desc =(String) dataSnapshot.child("desc").getValue();
                String post_image =(String) dataSnapshot.child("image").getValue();
                String post_uid =(String) dataSnapshot.child("uid").getValue();

                singletitle.setText(post_title);
                singleDesc.setText(post_desc);
                Picasso.get().load(post_image).into(singlePostImage);

                if(mAuth.getCurrentUser().getUid().equals(post_uid)){
                    deletebtn.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteClicked(View view) {
        mdatabase.child(Post_key).removeValue();
        Intent intent = new Intent(SimpleSingletest.this,MainActivity.class);
        startActivity(intent);
    }
}
