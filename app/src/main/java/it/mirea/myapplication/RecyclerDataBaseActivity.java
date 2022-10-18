package it.mirea.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.mirea.myapplication.adapter.NewBookAdapter;
import it.mirea.myapplication.model.NewBook;

public class RecyclerDataBaseActivity extends Activity {

    static List<NewBook> bookList = new ArrayList<>();
    RecyclerView newBookRecycler;
    NewBookAdapter newBookAdapter;
    static boolean f = false;
    FirebaseStorage storage;
    StorageReference storageRef;
    List<String> folderNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_database_page);

        folderNames = new ArrayList<String>();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("/").child("For Checking");
        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                          @Override
                                          public void onSuccess(ListResult listResult) {
                                              folderNames.clear();
                                              bookList.clear();
                                              for (StorageReference prefix : listResult.getPrefixes()) {
                                                  folderNames.add(prefix.getName().split("\\.")[0]);
                                              }
                                              for(int i = 0; i < folderNames.size(); i++) {
                                                  bookList.add(new NewBook(i + 1, "123 стр", folderNames.get(i)));
                                              }
                                              if(bookList != null) {
                                                  setBookRecycler(bookList);
                                              }
                                              else {
                                                  Toast.makeText(RecyclerDataBaseActivity.this, "Что-то в этой жизни пошло не так", Toast.LENGTH_SHORT).show();
                                                  Intent intent = new Intent(RecyclerDataBaseActivity.this, MainActivity.class);
                                                  startActivity(intent);
                                                  finish();
                                              }
                                          }
                                      });


    }

    public void ClickMain(View view){
        Intent intent=new Intent(RecyclerDataBaseActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickSettings(View view) {
        Intent intent=new Intent(RecyclerDataBaseActivity.this,SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        Intent intent = new Intent(RecyclerDataBaseActivity.this,ProfileActivity.class);
        switch(new Single().getInstance().type){

            case 0:
                intent=new Intent(RecyclerDataBaseActivity.this,ProfileActivity.class);
                break;
            case 1:
                intent=new Intent(RecyclerDataBaseActivity.this,ProfileAuthorActivity.class);
                break;
            case 2:
                intent=new Intent(RecyclerDataBaseActivity.this,ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }


    private void setBookRecycler(List<NewBook> bookList) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
        newBookRecycler = findViewById(R.id.databaseRecyclerFolders);
        newBookRecycler.setLayoutManager(layoutManager);

        newBookAdapter = new NewBookAdapter(this, bookList);
        newBookRecycler.setAdapter(newBookAdapter);
    }


}
