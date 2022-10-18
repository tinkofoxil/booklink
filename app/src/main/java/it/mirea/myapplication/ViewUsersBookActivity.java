package it.mirea.myapplication;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.json.JSONArray;
import org.json.JSONObject;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.reginald.editspinner.EditSpinner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.mirea.myapplication.model.NewBook;


public class ViewUsersBookActivity extends Activity {

    private final static int SELECT_IMAGE = 1;

    private static boolean f;
    private EditText editBookName, editBookAuthor, editNumberOfPages;
    TextView bookNameOriginal;
    private ImageView bookImg, editBookImg;
    private TextView metaInfText;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Intent intent;
    private File path;
    private Uri selectedImgUri;
    private EditSpinner category;
    private List<String> categories;
    private String id;
    private Uri selectedDocUri;
    private String categoryValue;
    private View loadingView;
    private boolean categoryF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_users_book_page);
        selectedImgUri = null;
        f = false;
        categoryF = false;
        intent = getIntent();
        downloadFile();
        getCategories();
        loadingView = findViewById(R.id.loading_spinner);
        editBookName = findViewById(R.id.editBookName);
        editNumberOfPages = findViewById(R.id.editNumberOfPages);
        bookNameOriginal = findViewById(R.id.bookNameOriginal);
        bookNameOriginal.setText(intent.getStringExtra("bookTitle"));
        metaInfText = findViewById(R.id.metaInfText);
        editBookAuthor = findViewById(R.id.editBookAuthor);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("logo");
        System.out.println(intent.getStringExtra("bookTitle") + ".txt " + "logo.jpeg");
        bookImg = findViewById(R.id.bookImg);
        editBookImg = findViewById(R.id.editBookImg);
        category = findViewById(R.id.edit_spinner);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                categories);
        category.setAdapter(adapter);
        category.selectItem(0);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryValue = categories.get(position);
                if (position == 0) {
                    category.setEditable(true);
                    categoryF = true;
                }
            }
        });
        // Load the image using Glide
        Glide.with(this)
                .load(storageReference)
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(bookImg);
        readTxt();


    }

    private void downloadFile() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("metainf");

        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        File localFile = new File(rootPath, "metainf");
        path = rootPath;

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

        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("description");

        rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        localFile = new File(rootPath, "description.txt");
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

        rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        localFile = new File(rootPath, "categoryList.json");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library").child("categoryList.json");
        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", "найс");
                //  updateDb(timestamp,localFile.toString(),position);
                getCategories();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", "не найс");
            }
        });
    }

    public void readTxt() {
        //Get the text file
        //Read text from file
        File file = new File(path, "metainf.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        //Set the text
        metaInfText.setText(text);
    }

    public void onEditBookImg(View view) {
        // in onCreate or any event where your want the user to
        // select a file
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Выберите мем"), SELECT_IMAGE);
    }

    public void changeToDescription(View view) {
        File file;
        if (f == true) {
            //Get the text file
            //Read text from file
            file = new File(path, "metainf.txt");
            f = false;
        } else {
            file = new File(path, "description.txt");
            f = true;
        }
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
                System.out.println(line);
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        //Set the text
        metaInfText.setText(text);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isStoragePermissionGranted();
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                selectedImgUri = data.getData();
                File file = new File(selectedImgUri.getPath());//create path from uri
                String path = file.getPath();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("logo");
                UploadTask uploadTask = storageRef.putFile(selectedImgUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        Toast.makeText(ViewUsersBookActivity.this, "Иконка книги обновлена!", Toast.LENGTH_SHORT).show();
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("logo");
                        Glide.with(ViewUsersBookActivity.this)
                                .load(storageRef)
                                .apply(RequestOptions.skipMemoryCacheOf(true))
                                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                .into(bookImg);
                        return null;
                    }
                });
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    public void ClickMain(View view) {
        Intent intent = new Intent(ViewUsersBookActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        Intent intent;
        intent = new Intent(ViewUsersBookActivity.this, ProfileSupervisorActivity.class);
        switch (new Single().getInstance().type) {

            case 0:
                intent = new Intent(ViewUsersBookActivity.this, ProfileActivity.class);
                break;
            case 1:
                intent = new Intent(ViewUsersBookActivity.this, ProfileAuthorActivity.class);
                break;
            case 2:
                intent = new Intent(ViewUsersBookActivity.this, ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    public void ClickSettings(View view) {
        Intent intent = new Intent(ViewUsersBookActivity.this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void getCategories() {
        categories = new ArrayList<String>();
        try (FileReader fileReader = new FileReader((path + "/categoryList.json"))) {

            JsonArray objects = Jsoner.deserializeMany(fileReader);

            Mapper mapper = new DozerBeanMapper();

            JsonArray o = (JsonArray) objects;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                for (int i = 0; i < ((List<String>)((Map)(o.get(0))).get("Array")).size(); i++) {
                    categories.add(((List<String>)((Map)(o.get(0))).get("Array")).get(i));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }

    }

    public void getid() {
        try (FileReader fileReader = new FileReader((path + "/categoryList.json"))) {
            JsonArray objects = Jsoner.deserializeMany(fileReader);
            Mapper mapper = new DozerBeanMapper();
            JsonArray o = (JsonArray) objects;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                id = (((((Map)(o.get(0))).get("booksAmount")))).toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonException e) {
            e.printStackTrace();
        }

    }


    public void clickUpload(View view) throws IOException {
        String title;
        String author, pageNumber, bookType;
        NewBook newBook;
        if (editNumberOfPages.getText().toString() == null) {
            Toast.makeText(this, "Введите время, за которое читается произведение!", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            pageNumber = editNumberOfPages.getText().toString();
        }
        if (!TextUtils.isEmpty(editBookName.getText().toString())) {
            title = editBookName.getText().toString();
        }
        else{
            Intent intent = getIntent();
            title = intent.getStringExtra("bookTitle");
        }
        if (editBookAuthor.getText().toString() == null) {
            Toast.makeText(this, "Введите автора произведения!", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            author = editBookAuthor.getText().toString();
        }
        System.out.println(category.getListSelection());

        bookType = categoryValue;
        getid();
        newBook = new NewBook(Integer.parseInt(id), pageNumber, title);
        newBook.setAuthor(author);
        newBook.setType(bookType);
        newBook.setTitle(title);

        // pretty print
        String json = Jsoner.prettyPrint(newBook.toString());

        System.out.println(json);

        // Java objects to JSON file
        try (FileWriter fileWriter = new FileWriter(path + "/bookJson.json")) {
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (selectedImgUri == null) {
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child("logo");

            File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }

            File localFile = new File(rootPath, "logo.jpeg");
            path = rootPath;

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
            selectedImgUri = Uri.fromFile(localFile);
        }
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library");
        StorageReference uploadBook = storageReference.child(newBook.getTitle() + ".txt");
        uploadBook.child("logo").putFile(selectedImgUri);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking").child(intent.getStringExtra("bookTitle") + ".txt").child(intent.getStringExtra("bookTitle"));
        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        File localFile = new File(rootPath, newBook.getTitle() + ".txt");
        localFile.createNewFile();
        path = rootPath;
        storageReference.getFile(new File(rootPath, newBook.getTitle() + ".txt")).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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
        selectedDocUri = Uri.fromFile(localFile);
        uploadBook.child(title + ".txt").putFile(Uri.fromFile(/*new File(/*rootPath, title + ".txt"*/localFile));
        uploadBook.child("metainf.json").putFile(Uri.fromFile(new File(rootPath, "bookJson.json")));
        UploadTask uploadTask = uploadBook.child("description.txt").putFile(Uri.fromFile(new File(rootPath, "description.txt")));
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                Toast.makeText(ViewUsersBookActivity.this, "Книга добавлена в библиотеку!", Toast.LENGTH_SHORT).show();
                return null;
            }
        });
        Map <String, Integer> map = new HashMap<>();
        JSONObject jsonStr = new JSONObject(map);
        String js = "{\"Map\": " + jsonStr.toString() + "}";
        String jsonString;
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference users = db.getReference("/");
        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("reading").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    new Single().getInstance().reading = (String) task.getResult().getValue();
                }
            }
        });
        jsonString = new Single().getInstance().reading;
        if (jsonString.replace(" ", "") != "{\"Map\":{}}") {
            try (StringReader reader = new StringReader(jsonString)) {
                JsonArray objects = Jsoner.deserializeMany(reader);
                Mapper mapper = new DozerBeanMapper();
                JsonArray o = (JsonArray) objects;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    List<String> keys = new ArrayList<>(((Map) (((Map) (o.get(0))).get("Map"))).keySet());
                    List<Integer> values = new ArrayList<>(((Map) (((Map) (o.get(0))).get("Map"))).values());
                    for (int i = 0; i < keys.size(); i++) {
                        System.out.println(keys.get(i).replace(" ", "") + " " + title.replace(" ", ""));
                        if(!keys.get(i).equals(title)) {
                            map.put(keys.get(i), Integer.parseInt(String.valueOf(values.get(i))));
                        }
                    }
                }
            } catch (JsonException e) {
                e.printStackTrace();
            }
        }

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



        deleteFolder();
        Intent intent = new Intent(ViewUsersBookActivity.this, ProfileSupervisorActivity.class);
        startActivity(intent);
        finish();
    }

    public void clickDelete(View view){
        deleteFolder();
        Intent intent = new Intent(ViewUsersBookActivity.this, ProfileSupervisorActivity.class);
        startActivity(intent);
        finish();
    }

    public void clickRead(View view) {
        Intent newIntent = new Intent(ViewUsersBookActivity.this, TextbookActivity.class);
        newIntent.putExtra("bookTitle", intent.getStringExtra("bookTitle"));
        newIntent.putExtra("from", "reading");
        startActivity(newIntent);
    }


    public void deleteFolder() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking");
        storageReference.child(intent.getStringExtra("bookTitle") + ".txt").child(intent.getStringExtra("bookTitle") + ".txt").delete();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking");
        storageReference.child(intent.getStringExtra("bookTitle") + ".txt").child("logo").delete();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking");
        storageReference.child(intent.getStringExtra("bookTitle") + ".txt").child("description").delete();
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/For Checking");
        storageReference.child(intent.getStringExtra("bookTitle") + ".txt").child("metainf").delete();
    }

    @Override
    protected void onResume() {
        super.onResume();
        categoryF = false;
    }

}

