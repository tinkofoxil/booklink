package it.mirea.myapplication.model;

public class NewBook {

    int id;
    String number_of_pages, author;
    String type;
    String title;
    String img;


    public NewBook(int id, String number_of_pages, String title) {
        this.number_of_pages = number_of_pages;
        this.title = title;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber_of_pages() {
        return number_of_pages;
    }

    public void setNumber_of_pages(String number_of_pages) {
        this.number_of_pages = number_of_pages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "{\n\"Book\": \n{\n\"id\":" + id + ",\n\"number_of_pages\": " + '"' + number_of_pages + '"' + ",\n\"type\": " + '"' + type + '"' + ",\n\"title\": " + '"' + title + '"' + ",\n\"author\": " + '"' + author + '"' + "\n}\n}";
    }
}
