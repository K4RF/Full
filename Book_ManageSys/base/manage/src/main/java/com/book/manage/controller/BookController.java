package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.service.book.BookService;
import com.book.manage.service.book.RentalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/bookList")
public class BookController {
    private final BookService bookService;
    private final RentalService rentalService;

    // 모든 요청에서 loginMember 모델을 추가
    @ModelAttribute
    public void addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);
    }

    @GetMapping
    public String books(@ModelAttribute("bookSearch") BookSearchDto bookSearch, Model model) {
        List<Book> books = bookService.findBooks(bookSearch);
        model.addAttribute("books", books);
        return "/book/bookList";
    }

    @GetMapping("/{bookId}")
    public String book(@PathVariable long bookId, Model model) {
        Book book = bookService.findById(bookId).orElseThrow();
        String rentalStatus = rentalService.getRentalStatusByBookId(bookId); // 대출 상태 가져오기

        model.addAttribute("book", book);
        model.addAttribute("rentalStatus", rentalStatus); // 대출 상태 추가
        return "/book/bookInfo";
    }

    @GetMapping("/add")
    public String addBookReq(Model model,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                             HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        Book book = new Book();
        model.addAttribute("book", book);
        return "book/addBookForm";
    }

    @PostMapping("/add")
    public String addBookRes(@ModelAttribute @Validated Book book, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        Book savedBook = bookService.save(book);
        redirectAttributes.addAttribute("bookId", savedBook.getBookId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/bookList/{bookId}";
    }

    @GetMapping("/{bookId}/edit")
    public String editBookReq(@PathVariable Long bookId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        Book book = bookService.findById(bookId).orElseThrow();

        // 로그인한 사용자가 도서 편집 권한 있는지 검증 로직 추가 필요
        BookEditDto bookEditDto = new BookEditDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDetails());
        model.addAttribute("book", bookEditDto);

        return "book/editBookForm";
    }

    @PostMapping("/{bookId}/edit")
    public String editBookRes(@PathVariable Long bookId, @Validated @ModelAttribute("book") BookEditDto bookEditDto, Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

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
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        bookService.deleteById(bookId);

        redirectAttributes.addFlashAttribute("message", "도서가 성공적으로 삭제되었습니다");
        return "redirect:/bookList";
    }

    // 도서 대출 요청
    @GetMapping("/{bookId}/rental")
    public String rental(@PathVariable Long bookId,
                         @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        try {
            rentalService.createRental(bookId, loginMember.getMemberId());
            redirectAttributes.addFlashAttribute("message", "도서 대출이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("Rental error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "도서 대출에 실패했습니다. 사유: " + e.getMessage());
        }

        return "redirect:/bookList/" + bookId;
    }
}
