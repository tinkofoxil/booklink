package it.mirea.myapplication.model;

import com.google.firebase.storage.StorageReference;

public class Book {

    int id;
    String type, title, author, number_of_pages;

    StorageReference img, description, text;

    public Book() {
        this.id = 0;
        this.number_of_pages = "number_of_pages";
        this.type = "type";
        this.title = "title";
        this.author = "author";
    }

    public Book(int id, String number_of_pages, String type, String title, String author, StorageReference img, StorageReference description, StorageReference text) {
        this.id = id;
        this.number_of_pages = number_of_pages;
        this.type = type;
        this.title = title;
        this.img = img;
        this.description = description;
        this.text = text;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNumber_of_pages() {
        return number_of_pages;
    }

    public void setNumber_of_pages(String number_of_pages) {
        this.number_of_pages = number_of_pages;
    }

    public StorageReference getImg() {
        return img;
    }

    public void setImg(StorageReference img) {
        this.img = img;
    }

    public StorageReference getDescription() {
        return description;
    }

    public void setDescription(StorageReference description) {
        this.description = description;
    }

    public StorageReference getText() {
        return text;
    }

    public void setText(StorageReference text) {
        this.text = text;
    }
}
