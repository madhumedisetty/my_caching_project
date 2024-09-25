package com.example.BookSystem;

import java.util.*;
import java.io.*;
import java.util.concurrent.*;
public class BookLibrarySystem {
    public static void main(String[] args) {
        BookLibrary bookLibrary=new BookLibrary();
        bookLibrary.addBook(new Book("123","Harry Potter","J.K.Rowling"));
        bookLibrary.addBook(new Book("456","To Kill a Mockingbird","Harper Lee"));
        bookLibrary.addBook(new Book("789","1984","George Orwell"));
        bookLibrary.addBook(new Book("101","Pride and Prejudice","Jane Austen"));
        bookLibrary.addBook(new Book("102","The Great Gatsby","F. Scott Fitzgerald"));
        bookLibrary.addBook(new Book("103","Moby Dick","Herman Melville"));
        bookLibrary.addBook(new Book("104","War and Peace","Leo Tolstoy"));
        bookLibrary.addBook(new Book("105","Hamlet","William Shakespeare"));
        bookLibrary.addBook(new Book("106","Macbeth","William Shakespeare"));
        
        System.out.println(bookLibrary.getAllBooks());
        System.out.println(bookLibrary.getAuthors());
        System.out.println(bookLibrary.getBookCountByAuthor());
        //How Future is used in Asynchronous programming
        //ExecutorService, Callable and Future
        //Explain the difference between Future and ExecutorService
        //Explain the difference between Future and Thread
        //Explain the difference between Future and Runnable

        Future<Book> futureMostPopularBook=bookLibrary.getMostPopularBookAsync();
        try{
            Book mostPopularBook=futureMostPopularBook.get();
            System.out.println("Most popular book: "+mostPopularBook.toString());
        }catch(InterruptedException | ExecutionException e){
            e.printStackTrace();
        }finally{
            bookLibrary.executorService.shutdown();
        }
        System.out.println("Program continues while waiting for the most popular book");
    }
    static class BookLibrary{
        private List<Book> books = new ArrayList<>();

        private Set<String> authors = new HashSet<>();

        private Map<String,Integer> bookCountByAuthor = new HashMap<>();

        private ExecutorService executorService = Executors.newSingleThreadExecutor();
        BookLibrary(){
            books=new ArrayList<>();
            authors=new HashSet<>();
            bookCountByAuthor=new HashMap<>();
            executorService=Executors.newSingleThreadExecutor();
        }
        public void addBook(Book book){
            books.add(book);
            authors.add(book.author);
            bookCountByAuthor.put(book.author,bookCountByAuthor.getOrDefault(book.author,0)+1);
        }
        public List<Book> getAllBooks(){
            return new ArrayList<>(books);
        }
        public Set<String> getAuthors(){
            return new HashSet<>(authors);
        }
        public Map<String,Integer> getBookCountByAuthor(){
            return new HashMap<>(bookCountByAuthor);
        }   
        //Asynchronous programming
        public Future<Book> getMostPopularBookAsync(){
        return executorService.submit(()->{
            Thread.sleep(2000);

            return books.isEmpty()?null:getMostPopularBook();
        });
    }
    private Book getMostPopularBook(){
        return books.get(0);
    }
    }
    static class Book implements Serializable{
        private String isbn;
        private String title;
        private String author;
        public Book(String isbn,String title,String author){
            this.isbn=isbn;
            this.title=title;
            this.author=author;
        }
        // trainsent feild, it would not be serialized
        transient int curentPage=0;
        @Override
        public String toString(){
            return "Book{"+"isbn='"+isbn+'\''+", title='"+title+'\''+", author='"+author+'\''+'}';
        }
    }
}




