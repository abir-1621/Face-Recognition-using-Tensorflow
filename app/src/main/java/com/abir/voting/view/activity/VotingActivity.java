package com.abir.voting.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.abir.voting.R;
import com.abir.voting.adapter.CandidateAdapter;
import com.abir.voting.model.Candidate;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class VotingActivity extends AppCompatActivity implements CandidateAdapter.OnSelected{
    private RecyclerView candidateRecycler;
    private List<Candidate> candidates=new ArrayList<>();
    private DatabaseReference topJin;
    private CandidateAdapter candidateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        findAttributes();
        topJin = FirebaseDatabase.getInstance().getReference("Candidate");
        topJin.addValueEventListener(candidatesEventListener);
       // Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/voting-1c928.appspot.com/o/Untitled%20design%20(1).png?alt=media&token=e4cd1da9-7a11-4d7d-bcc3-025de1effd60").into((ImageView) findViewById(R.id.testImage));



    }

    private void findAttributes() {
        candidateRecycler = findViewById(R.id.candidateRecycler);
    }

    private void candidateRecyclerSetup() {
        if (candidates != null) {
            candidateRecycler.setHasFixedSize(true);
            candidateRecycler.setLayoutManager(new GridLayoutManager(this, 2));
            candidateAdapter = new CandidateAdapter(candidates);
            candidateAdapter.setOnItemClickListener(this);
            candidateRecycler.setAdapter(candidateAdapter);
            //firebaseUploadBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
        }
    }

    private ValueEventListener candidatesEventListener = new ValueEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                Candidate candidatePacket = postSnapshot.getValue(Candidate.class);
                assert candidatePacket != null;
                candidates.add(candidatePacket);
            }
            candidateRecyclerSetup();
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };


    private void firebaseUploadBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        StorageReference imageStorage = FirebaseStorage.getInstance().getReference();;
        StorageReference imageRef = imageStorage.child("images/" + "imageName");

        Task<Uri> urlTask = imageRef.putBytes(data).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                System.out.println("##############################!task.isSuccessful()@@@@@@@@@@@@@@@@@@@@@@@@@@@#########");
                throw task.getException();
            }

            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                String uri = downloadUri.toString();
                System.out.println("##############################@@@@@@@@@@@@@@@@@@@@@@@@@@@#########"+uri);
//                sendMessageWithFile(uri);
            } else {
                // Handle failures
                // ...

                System.out.println("##############################@ else@@@@@@@@@@@@@@@@@@@@@@@@@@#########");
            }
//            progressBar.setVisibility(View.GONE);
        });

    }

    @Override
    public void SelectedCandidate(Candidate candidate) {

    }
}