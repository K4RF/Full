package com.book.manage.controller;

import com.book.manage.entity.*;
import com.book.manage.repository.member.MemberRepository;
import com.book.manage.service.book.BookService;
import com.book.manage.service.member.MemberService;
import com.book.manage.service.order.OrderService;
import com.book.manage.service.rental.RentalService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final RentalService rentalService;
    private final OrderService orderService;
    private final BookService bookService;

    // 관리자 회원가입
    @GetMapping("/add")
    public String addAdmin(@ModelAttribute("member") Member member,
                           @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 이미 로그인된 경우, 비정상적인 접근을 방지하고 홈으로 리디렉션
        if (loginMember != null) {
            return "redirect:/";  // 이미 로그인된 경우 홈으로 이동
        }
        return "members/addAdminForm";
    }

    // 관리자 저장
    @PostMapping("/add")
    public String saveAdmin(@Validated @ModelAttribute Member member, BindingResult bindingResult) {
        // 로그인 ID 검증
        if (!member.getLoginId().matches("^[a-zA-Z0-9]{1,16}$")) {
            bindingResult.rejectValue("loginId", "invalidFormat", "로그인 ID는 영어와 숫자로 1~16자 이내여야 합니다.");
        }

        // 비밀번호 검증
        if (!member.getPassword().matches("^[a-zA-Z0-9]{8,16}$")) {
            bindingResult.rejectValue("password", "invalidLength", "비밀번호는 8자에서 16자 사이여야 합니다.");
        }

        // 닉네임 검증
        if (!member.getNickname().matches("^[a-zA-Z가-힣0-9 ]+$")) {
            bindingResult.rejectValue("nickname", "invalidFormat", "닉네임에는 특수 문자를 사용할 수 없습니다.");
        } else if ("deletedUser".equalsIgnoreCase(member.getNickname())) {
            bindingResult.rejectValue("nickname", "inappropriateName", "부적절한 이름입니다.");
        }

        // 중복 로그인 ID 확인
        if (memberRepository.findByLoginId(member.getLoginId()).isPresent()) {
            bindingResult.rejectValue("loginId", "duplicate", "이미 존재하는 로그인 ID입니다.");
        }

        // 검증 오류가 있으면 회원 가입 폼으로 되돌림
        if (bindingResult.hasErrors()) {
            return "members/addAdminForm";
        }


        // 회원 등록
        try {
            member.setRole(Role.ADMIN);

            memberService.join(member);
        } catch (Exception e) {
            log.error("Error occurred during member registration", e);
            return "members/addAdminForm";
        }

        // 성공 시 홈으로 리다이렉트
        return "redirect:/";
    }
    // 관리자 홈
    @GetMapping
    public String adminHome(@SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        if (!isAdmin(loginMember)) {
            return "redirect:/"; // 권한 없는 사용자는 홈으로 리디렉트
        }
        return "redirect:/admin/members";
    }

    // 회원 관리 페이지
    @GetMapping("/members")
    public String manageMembers(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        if (!isAdmin(loginMember)) {
            return "redirect:/"; // 권한 없는 사용자는 홈으로 리디렉트
        }
        model.addAttribute("selectedCategory", "members"); // 선택된 카테고리

        List<Member> members = memberService.findAllMembers(); // 전체 회원 조회
        model.addAttribute("members", members);
        return "admin/adminHome";
    }

    // 회원 삭제
    @PostMapping("/members/{memberId}/delete")
    public String deleteMember(@PathVariable Long memberId,
                               @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(loginMember)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/";
        }

        try {
            memberService.deleteMember(memberId);
            redirectAttributes.addFlashAttribute("message", "회원 삭제가 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("Error deleting member", e);
            redirectAttributes.addFlashAttribute("error", "회원 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/members";
    }

    // 도서 관리 페이지
    @GetMapping("/books")
    public String manageBooks(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        if (!isAdmin(loginMember)) {
            return "redirect:/"; // 권한 없는 사용자는 홈으로 리디렉트
        }
        model.addAttribute("selectedCategory", "books"); // 선택된 카테고리

        // 도서 목록 조회
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);

        return "admin/manageBooks";
    }

    // 도서 삭제
    @PostMapping("/books/{bookId}/delete")
    public String deleteBook(@PathVariable Long bookId,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                             RedirectAttributes redirectAttributes) {
        if (!isAdmin(loginMember)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/";
        }

        try {
            bookService.deleteById(bookId);
            redirectAttributes.addFlashAttribute("message", "도서가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Error deleting book", e);
            redirectAttributes.addFlashAttribute("error", "도서 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/books";
    }
    @GetMapping("/rentals")
    public String manageRentals(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        if (!isAdmin(loginMember)) {
            return "redirect:/"; // 권한 없는 사용자는 홈으로 리디렉트
        }
        model.addAttribute("selectedCategory", "rentals"); // 선택된 카테고리
        List<Rental> rentals = rentalService.findAllRentals();
        model.addAttribute("rentals", rentals);
        return "admin/manageRentals";
    }

    // 대출 상태 변경
    @PostMapping("/rentals/{rentalId}/updateStatus")
    public String updateRentalStatus(@PathVariable Long rentalId,
                                     @RequestParam String rentalStatus,
                                     @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                                     RedirectAttributes redirectAttributes) {
        if (!isAdmin(loginMember)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/";
        }

        try {
            rentalService.updateAdminRentalStatus(rentalId, rentalStatus);
            redirectAttributes.addFlashAttribute("message", "대출 상태가 변경되었습니다.");
        } catch (Exception e) {
            log.error("Error updating rental status", e);
            redirectAttributes.addFlashAttribute("error", "대출 상태 변경 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/rentals";
    }

    // 대출 기록 삭제
    @PostMapping("/rentals/{rentalId}/delete")
    public String deleteRental(@PathVariable Long rentalId,
                               @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                               RedirectAttributes redirectAttributes) {
        if (!isAdmin(loginMember)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/";
        }

        try {
            rentalService.deleteRental(rentalId);
            redirectAttributes.addFlashAttribute("message", "대출 기록이 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Error deleting rental", e);
            redirectAttributes.addFlashAttribute("error", "대출 기록 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/rentals";
    }

    // 구매 관리 페이지
    @GetMapping("/orders")
    public String manageOrders(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        if (!isAdmin(loginMember)) {
            return "redirect:/"; // 권한 없는 사용자는 홈으로 리디렉트
        }
        model.addAttribute("selectedCategory", "orders"); // 선택된 카테고리

        // 주문 목록 조회
        List<Order> orders = orderService.findAllOrders();
        model.addAttribute("orders", orders);

        return "admin/manageOrders";
    }

    // 주문 취소 (삭제)
    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId,
                              @SessionAttribute(value = "loginMember", required = false) Member loginMember,
                              RedirectAttributes redirectAttributes) {
        if (!isAdmin(loginMember)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/";
        }

        try {
            orderService.cancelOrder(orderId);
            redirectAttributes.addFlashAttribute("message", "주문이 취소되었습니다.");
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            redirectAttributes.addFlashAttribute("error", "주문 취소 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/orders";
    }


    // 권한 확인 메서드
    private boolean isAdmin(Member loginMember) {
        return loginMember != null && loginMember.getRole() == Role.ADMIN;
    }
}