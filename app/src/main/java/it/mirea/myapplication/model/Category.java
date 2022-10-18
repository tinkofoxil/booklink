package it.mirea.myapplication.model;

public class Category {

    int id; // id категории
    String title; // имя категории

    // конструктор
    public Category(int id, String title) {
        this.id = id;
        this.title = title;
    }

    // Геттеры
    // Метод возвращает id категории
    public int getId() {
        return id;
    }

    // Метод возвращает имя категории
    public String getTitle() {
        return title;
    }

    // Сеттеры
    // Метод устанавливает id категории
    public void setId(int id) {
        this.id = id;
    }

    // Метод устанавливает имя категории
    public void setTitle(String title) {
        this.title = title;
    }

}
