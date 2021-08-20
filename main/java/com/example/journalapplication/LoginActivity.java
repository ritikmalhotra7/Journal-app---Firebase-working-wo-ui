package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.journalapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("User");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        firebaseAuth = FirebaseAuth.getInstance();

        binding.createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.progressbar.setVisibility(View.VISIBLE);
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
                binding.progressbar.setVisibility(View.INVISIBLE);

            }
        });
        binding.loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailid.getText().toString();
                String password = binding.password.getText().toString();
                if(!TextUtils.isEmpty(binding.emailid.getText().toString()) && !TextUtils.isEmpty(binding.password.getText().toString())){
                    loginUser(email,password);
                }else{
                    Toast.makeText(LoginActivity.this, "You have to enter before click", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginUser(String email, String password) {
        binding.progressbar.setVisibility(View.VISIBLE);
        if(!TextUtils.isEmpty(binding.emailid.getText().toString()) && !TextUtils.isEmpty(binding.password.getText().toString())){
            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    firebaseUser = firebaseAuth.getCurrentUser();
                    //Log.d("taget", firebaseUser.toString());
                    String userId = firebaseUser.getUid();
                    //Log.d("taget", userId);
                    collectionReference.whereEqualTo("userid", userId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.d("taget", error.toString());
                            }
                            if (value != null) {
                                for (QueryDocumentSnapshot v : value) {
                                    JournalApi.getInstance().setUserName(v.getString("username"));
                                    JournalApi.getInstance().setUserid(v.getString("userid"));
                                    startActivity(new Intent(LoginActivity.this, JournalListActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "else", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    binding.progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Account not found!", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}