package it.mirea.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class BookActivity extends Activity {
    TextView bookAuthor, bookTitle, bookDescription;
    ImageView bookImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_page);

        bookImage = findViewById(R.id.bookImage);
        bookDescription = findViewById(R.id.bookDescription);
        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);

        //getImg();
        bookTitle.setText(getIntent().getStringExtra("bookTitle"));
        try {
            bookDescription.setText(getDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }
        bookAuthor.setText(getIntent().getStringExtra("bookAuthor"));
    }


    public void ClickMain(View view) {
        Intent intent = new Intent(BookActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        Intent intent;
        intent=new Intent(BookActivity.this,ProfileActivity.class);
        switch(new Single().getInstance().type){

            case 0:
                intent=new Intent(BookActivity.this,ProfileActivity.class);
                break;
            case 1:
                intent=new Intent(BookActivity.this,ProfileAuthorActivity.class);
                break;
            case 2:
                intent=new Intent(BookActivity.this,ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    public void ClickSettings(View view) {
        Intent intent = new Intent(BookActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickRead(View view){
        Intent intent = new Intent(BookActivity.this, TextbookActivity.class);
        new Single().getInstance().text = "Здесь будет книга";
        intent.putExtra("from", "reading_library");
        //intent.putExtra("bookText", new Single().getInstance().text);
        intent.putExtra("bookTitle", getIntent().getStringExtra("bookTitle"));
        startActivity(intent);
        finish();
    }

    public String getDescription() throws IOException {
        String line = "";
        String everything = "";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(getIntent().getStringExtra("bookTitle") + ".txt").child("description");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        File localFile = new File(rootPath, "description.txt");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", "найс");
                //  updateDb(timestamp,localFile.toString(),position);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", "не найс");
            }
        });
        BufferedReader br = new BufferedReader(new FileReader(localFile));
        try {
            StringBuilder sb = new StringBuilder();
            line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        return everything;
    }

//    public void getImg() {
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library").child(getIntent().getStringExtra("bookTitle") + ".txt").child("logo");
//        Glide.with(this)
//                .load(storageReference)
//                .apply(RequestOptions.skipMemoryCacheOf(true))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
//                .into(bookImage);
//
//    }
}