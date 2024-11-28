package com.book.manage.controller;

import com.book.manage.entity.*;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.repository.book.category.CategoryRepository;
import com.book.manage.service.book.BookService;
import com.book.manage.service.book.RentalService;
import com.book.manage.service.book.category.CategoryService;
import com.book.manage.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/bookList")
public class BookController {
    private final BookService bookService;
    private final RentalService rentalService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    // 모든 요청에서 loginMember 모델을 추가
    @ModelAttribute
    public void addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);
    }

    private String handleLoginRedirect(Member loginMember, Long bookId) {
        if (loginMember == null) {
            String redirectUrl = "/bookList";
            if (bookId != null) {
                redirectUrl = "/bookList/" + bookId;
            }
            return "redirect:/login?redirectURL=" + redirectUrl;
        }
        return null;
    }

    @GetMapping
    public String books(@ModelAttribute("bookSearch") BookSearchDto bookSearch, Model model) {
        List<Book> books = bookService.findBooks(bookSearch);
        model.addAttribute("books", books);
        model.addAttribute("selectedCategory", bookSearch.getCategory());  // 선택된 카테고리 추가
        return "book/bookList";
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
        // 카테고리 순서대로 정렬
        List<Category> sortedCategories = book.getCategories().stream()
                .sorted(Comparator.comparingInt(Category::getCateOrder))  // cateOrder 기준으로 정렬
                .collect(Collectors.toList());

        // 로그인된 사용자의 memberId 가져오기
        Long loginMemberId = (loginMember != null) ? loginMember.getMemberId() : null;

        // 대출 기록의 memberId 확인
        Long rentalMemberId = (rental != null) ? rental.getMember().getMemberId() : null;


        // 대출 가능 여부 계산 (대출중일 경우 대출 불가로 설정)
        if ("대출중".equals(rentalStatus)) {
            book.setRentalAbleBook(false); // 대출중일 경우 대출 불가
        } else if ("대출 가능".equals(rentalStatus)) {
            book.setRentalAbleBook(true); // 대출 가능일 경우 대출 가능
        }

        // 모델에 데이터 추가
        model.addAttribute("book", book);
        model.addAttribute("rentalStatus", rentalStatus);
        model.addAttribute("rentalId", rental != null ? rental.getRentalId() : null);
        model.addAttribute("rentalAbleBook", book.getRentalAbleBook()); // rentalAbleBook 값을 모델에 추가
        model.addAttribute("rentalMemberId", rentalMemberId);
        model.addAttribute("loginMemberId", loginMemberId); // 로그인된 사용자 ID 추가
        model.addAttribute("sortedCategories", sortedCategories);

        return "/book/bookInfo";
    }


    @GetMapping("/add")
    public String addBookReq(Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }
        if(loginMember.getRole() != Role.ADMIN) {
            return "/book/returnBook";
        }

        Book book = new Book();
        model.addAttribute("book", book);
        return "book/addBookForm";
    }

    // 도서 등록 처리
    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") @Valid Book book,
                          @RequestParam("categoriesFormatted") String categoriesFormatted,
                          Model model, BindingResult bindingResult, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request)  {

        // 도서 대출 가능 상태: 기본값
        book.setRentalAbleBook(true); // 기본값을 true로 설정

        // 카테고리 입력값을 쉼표로 구분하여 Set<String>으로 변환
        Set<String> categories = new HashSet<>();
        String[] categoryNames = categoriesFormatted.split(",");

        for (String categoryName : categoryNames) {
            categories.add(categoryName.trim());  // 각 카테고리명을 Set에 추가
        }

        // 도서 저장 및 카테고리 처리
        try {
            Book savedBook = bookService.save(book, categories);
            // 책 ID로 렌탈 상태 가져오기 (렌탈 서비스에서 상태 조회)
            String rentalStatus = rentalService.getRentalStatusByBookId(savedBook.getBookId()); // bookId를 기반으로 렌탈 상태를 확인
            redirectAttributes.addAttribute("bookId", savedBook.getBookId());
            redirectAttributes.addAttribute("status", true);
            redirectAttributes.addAttribute("rentalStatus", rentalStatus); // 렌탈 상태 추가
            model.addAttribute("message", "도서 등록이 완료되었습니다.");
            return "redirect:/bookList/{bookId}";
        } catch (Exception e) {
            log.error("Error occurred while saving book: {}", e.getMessage());
            model.addAttribute("error", "도서 등록에 실패했습니다.");
            return "book/addBookForm"; // 도서 등록 폼 페이지로 다시 돌아가기
        }
    }


    @GetMapping("/{bookId}/edit")
    public String editBookReq(@PathVariable Long bookId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }
        if(loginMember.getRole() != Role.ADMIN) {
            return "/book/returnBook";
        }

        Book book = bookService.findById(bookId).orElseThrow();
        BookEditDto bookEditDto = new BookEditDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDetails(), book.getCategories());
        model.addAttribute("book", bookEditDto);
        return "book/editBookForm";
    }

    @PostMapping("/{bookId}/edit")
    public String editBookRes(@PathVariable Long bookId,
                              @Validated @ModelAttribute("book") BookEditDto bookEditDto,
                              @RequestParam("categoryFormatted") String categoryFormatted,
                              Model model,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인 체크 및 리디렉션 처리
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        if(loginMember.getRole() != Role.ADMIN) {
            return "/book/returnBook";
        }

        if (bindingResult.hasErrors()) {
            return "book/editBookForm";
        }

        // 카테고리 입력값을 쉼표로 구분하여 Set<String>으로 변환
        Set<String> categories = new HashSet<>();
        String[] categoryNames = categoryFormatted.split(",");
        for (String categoryName : categoryNames) {
            categories.add(categoryName.trim());  // 각 카테고리명을 Set에 추가
        }

        // 카테고리 중복 검증
        List<String> categoryList = bookEditDto.getCategories().stream()
                .map(Category::getCate)
                .collect(Collectors.toList());
        if (categoryService.hasDuplicateCates(categoryList)) {
            bindingResult.rejectValue("categoryFormatted", "duplicateCates", "중복된 카테고리가 있습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "book/editBookForm";
        }

        // Book 객체로 변환하여 저장
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(bookEditDto.getTitle());
        book.setAuthor(bookEditDto.getAuthor());
        book.setPublisher(bookEditDto.getPublisher());
        book.setDetails(bookEditDto.getDetails());

        // 카테고리 처리
        bookService.edit(bookId, bookEditDto);

        redirectAttributes.addFlashAttribute("message", "도서가 성공적으로 수정되었습니다.");
        return "redirect:/bookList/{bookId}";
    }

    @PostMapping("/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }
        if(loginMember.getRole() != Role.ADMIN) {
            return "/book/returnBook";
        }

        rentalService.deleteRentalsByBookId(bookId);
        bookService.deleteById(bookId);
        redirectAttributes.addFlashAttribute("message", "도서와 관련된 대출 데이터가 성공적으로 삭제되었습니다.");
        return "redirect:/bookList";
    }

    @PostMapping("/{bookId}/rental")
    public String rentBook(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        try {
            rentalService.createRental(bookId, loginMember.getMemberId());
            redirectAttributes.addFlashAttribute("status", "대출 성공!");
            redirectAttributes.addFlashAttribute("message", "도서를 성공적으로 대출했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("status", "대출 실패");
            redirectAttributes.addFlashAttribute("message", "이미 대출된 도서입니다.");
        }
        return "redirect:/bookList/{bookId}";
    }

    @PostMapping("/{bookId}/return")
    public String returnBook(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        try {
            rentalService.returnBook(bookId, loginMember.getMemberId());
            redirectAttributes.addFlashAttribute("status", "반납 성공");
            redirectAttributes.addFlashAttribute("message", "도서를 성공적으로 반납했습니다.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("status", "반납 실패");
            redirectAttributes.addFlashAttribute("message", "반납할 대출 기록이 없습니다.");
        }
        return "redirect:/bookList/{bookId}";
    }
}
