package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
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
    public String book(
            @PathVariable long bookId,
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 도서 정보 가져오기
        Book book = bookService.findById(bookId).orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // 대출 상태 및 대출 기록 가져오기
        String rentalStatus = rentalService.getRentalStatusByBookId(bookId);
        Rental rental = rentalService.findActiveRentalByBookId(bookId);

        // 로그인된 사용자의 memberId 가져오기
        Long loginMemberId = (loginMember != null) ? loginMember.getMemberId() : null;

        // 대출 기록의 memberId 확인
        Long rentalMemberId = (rental != null) ? rental.getMember().getMemberId() : null;

        // 모델에 데이터 추가
        model.addAttribute("book", book);
        model.addAttribute("rentalStatus", rentalStatus);
        model.addAttribute("rentalId", (rental != null) ? rental.getRentalId() : null);
        model.addAttribute("rentalMemberId", rentalMemberId);
        model.addAttribute("loginMemberId", loginMemberId); // 로그인된 사용자 ID 추가

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
        // 로그인 상태 확인
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        // 책 저장
        Book savedBook = bookService.save(book);

        // 책 ID로 렌탈 상태 가져오기 (렌탈 서비스에서 상태 조회)
        String rentalStatus = rentalService.getRentalStatusByBookId(savedBook.getBookId()); // bookId를 기반으로 렌탈 상태를 확인

        // 리다이렉트할 때 책 ID와 렌탈 상태를 파라미터로 전달
        redirectAttributes.addAttribute("bookId", savedBook.getBookId());
        redirectAttributes.addAttribute("status", true);
        redirectAttributes.addAttribute("rentalStatus", rentalStatus); // 렌탈 상태 추가

        return "redirect:/bookList/{bookId}";
    }

    @GetMapping("/{bookId}/edit")
    public String editBookReq(@PathVariable Long bookId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
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

    // 도서 삭제 요청 (GET 요청 처리 추가)
    @GetMapping("/{bookId}/delete")
    public String deleteBookGet(@PathVariable Long bookId,
                                @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                                HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        log.warn("잘못된 접근: GET 요청은 허용되지 않습니다. POST 요청을 사용하세요.");
        return "redirect:/bookList/" + bookId;
    }

    @PostMapping("/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        // 해당 도서에 대한 대출 기록 삭제
        rentalService.deleteRentalsByBookId(bookId);

        // 도서 삭제
        bookService.deleteById(bookId);

        redirectAttributes.addFlashAttribute("message", "도서와 관련된 대출 데이터가 성공적으로 삭제되었습니다");
        return "redirect:/bookList";
    }

    // 도서 대출 요청 (GET 요청 처리 추가)
    @GetMapping("/{bookId}/rental")
    public String rentBookGet(@PathVariable Long bookId,
                              @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                              HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        log.warn("잘못된 접근: GET 요청은 허용되지 않습니다. POST 요청을 사용하세요.");
        return "redirect:/bookList/" + bookId;
    }
    // 도서 대출 요청
    @PostMapping("/{bookId}/rental")
    public String rentBook(@PathVariable Long bookId,
                           @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                           RedirectAttributes redirectAttributes) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        try {
            // 대출 로직 수행
            rentalService.createRental(bookId, loginMember.getMemberId());
            redirectAttributes.addFlashAttribute("status", "대출 성공!");
            redirectAttributes.addFlashAttribute("message", "도서를 성공적으로 대출했습니다.");
        } catch (IllegalStateException e) {
            // 이미 대출 중인 경우
            redirectAttributes.addFlashAttribute("error", "이미 대출 중입니다.");
        } catch (Exception e) {
            // 기타 에러 처리
            redirectAttributes.addFlashAttribute("error", "도서 대출에 실패했습니다. 사유: " + e.getMessage());
        }

        return "redirect:/bookList/" + bookId;
    }

    // 도서 반납 요청 (GET 요청 처리 추가)
    @GetMapping("/{bookId}/return")
    public String returnBookGet(@PathVariable Long bookId,
                                @RequestParam(required = false) Long rentalId,
                                @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                                HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        log.warn("잘못된 접근: GET 요청은 허용되지 않습니다. POST 요청을 사용하세요.");
        return "redirect:/bookList/" + bookId;
    }

    @PostMapping("/{bookId}/return")
    public String returnBook(@PathVariable Long bookId,
                             @RequestParam Long rentalId,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = "/bookList/" + bookId;
            return "redirect:/login?redirectURL=" + redirectUrl;
        }

        try {
            // 대출 기록 조회
            Rental rental = rentalService.findActiveRentalByBookId(bookId);

            // 대출 기록이 존재하지 않거나 대출자가 로그인한 사용자가 아닌 경우 처리
            if (rental == null || !rental.getMember().getMemberId().equals(loginMember.getMemberId())) {
                redirectAttributes.addFlashAttribute("error", "본인이 대출한 도서만 반납할 수 있습니다.");
                return "redirect:/bookList/" + bookId;
            }

            // 반납 처리
            rentalService.returnBook(rentalId, bookId);
            redirectAttributes.addFlashAttribute("status", "반납 성공!");
            redirectAttributes.addFlashAttribute("message", "도서 반납이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("Return error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "도서 반납에 실패했습니다. 사유: " + e.getMessage());
        }

        return "redirect:/bookList/" + bookId;
    }


}
