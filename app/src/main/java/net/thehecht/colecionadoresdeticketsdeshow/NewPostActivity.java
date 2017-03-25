package net.thehecht.colecionadoresdeticketsdeshow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {


    static final int REQUEST_IMAGE_CAPTURE = 1;

    private File tempFile = null;
    private File cropTempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(NewPostActivity.this.getPackageManager()) != null) {
            try {
                tempFile = File.createTempFile("temp", ".jpg", NewPostActivity.this.getCacheDir());
            } catch (IOException ex) {
                Snackbar.make(findViewById(android.R.id.content), "Houve um erro ao criar o arquivo temporário...", Snackbar.LENGTH_LONG).show();
            }
            if (tempFile != null) {
                Uri tempFileUri = FileProvider.getUriForFile(NewPostActivity.this, "net.thehecht.colecionadoresdetickets.fileprovider", tempFile);
                Log.d("DEBUG", tempFileUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (tempFile == null) return;
            UCrop.Options options = new UCrop.Options();
            options.setHideBottomControls(true);
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setCompressionQuality(80);
            try {
                cropTempFile = File.createTempFile("temp_crop", ".jpg", NewPostActivity.this.getCacheDir());
            } catch (IOException e) {
                Snackbar.make(findViewById(android.R.id.content), "Houve um erro ao criar o arquivo temporário...", Snackbar.LENGTH_LONG).show();
            }
            UCrop.of(Uri.fromFile(tempFile), Uri.fromFile(cropTempFile))
                    .withAspectRatio(600, 400)
                    .withMaxResultSize(600, 400)
                    .withOptions(options)
                    .start(this);
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            final Uri resultUri = UCrop.getOutput(data);
            ((ImageView)findViewById(R.id.image)).setImageURI(resultUri);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Snackbar.make(findViewById(android.R.id.content), "Houve um erro ao recortar a foto...", Snackbar.LENGTH_LONG).show();
        }
    }



}
