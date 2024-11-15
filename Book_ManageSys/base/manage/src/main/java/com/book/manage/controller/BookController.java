package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.service.book.BookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping("/{bookId}")
    public String book(@PathVariable long bookId, Model model) {
        Book book = bookService.findById(bookId).orElseThrow();
        model.addAttribute("book", book);
        return "/book/bookInfo";
    }

    @GetMapping("/add")
    public String addBookReq(Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트, 원래 URL을 함께 전달
        if (loginMember == null) {
            return "redirect:/login";
        }

        Book book = new Book();
        model.addAttribute("book", book);
        return "book/addBookForm";
    }

    @PostMapping("/add")
    public String addBookRes(@ModelAttribute @Validated Book book, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        Book savedBook = bookService.save(book);
        redirectAttributes.addAttribute("bookId", savedBook.getBookId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/books/{bookId}";
    }
}
