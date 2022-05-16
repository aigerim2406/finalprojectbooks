package com.example.books.controllers;

import com.example.books.model.Author;
import com.example.books.model.Book;
import com.example.books.model.Users;
import com.example.books.repository.AuthorRepository;
import com.example.books.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping(value="/")
    public String indexPage(Model model){
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("authors", authors);
        List<Book> books = bookRepository.findAll();
        model.addAttribute("books", books);
        model.addAttribute("currentUser", getCurrentUser());
        return "index";
    }

    @GetMapping(value="/login")
    public String login(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "login";
    }

    @GetMapping(value="/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "profile";
    }

    @GetMapping(value="/adminpanel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public String admin(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "adminpanel";
    }

    @GetMapping(value="/moderatorpanel")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR')")
    public String moderator(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "moderatorpanel";
    }

    @GetMapping(value="/403")
    public String accessDeniedPage(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "403";
    }
    @GetMapping(value="/404")
    public String nonexistingPage(Model model){
        model.addAttribute("currentUser", getCurrentUser());
        return "404";
    }

    @GetMapping(value="/authors")
    public String AuthorsPage(Model model){
        List<Author> authors=authorRepository.findAll();
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("authors", authors);
        return "authors";
    }

    @PostMapping(value="addbook")
    public String addbook(@RequestParam(name="name") String name,
                          @RequestParam(name="genre") String genre,
                          @RequestParam(name="price") double price,
                          @RequestParam(name="author_id") Long authorId,
                          Model model){
        Author author = authorRepository.findById(authorId).orElse(null);
        model.addAttribute("currentUser", getCurrentUser());
        if(author!=null){
            Book book = new Book();
            book.setName(name);
            book.setGenre(genre);
            book.setPrice(price);
            book.setAuthor(author);
            bookRepository.save(book);
        }
        return "redirect:/";
    }

    @PostMapping(value="addauthor")
    public String addauthor(@RequestParam(name="fullname") String fullname,
                            Model model){
        Author author=new Author();
        author.setFullname(fullname);
        model.addAttribute("currentUser", getCurrentUser());
        authorRepository.save(author);
        return "redirect:/authors";
    }

    @GetMapping(value="/details/{id}")
    public String bookDetails(@PathVariable(name="id") Long id, Model model){
        List<Author> authors = authorRepository.findAll();
        model.addAttribute("authors", authors);
        Book book = bookRepository.findById(id).orElse(null);
        model.addAttribute("book", book);
        model.addAttribute("currentUser", getCurrentUser());
        return "details";
    }

    @GetMapping(value="/author_details/{id}")
    public String authorDetails(@PathVariable(name="id") Long id, Model model){
        Author author= authorRepository.findById(id).orElse(null);
        model.addAttribute("author", author);
        model.addAttribute("currentUser",getCurrentUser());
        return "author_details";
    }

    @PostMapping(value="/savebook")
    public String savebook(@RequestParam(name="id") Long id,
                           @RequestParam(name="name") String name,
                           @RequestParam(name="genre") String genre,
                           @RequestParam(name="price") double price,
                           @RequestParam(name="author_id") Long authorId,
                           Model model){
        Book book = bookRepository.findById(id).orElse(null);
        Author author = authorRepository.findById(authorId).orElse(null);
        model.addAttribute("currentUser", getCurrentUser());
        if(author!=null && book!=null){
            book.setName(name);
            book.setGenre(genre);
            book.setPrice(price);
            book.setAuthor(author);
            bookRepository.save(book);
            return "redirect:/";
        }else{
            return "redirect:/404";
        }
    }

    @PostMapping(value="/saveauthor")
    public String saveauthor(@RequestParam(name="id") Long id,
                             @RequestParam(name="fullname") String fullname,
                             Model model){
        Author author=authorRepository.findById(id).orElse(null);
        model.addAttribute("currentUser", getCurrentUser());
        if(author!=null){
            author.setFullname(fullname);
            authorRepository.save(author);
            return "redirect:/authors";
        }else{
            return "redirect:/404";
        }
    }

    @PostMapping(value="deletebook")
    public String deletebook(@RequestParam(name="id") Long id,
                             Model model){
        Book book = bookRepository.findById(id).orElse(null);
        model.addAttribute("currentUser", getCurrentUser());
        if(book!=null){
            bookRepository.delete(book);
        }else{
            return "redirect:/404";
        }
        return "redirect:/";
    }

    @PostMapping(value="/deleteauthor")
    public String deleteauthor(@RequestParam(name="id") Long id,
                               Model model){
        Author author = authorRepository.findById(id).orElse(null);
        model.addAttribute("currentUser", getCurrentUser());
        if(author!=null){
            authorRepository.delete(author);
        }else{
            return "redirect:/404";
        }

        return "redirect:/authors";
    }

    private Users getCurrentUser(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users currentUser=(Users) authentication.getPrincipal();
            return currentUser;
        }
        return null;
    }
}
