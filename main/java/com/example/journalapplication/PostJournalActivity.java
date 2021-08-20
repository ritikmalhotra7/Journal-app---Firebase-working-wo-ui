package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.journalapplication.databinding.ActivityPostJournalBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

public class PostJournalActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 25;
    private ActivityPostJournalBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference ;
    private Uri imageUri;
    private CollectionReference collectionReference = db.collection("Journals");

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            
            if(data != null){
                imageUri = data.getData();
                binding.image.setImageURI(imageUri);
                Toast.makeText(this, "Background changed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_post_journal);
        binding = ActivityPostJournalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        JournalApi j = JournalApi.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.saveJournal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(binding.titleof.getText().toString()) && !TextUtils.isEmpty(binding.thoughts.getText().toString()) && imageUri != null ){
                    saveJournal();
                }else{
                    Toast.makeText(PostJournalActivity.this, "something is missing!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(JournalApi.getInstance() != null){
            binding.titleof.setText(JournalApi.getInstance().getUserName());
            Date currentTime = Calendar.getInstance().getTime();
            binding.timed.setText(currentTime.toString());
        }

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,REQUEST_CODE);
                Toast.makeText(PostJournalActivity.this,"Select an Image!", Toast.LENGTH_SHORT).show();


            }
        });


        binding.progressbar.setVisibility(View.INVISIBLE);
        authStateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    //logedin
                }else {
                    //new user
                }
            }
        };


    }

    private void saveJournal() {
        binding.progressbar.setVisibility(View.VISIBLE);
        if(     !TextUtils.isEmpty(binding.title.getText()) &&
                !TextUtils.isEmpty(binding.thoughts.getText()) &&
                imageUri != null){

            StorageReference filePath = storageReference
                    .child("Journal" + JournalApi.getInstance().getUserid())
                    .child("image" + Timestamp.now().getSeconds());
            filePath
                    .putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    String username = JournalApi.getInstance().getUserName();
                                    String userid = JournalApi.getInstance().getUserid();

                                    Journal journal = new Journal();

                                    journal.setImageUrl(url);
                                    journal.setTitle(binding.title.getText().toString());
                                    journal.setThoughts(binding.thoughts.getText().toString());
                                    journal.setUserId(userid);
                                    journal.setTime(new Timestamp(new Date()));
                                    journal.setUserName(username);

                                    collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            binding.progressbar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(PostJournalActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.progressbar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PostJournalActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull  Exception e) {
                            binding.progressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostJournalActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("taget", e.toString());
                        }
            });

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}