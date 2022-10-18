package it.mirea.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.mirea.myapplication.model.Book;

public class Single {

    private static final Single INSTANCE = new Single();

    public Single(){
        logic = false;
        name = "";
        email="";
        number="";
        text = "";
        type = 0;
        db = null;
        auth = null;
        reading = "";
        book = null;
        bookList = new ArrayList<>();
        myBookList = new ArrayList<>();
        bookNames = new ArrayList<>();
        localFile = null;
    }

    Boolean logic;
    String name, email, number, text, reading;
    FirebaseDatabase db;
    FirebaseAuth auth;
    Book book;
    int type;
    List <Book> bookList, myBookList;
    public List <String> bookNames;
    File localFile;

    public static Single getInstance(){
        return INSTANCE;
    }

    public Boolean getLogic() {
        return logic;
    }

    public void setLogic(Boolean logic) {
        this.logic = logic;
    }
}