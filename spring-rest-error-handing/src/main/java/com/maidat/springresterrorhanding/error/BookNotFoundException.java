package com.maidat.springresterrorhanding.error;

public class BookNotFoundException extends RuntimeException{

    public BookNotFoundException(Long id){
        super("Book id not found : " + id);
    }
}
