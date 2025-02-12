package com.book.manage.controller;

import com.book.manage.entity.*;
import com.book.manage.entity.dto.BookEditDto;
import com.book.manage.entity.dto.BookSearchDto;
import com.book.manage.service.book.BookService;
import com.book.manage.service.comment.CommentService;
import com.book.manage.service.order.OrderService;
import com.book.manage.service.rental.RentalService;
import com.book.manage.service.category.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private final CommentService commentService;

    private final OrderService orderService;

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
    public String books(
            @ModelAttribute("bookSearch") BookSearchDto bookSearch,
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {

        // 책 검색 결과를 조회하고 모델에 추가
        List<Book> books = bookService.findBooks(bookSearch);
        model.addAttribute("books", books);
        model.addAttribute("selectedCategory", bookSearch.getCategory()); // 선택된 카테고리 추가
        model.addAttribute("cacheBuster", System.currentTimeMillis()); // 캐시 방지용 무작위 값
        // 로그인 멤버 정보를 모델에 추가 (null일 수도 있음)
        model.addAttribute("loginMember", loginMember);

        return "book/bookList";
    }

    @GetMapping("/{bookId}")
    public String book(
            @PathVariable long bookId,
            Model model,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        bookService.incrementViewCount(bookId); // 조회수 증가

        // 도서 정보 가져오기
        Book book = bookService.findById(bookId).orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // 대출 상태 및 대출 기록 가져오기
        String rentalStatus = rentalService.getRentalStatusByBookId(bookId);
        Rental rental = rentalService.findActiveRentalByBookId(bookId);

        // 대출 연장 횟수 가져오기
        int extensionCount = (rental != null) ? rentalService.getExtensionCount(rental.getRentalId()) : 0;

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

        // 댓글 불러오기
        List<Comment> comments = commentService.getCommentsByBookId(bookId);

        // 댓글 작성 여부 확인
        boolean hasComment = false;
        if (loginMember != null) {
            hasComment = commentService.hasUserCommented(bookId, loginMember);
        }

        model.addAttribute("hasComment", hasComment);

        // 모델에 데이터 추가
        model.addAttribute("book", book);
        model.addAttribute("rentalStatus", rentalStatus);
        model.addAttribute("rentalId", rental != null ? rental.getRentalId() : null);
        model.addAttribute("rentalAbleBook", book.getRentalAbleBook()); // rentalAbleBook 값을 모델에 추가
        model.addAttribute("rentalMemberId", rentalMemberId);
        model.addAttribute("extensionCount", extensionCount); // 대출 연장 횟수 추가
        model.addAttribute("loginMemberId", loginMemberId); // 로그인된 사용자 ID 추가
        model.addAttribute("cacheBuster", System.currentTimeMillis()); // 캐시 방지용 무작위 값
        model.addAttribute("sortedCategories", sortedCategories);
        model.addAttribute("comments", comments);

        return "book/bookInfo";
    }


    @GetMapping("/add")
    public String addBookReq(Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }
        if (loginMember.getRole() != Role.ADMIN) {
            return "book/returnBook";
        }

        Book book = new Book();
        model.addAttribute("book", book);
        return "book/addBookForm";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute("book") @Valid Book book,
                          @RequestParam("categoriesFormatted") String categoriesFormatted,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          Model model, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                          HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "book/addBookForm";
        }

        book.setRentalAbleBook(true); // 기본값 설정

        // 카테고리 입력값 처리
        Set<String> categories = new HashSet<>();
        for (String categoryName : categoriesFormatted.split(",")) {
            categories.add(categoryName.trim());
        }

        // 이미지 업로드 처리
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/images";
            try {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                imageFile.transferTo(uploadPath.resolve(fileName));
                book.setImagePath("/uploads/images/" + fileName + "?v=" + UUID.randomUUID());
            } catch (IOException e) {
                log.error("Error occurred while uploading image: {}", e.getMessage());
                model.addAttribute("error", "이미지 업로드에 실패했습니다.");
                return "book/addBookForm";
            }
        }

        // 도서 저장
        try {
            Book savedBook = bookService.save(book, categories);
            String rentalStatus = rentalService.getRentalStatusByBookId(savedBook.getBookId());
            redirectAttributes.addAttribute("bookId", savedBook.getBookId());
            redirectAttributes.addAttribute("status", true);
            redirectAttributes.addAttribute("rentalStatus", rentalStatus);
            model.addAttribute("message", "도서 등록이 완료되었습니다.");
            return "redirect:/bookList/{bookId}";
        } catch (Exception e) {
            log.error("Error occurred while saving book: {}", e.getMessage());
            model.addAttribute("error", "도서 등록에 실패했습니다.");
            return "book/addBookForm";
        }
    }

    @GetMapping("/{bookId}/edit")
    public String editBookReq(@PathVariable Long bookId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }
        if(loginMember.getRole() != Role.ADMIN) {
            return "book/returnBook";
        }

        Book book = bookService.findById(bookId).orElseThrow();
        BookEditDto bookEditDto = new BookEditDto(book.getBookId(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDetails(), book.getCategories(), book.getImagePath(), book.getPublishDate(), book.getPrice());
        model.addAttribute("book", bookEditDto);
        return "book/editBookForm";
    }

    @PostMapping("/{bookId}/edit")
    public String editBookRes(@PathVariable Long bookId,
                              @Validated @ModelAttribute("book") BookEditDto bookEditDto,
                              @RequestParam("categoryFormatted") String categoryFormatted,
                              @RequestParam("imageFile") MultipartFile imageFile, // 이미지 파일 파라미터 추가
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
            return "book/returnBook";
        }

        if (bindingResult.hasErrors()) {
            return "book/editBookForm";
        }

        // 기존 도서 데이터 가져오기
        Book existingBook = bookService.findById(bookId).orElseThrow();

        // 카테고리 입력값을 쉼표로 구분하여 Set<String>으로 변환
        Set<String> categories = new HashSet<>();
        String[] categoryNames = categoryFormatted.split(",");
        for (String categoryName : categoryNames) {
            categories.add(categoryName.trim());  // 각 카테고리명을 Set에 추가
        }

        // 이미지 업로드 처리
        if (!imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/images"; // static 폴더 내에 저장
            try {
                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                imageFile.transferTo(uploadPath.resolve(fileName));
                bookEditDto.setImagePath("/uploads/images/" + fileName + "?v=" + UUID.randomUUID()); // 캐시 무효화 쿼리 추가
            } catch (IOException e) {
                log.error("Error occurred while uploading image: {}", e.getMessage());
                model.addAttribute("error", "이미지 업로드에 실패했습니다.");
                return "book/editBookForm";
            }
        } else {
            // 이미지 파일이 없으면 기존 경로 유지
            bookEditDto.setImagePath(existingBook.getImagePath());
        }

        // 발행일자가 입력되지 않았을 경우 기존 값 유지
        if (bookEditDto.getPublishDate() == null) {
            bookEditDto.setPublishDate(existingBook.getPublishDate());
        }

        // 카테고리 중복 검증
        List<String> categoryList = categories.stream().collect(Collectors.toList());
        if (categoryService.hasDuplicateCates(categoryList)) {
            bindingResult.rejectValue("categoryFormatted", "duplicateCates", "중복된 카테고리가 있습니다.");
        }

        if (bindingResult.hasErrors()) {
            return "book/editBookForm";
        }

        // Book 객체 업데이트
        existingBook.setTitle(bookEditDto.getTitle());
        existingBook.setAuthor(bookEditDto.getAuthor());
        existingBook.setPublisher(bookEditDto.getPublisher());
        existingBook.setDetails(bookEditDto.getDetails());
        existingBook.setImagePath(bookEditDto.getImagePath());
        existingBook.setPublishDate(bookEditDto.getPublishDate());
        existingBook.setPrice(bookEditDto.getPrice());

        // 카테고리 처리
        bookService.edit(bookId, bookEditDto);

        redirectAttributes.addFlashAttribute("message", "도서가 성공적으로 수정되었습니다.");
        return "redirect:/bookList/{bookId}";
    }

    // 도서 삭제에 대한 Get 방식 처리
    @GetMapping("/{bookId}/delete")
    public String deleteBookGet(@PathVariable Long bookId, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        // 관리자 권한 확인
        if (loginMember.getRole() != Role.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "삭제 권한이 없습니다.");
            return "redirect:/bookList/" + bookId;
        }

        return "redirect:/bookList/" + bookId; // 삭제는 POST 요청으로만 허용
    }
    // 도서 삭제
    @PostMapping("/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId,
                             RedirectAttributes redirectAttributes,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인 체크 및 리디렉션 처리
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        // 관리자 권한 체크
        if (loginMember.getRole() != Role.ADMIN) {
            return "book/returnBook";
        }

        // 도서 정보 가져오기
        Optional<Book> bookOptional = bookService.findById(bookId);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();

            // 이미지 파일 삭제
            String imagePath = book.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                String fileSystemPath = "src/main/resources/static" + imagePath;
                File imageFile = new File(fileSystemPath);
                if (imageFile.exists() && imageFile.isFile()) {
                    if (!imageFile.delete()) {
                        log.warn("이미지 파일 삭제 실패: {}", fileSystemPath);
                    }
                } else {
                    log.warn("이미지 파일이 존재하지 않거나 파일이 아님: {}", fileSystemPath);
                }
            }

            // 관련 대출 데이터 삭제
            rentalService.deleteRentalsByBookId(bookId);

            // 도서 데이터 삭제
            bookService.deleteById(bookId);

            redirectAttributes.addFlashAttribute("message", "도서와 관련된 대출 데이터 및 이미지 파일이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("error", "삭제할 도서를 찾을 수 없습니다.");
        }

        return "redirect:/bookList";
    }


    // 도서 대여에 대한 Get 방식 처리
    @GetMapping("/{bookId}/rental")
    public String rentBookGet(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        redirectAttributes.addFlashAttribute("error", "대출은 POST 요청으로만 가능합니다.");
        return "redirect:/bookList/" + bookId;
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
            redirectAttributes.addFlashAttribute("error", "이미 대출된 도서입니다.");
        }
        return "redirect:/bookList/{bookId}";
    }

    // 도서 반납에 대한 Get 방식 처리
    @GetMapping("/{bookId}/return")
    public String returnBookGet(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        redirectAttributes.addFlashAttribute("error", "반납은 POST 요청으로만 가능합니다.");
        return "redirect:/bookList/" + bookId;
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
            redirectAttributes.addFlashAttribute("error", "반납할 대출 기록이 없습니다.");
        }
        return "redirect:/bookList/{bookId}";
    }

    // 도서 대출 연장에 대한 GET 방식 처리
    @GetMapping("/{bookId}/extend")
    public String extendRentalGet(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        redirectAttributes.addFlashAttribute("error", "대출 연장은 POST 요청으로만 가능합니다.");
        return "redirect:/bookList/" + bookId;
    }

    // 도서 대출 연장 처리
    @PostMapping("/{bookId}/extend")
    public String extendRental(@PathVariable Long bookId, @SessionAttribute(value = "loginMember", required = false) Member loginMember, RedirectAttributes redirectAttributes) {
        String redirect = handleLoginRedirect(loginMember, bookId);
        if (redirect != null) {
            return redirect;
        }

        try {
            boolean success = rentalService.extendRental(bookId, loginMember.getMemberId());
            if (success) {
                redirectAttributes.addFlashAttribute("status", "연장 성공");
                redirectAttributes.addFlashAttribute("message", "대출 기간이 성공적으로 연장되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("status", "연장 실패");
                redirectAttributes.addFlashAttribute("error", "연장 가능 횟수를 초과했습니다.");
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("status", "연장 실패");
            redirectAttributes.addFlashAttribute("error", "연장 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/bookList/" + bookId;
    }

    // 바로 구매 처리
    @GetMapping("/{bookId}/checkout")
    public String checkoutImmediately(@PathVariable Long bookId, HttpSession session, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }
        // 장바구니에 해당 도서 추가
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // 세션에서 장바구니 가져오기
        List<Cart> cart = (List<Cart>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // 장바구니에 도서가 없는 경우 추가
        Optional<Cart> existingItem = cart.stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1); // 기존 수량에 1 추가
        } else {
            Cart newItem = new Cart();
            newItem.setBookId(bookId);
            newItem.setTitle(book.getTitle());
            newItem.setPrice(book.getPrice());
            newItem.setQuantity(1);
            cart.add(newItem);
        }

        // 장바구니 정보 및 총 합계 전달
        int totalPrice = cart.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);

        return "order/orderConfirm"; // 장바구니 확인 페이지로 리다이렉트
    }
}
