package it.mirea.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextbookActivity extends Activity {

    private TextView article;
    String title, reading;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase db;
    DatabaseReference users;
    Map<String, Integer> map;
    ScrollView scroll;
    int last_page;
    File rootPath;
    File book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.textbook_page);
        scroll = findViewById(R.id.scroll);
        map = new HashMap<>();
        article = findViewById(R.id.article);
        title = getIntent().getStringExtra("bookTitle");
        storage = FirebaseStorage.getInstance();
        if(getIntent().getStringExtra("from").equals("checking")) {
            storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(title + ".txt").child(title + ".txt");
        }
        else {
            storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library").child(title + ".txt").child(title + ".txt");
        }
        try {
            readBook();
        } catch (IOException e) {
            e.printStackTrace();
        }
        db = FirebaseDatabase.getInstance();
        users = db.getReference("/");
        //if (getIntent().getStringExtra("from").equals("")) {
            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reading_library").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        new Single().getInstance().reading = (String) task.getResult().getValue();
                    }
                }
            });
        reading = new Single().getInstance().reading;
        if (reading.replace(" ", "") != "{\"Map\":{}}") {
            try (StringReader reader = new StringReader(reading)) {
                JsonArray objects = Jsoner.deserializeMany(reader);
                Mapper mapper = new DozerBeanMapper();
                JsonArray o = (JsonArray) objects;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    List<String> keys = new ArrayList<>(((Map) (((Map) (o.get(0))).get("Map"))).keySet());
                    List<Integer> values = new ArrayList<>(((Map) (((Map) (o.get(0))).get("Map"))).values());
                    for (int i = 0; i < keys.size(); i++) {
                        System.out.println(keys.get(i).replace(" ", "") + " " + title.replace(" ", ""));
                        map.put(keys.get(i), Integer.parseInt(String.valueOf(values.get(i))));
                        if(keys.get(i).equals(title)) {
                            scroll.setScrollY(Integer.parseInt(String.valueOf(values.get(i))));
                            int offsetY = Integer.parseInt(String.valueOf(values.get(i)));
                            scroll.post(new Runnable() {
                                public void run() {
                                    scroll.smoothScrollTo(0, offsetY);
                                }
                            });
                        }
                    }
                }
            } catch (JsonException e) {
                e.printStackTrace();
            }
        }
    }




    public void ClickMain(View view) {
        setLatRead();
        Intent intent = new Intent(TextbookActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        setLatRead();
        Intent intent;
        intent=new Intent(TextbookActivity.this,ProfileActivity.class);
        switch(new Single().getInstance().type){

            case 0:
                intent=new Intent(TextbookActivity.this,ProfileActivity.class);
                break;
            case 1:
                intent=new Intent(TextbookActivity.this,ProfileAuthorActivity.class);
                break;
            case 2:
                intent=new Intent(TextbookActivity.this,ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    public void ClickSettings(View view) {
        setLatRead();
        Intent intent = new Intent(TextbookActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }


    public File downloadBook() throws IOException {
        System.out.println(getIntent().getStringExtra("bookTitle"));
        storage = FirebaseStorage.getInstance();
        if (getIntent().getStringExtra("from").equals("checking")) {
            storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(title + ".txt").child(title + ".txt");
        }
        else {
            storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library").child(title + ".txt").child(title + ".txt");
        }
        rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        book = new File(rootPath, title + ".txt");
        book.createNewFile();
        storageReference.getFile(book).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", "найс");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", "не найс с файлом, как всегда блять");
            }
        });
        System.out.println(book);
        return book;
    }

    public void readBook() throws IOException {
        // first open the file and read it into a StringBuilder
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(downloadBook()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder total = new StringBuilder();
        String line = "";
        while(true) {
            try {
                if (!((line = r.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            total.append(line + "\n");
        }
        r.close();
        // then get the TextView and set its text
        article.setText(total.toString());
    }

    public void setLatRead() {
        map.put(title, scroll.getScrollY());
        JSONObject json = new JSONObject(map);
        String js = "{\"Map\": " + json.toString() + "}";

        if(getIntent().getStringExtra("from").equals("checking")) {
            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reading")
                    .setValue(js)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
        else {
            users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reading_library")
                    .setValue(js)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }

    }
}
