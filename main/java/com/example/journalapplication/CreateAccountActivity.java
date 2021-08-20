package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.journalapplication.databinding.ActivityCreateAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private ActivityCreateAccountBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String password_key = "password";
    private final String userid_key = "userid";
    private final String username_key = "username";
    private final String email_key = "email";
    private CollectionReference collectionRefrence = db.collection("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        /*authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    //user already logged in
                }else {
                    //have no user now
                }
            }
        };*/

        binding.createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /*createNewUser(binding.emailid.getText().toString(),binding.password.getText().toString(),binding.username.getText().toString());*/
                if (!TextUtils.isEmpty(binding.emailid.getText().toString())
                        && !TextUtils.isEmpty(binding.password.getText().toString())
                        && !TextUtils.isEmpty(binding.username.getText().toString())) {

                    createNewUser(binding.emailid.getText().toString(),binding.password.getText().toString(),binding.username.getText().toString());

                }else {
                    Toast.makeText(CreateAccountActivity.this,
                            "Empty Fields Not Allowed",
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });


    }

    private void createNewUser(String emailid, String password, String username) {
        if(!TextUtils.isEmpty(emailid) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username)){
            binding.progressbar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        firebaseUser = firebaseAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();

                        Map<String,String> user = new HashMap<>();
                        user.put(password_key, password);
                        user.put(email_key,emailid);
                        user.put(username_key,username);

                        user.put(userid_key,userid);

                        collectionRefrence.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(task.getResult()).exists()){
                                            binding.progressbar.setVisibility(View.INVISIBLE);

                                            JournalApi.getInstance().setUserName(username);
                                            JournalApi.getInstance().setUserid(userid);

                                            Intent in = new Intent(CreateAccountActivity.this, JournalListActivity.class);
                                            startActivity(in);

                                        }else{
                                            Toast.makeText(CreateAccountActivity.this, "something went wrong on else of documentRefrence", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        binding.progressbar.setVisibility(View.INVISIBLE);
                                        Log.d("taget", "onFailure: " + e);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.progressbar.setVisibility(View.INVISIBLE);
                                Log.d("taget", "onFailure: " + e);
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    binding.progressbar.setVisibility(View.INVISIBLE);

                    Toast.makeText(CreateAccountActivity.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                    Log.d("taget", e.toString());
                }
            });
        }else {
            Toast.makeText(this, "You have to Enter Something!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}