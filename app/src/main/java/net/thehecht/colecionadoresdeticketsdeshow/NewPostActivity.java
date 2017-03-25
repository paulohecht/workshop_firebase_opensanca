package net.thehecht.colecionadoresdeticketsdeshow;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        findViewById(R.id.post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = ((EditText)findViewById(R.id.comment)).getText().toString();

                DatabaseReference db = FirebaseDatabase.getInstance().getReference();

                String postId = db.push().getKey();

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map postValues = new HashMap();
                postValues.put("userId", userId);
                postValues.put("comment", comment);
                postValues.put("image", null);
                postValues.put("likesCount", 0);
                postValues.put("commentsCount", 0);
                postValues.put("createdAt", ServerValue.TIMESTAMP);

                Map updateValues = new HashMap();
                updateValues.put("posts/" + postId, postValues);
                updateValues.put("userPosts/" + userId + "/" + postId, postValues);

                db.updateChildren(updateValues, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            //TODO: Deu erro e agora?
                            return;
                        }
                        finish();
                    }
                });

            }
        });
    }
}
