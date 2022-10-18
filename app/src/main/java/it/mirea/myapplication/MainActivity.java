package it.mirea.myapplication;

import static android.os.Build.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.mirea.myapplication.adapter.BookAdapter;
import it.mirea.myapplication.adapter.CategoryAdapter;
import it.mirea.myapplication.model.Book;
import it.mirea.myapplication.model.Category;

public class MainActivity extends AppCompatActivity {

    static boolean f = false;
    List<String> categories;
    RecyclerView categoryRecycler, booksRecycler;
    CategoryAdapter categoryAdapter;
    static BookAdapter bookAdapter;
    StorageReference storageReference;
    FirebaseStorage storage;
    static Book book;
    static List<Book> bookList;
    static List<Book> fullBooksList = new ArrayList<>();
    List<Book> books;
    List<String> bookNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        bookList = new ArrayList<>();
        book = new Book();
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        if(new Single().getInstance().logic){
            setContentView(R.layout.activity_main);
            List<Category> categoryList = new ArrayList<>();
            getCategories();
            categoryList.add(new Category(0, "Все"));
            for(int i = 0; i < categories.size(); i++) {
                categoryList.add(new Category(i + 1, categories.get(i)));
            }
            setCategoryRecycler(categoryList);

            if (f == false) {
                getBooks();
                //fullBooksList.clear();
                System.out.println(new Single().getInstance().bookList.size());
                //getBooks();
                bookList.addAll(new Single().getInstance().bookList);
                for (int i = 0; i < bookList.size(); i++) {
                    fullBooksList.add(bookList.get(i));
                    System.out.println(fullBooksList.get(i).getTitle());
                }

                setBookRecycler(bookList);
                bookList.addAll(fullBooksList);
                f = true;
            }
            setBookRecycler(bookList);
        }
        else{
            Intent intent=new Intent(MainActivity.this,EntryActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void setBookRecycler(List<Book> bookList) {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false);
        booksRecycler = findViewById(R.id.booksRecycler);
        booksRecycler.setLayoutManager(layoutManager);
        bookAdapter = new BookAdapter(this, bookList);
        booksRecycler.setAdapter(bookAdapter);
    }


    private void setCategoryRecycler(List<Category> categoryList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        categoryRecycler = findViewById(R.id.categoryRecycler);
        categoryRecycler.setLayoutManager(layoutManager);
        categoryAdapter = new CategoryAdapter(this, categoryList);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    public void ClickSettings(View view) {
        Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void ClickProfile(View view) {
        Intent intent;
        intent=new Intent(MainActivity.this,ProfileActivity.class);
        switch(new Single().getInstance().type){

            case 0:
                intent=new Intent(MainActivity.this,ProfileActivity.class);
                break;
            case 1:
                intent=new Intent(MainActivity.this,ProfileAuthorActivity.class);
                break;
            case 2:
                intent=new Intent(MainActivity.this,ProfileSupervisorActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    public static void showBooksByCategory(String category){
        if (fullBooksList.size() != 0 && f) {
            bookList.clear();
            bookList.addAll(fullBooksList);
            List<Book> filterBooks = new ArrayList<>();
            if (category == "Все") {
                filterBooks.addAll(bookList);
            } else {
                for (Book i : bookList) {
                    System.out.println(i.getType() + " " + category);
                    if (i.getType().equals(category)) {
                        filterBooks.add(i);
                    }
                }
            }
            bookList.clear();
            bookList.addAll(filterBooks);
            bookAdapter.notifyDataSetChanged();
        }

    }


    public void getCategories() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
        File localFile = new File(rootPath, "categoryList.json");
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        storageReference = storage.getReferenceFromUrl("gs://library-805f6.appspot.com/Library").child("categoryList.json");
        storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.e("firebase ", "найс");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("firebase ", "не найс");
            }
        });
        categories = new ArrayList<String>();
        try (FileReader fileReader = new FileReader((rootPath + "/categoryList.json"))) {

            JsonArray objects = Jsoner.deserializeMany(fileReader);

            Mapper mapper = new DozerBeanMapper();

            JsonArray o = (JsonArray) objects;
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
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

    public void getBooks() {
        bookList = new ArrayList<>();
        bookNames = new ArrayList<>();
        books = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("/").child("Library");
        storageReference.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        bookNames.clear();
                        new Single().getInstance().bookNames.clear();
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            bookNames.add(prefix.getName().split("\\.")[0]);
                            System.out.println(prefix.getName().split("\\.")[0]);
                            new Single().getInstance().bookNames.add(prefix.getName().split("\\.")[0]);
                        }
                        File rootPath = new File(Environment.getExternalStorageDirectory(), "Downloads");
                        File localFile = new File(rootPath, "metainf.json");
                        for (int i = 0; i < new Single().getInstance().bookNames.size(); i++) {
                            if (i > 0)
                                localFile.delete();
                            StorageReference storageRef = storage.getReference("/").child("Library").child(bookNames.get(i) + ".txt").child("metainf.json");
                            localFile = new File(rootPath, "metainf.json");
                            if (!rootPath.exists()) {
                                rootPath.mkdirs();
                            }
                            int finalI = i;
                            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.e("firebase ", "найс");
                                    try (FileReader fileReader = new FileReader(new File(rootPath, "metainf.json"))) {
                                        JsonArray objects = Jsoner.deserializeMany(fileReader);
                                        JsonArray o = (JsonArray) objects;
                                        if (VERSION.SDK_INT >= VERSION_CODES.N) {
                                            String title = (String) (((Map) ((Map) (o.get(0))).get("Book"))).get("title");
                                            String author = (String) (((Map) ((Map) (o.get(0))).get("Book"))).get("author");
                                            String number_of_pages = (String) (((Map) ((Map) (o.get(0))).get("Book"))).get("number_of_pages");
                                            String type = (String) (((Map) ((Map) (o.get(0))).get("Book"))).get("type");
                                            StorageReference imgRef = storage.getReference("/").child("Library").child(title + ".txt").child("logo");
                                            StorageReference descRef = storage.getReference("/").child("Library").child(title + ".txt").child("description.txt");
                                            StorageReference textRef = storage.getReference("/").child("Library").child(title + ".txt").child(title + ".txt");

                                            book = new Book(finalI + 1, number_of_pages, type, title, author, imgRef, descRef, textRef);
                                            //bookList.add(book);
                                            new Single().getInstance().bookList.add(book);
                                            //setBookRecycler(fullBooksList);
                                            System.out.println("bookList" + bookList + " fullBooksList" + fullBooksList);
                                        }
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JsonException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                });
        bookList = new Single().getInstance().bookList;
        //setBookRecycler(bookList);
    }

    public void onUpdate(View view) {
        //bookAdapter.notifyDataSetChanged();
        if(fullBooksList.size() == 0) {
            fullBooksList.addAll(bookList);
        }
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fullBooksList.size() != 0 && f) {
            bookList.clear();
            bookList.addAll(fullBooksList);
            //List<Book> filterBooks = new ArrayList<>();
            //filterBooks.addAll(bookList);
            //bookList.clear();
            //bookList.addAll(filterBooks);
            //bookAdapter.notifyDataSetChanged();

        }
    }

}

