package com.example.journalapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.journalapplication.databinding.ActivityJournalListBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JournalListActivity extends AppCompatActivity {
    private ActivityJournalListBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Journals");
    private List<Journal> journalList ;
    private RecyclerView rv;
    private JournalRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJournalListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        binding.textView2.setVisibility(View.INVISIBLE);
        journalList = new ArrayList<>();
        rv = binding.recyclerview;
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_layout,menu);
        return super.onCreateOptionsMenu(menu);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (firebaseUser != null && firebaseAuth != null) {
            switch (item.getItemId()) {
                case R.id.addnew:
                    startActivity(new Intent(JournalListActivity.this, PostJournalActivity.class));
                    break;
                case R.id.sign_out:
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,LoginActivity.class));
                    finish();
                    break;
            }
        }
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        onStop();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        collectionReference.whereEqualTo("userId", JournalApi.getInstance()
                .getUserid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot journals : queryDocumentSnapshots) {
                                Journal journal = journals.toObject(Journal.class);
                                journalList.add(journal);
                            }

                            //Invoke recyclerview
                            adapter = new JournalRecyclerAdapter(JournalListActivity.this,
                                    journalList);
                            rv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(JournalListActivity.this, "Here are your Journals", Toast.LENGTH_SHORT).show();

                        }else {
                            binding.textView2.setVisibility(View.VISIBLE);
                        }

                    }
                });
        super.onStart();
    }
}