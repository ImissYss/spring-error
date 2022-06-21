package com.maidat.springresterrorhanding;

import com.maidat.springresterrorhanding.error.BookNotFoundException;
import com.maidat.springresterrorhanding.error.BookUnSupportedFieldPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@Validated
public class BookController {

    @Autowired
    private final BookRepository bookRepository;

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // find
    @GetMapping("/books")
    List<Book> findAll(){
        return bookRepository.findAll();
    }

    // Save
    @PostMapping("/books")
    Book newBook(@Valid @RequestBody Book newBook){
        return bookRepository.save(newBook);
    }

    @GetMapping("/books/{id}")
    Book findOne(@PathVariable @Min(1) Long id){
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    // save or update
    @PutMapping("/books/{id}")
    Book saveOrUpdate(@RequestBody Book newBook,@PathVariable Long id){
        return bookRepository.findById(id)
                .map(x -> {
                    x.setName(newBook.getName());
                    x.setAuthor(newBook.getAuthor());
                    x.setPrice(newBook.getPrice());
                    return bookRepository.save(x);
                })
                .orElseGet(() -> {
                    newBook.setId(id);
                    return bookRepository.save(newBook);
                });
    }

    @PatchMapping("/books/{id}")
    Book patch(@RequestBody Map<String, String> update, @PathVariable Long id){
        return bookRepository.findById(id)
                .map(x -> {
                    if (!StringUtils.isEmpty("author")){
                        x.setAuthor(update.get("author"));
                        return bookRepository.save(x);
                    }else{
                        throw new BookUnSupportedFieldPatchException(update.keySet());
                    }
                })
                .orElseGet(() -> {
                    throw new BookNotFoundException(id);
                });
    }

}
