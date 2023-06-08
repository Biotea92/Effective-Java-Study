package org.example.chapter02.item01;

public class Book {

    private String title;
    private String author;

    private Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public static Book createWithTitleAndAuthor(String title, String author) {
        return new Book(title, author);
    }

    public static void main(String[] args) {
//        Book book = new Book("effective java", "Joshua Bloch");
        Book book = Book.createWithTitleAndAuthor("effective java", "Joshua Bloch");
    }
}
