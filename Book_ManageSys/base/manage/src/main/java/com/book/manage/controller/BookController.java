package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
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
@RequestMapping("/bookList")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String books(){
        return "/book/bookList";
    }

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
        return "redirect:/bookList/{bookId}";
    }

    @GetMapping("/{bookId}/edit")
    public String editBookReq(@PathVariable Long bookId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트하며, 원래 URL을 함께 전달
        if (loginMember == null) {
            return "redirect:/login";
        }
        Book book = bookService.findById(bookId).orElseThrow();

        // 로그인한 사용자가 도서 편집 권한 있는지 검증 로직 추가 필요
        BookEditDto bookEditDto = new BookEditDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDetails());
        model.addAttribute("book", bookEditDto);

        return "book/editBookForm";
    }

    @PostMapping("/{bookId}/edit")
    public String editBookRes(@PathVariable Long bookId, @Validated @ModelAttribute("book") BookEditDto bookEditDto,Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인 검증
        if(loginMember == null) {
            return "redirect:/login";
        }

        Book book = bookService.findById(bookId).orElseThrow();
        // 검증에 실패한 경우 다시 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("book", bookEditDto);
            return "book/editBookForm";
        }
        bookService.edit(bookId, bookEditDto);
        redirectAttributes.addFlashAttribute("message", "도서가 성공적으로 수정되었습니다");
        return "redirect:/bookList/" + bookId;
    }


    @PostMapping("/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if(loginMember == null) {
            return "redirect:/login";
        }
        Book book = bookService.findById(bookId).orElseThrow();
        // 추후에 사용자가 삭제 권한이 있는지 검증 로직 추가
        bookService.deleteById(bookId);

        redirectAttributes.addFlashAttribute("message", "도서가 성공적으로 삭제되었습니다");
        return "redirect:/bookList";
    }
}
