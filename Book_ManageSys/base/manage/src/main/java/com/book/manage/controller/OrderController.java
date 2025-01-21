package com.book.manage.controller;

import com.book.manage.entity.Member;
import com.book.manage.entity.Order;
import com.book.manage.entity.Rental;
import com.book.manage.entity.dto.OrderSearchDto;
import com.book.manage.entity.dto.RentalSearchDto;
import com.book.manage.login.session.SessionConst;
import com.book.manage.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orderList")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public String getOrderList(HttpServletRequest request, @ModelAttribute("orderSearch")OrderSearchDto searchDto, Model model) {
        // 세션에서 로그인 사용자 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/login?redirectURL=/orderList";
        }

        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/orderList";
        }

        /// 로그인한 사용자 ID로 검색 조건 설정
        searchDto.setMemberId(loginMember.getMemberId());
        // 검색 조건에 따른 대출 목록 조회
        List<Order> orders = orderService.findOrders(searchDto);
        model.addAttribute("orders", orders);

        return "order/orderList"; // 주문 목록 페이지
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(
            @PathVariable Long orderId,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            Model model) {
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/orderList";
        }

        try {
            // 주문이 로그인된 사용자의 것인지 확인
            Order order = orderService.getOrderById(orderId);
            if (!order.getMember().getMemberId().equals(loginMember.getMemberId())) {
                model.addAttribute("error", "본인의 주문만 취소할 수 있습니다.");
                return "redirect:/orderList";
            }

            orderService.cancelOrder(orderId);
            model.addAttribute("message", "주문이 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "주문 취소에 실패했습니다: " + e.getMessage());
        }

        return "redirect:/orderList";
    }


    @GetMapping("/dateRange")
    public String getOrdersByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            Model model) {
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/orderList";
        }

        model.addAttribute("orders", orderService.getOrdersByDateRange(startDate, endDate));
        return "/order/orderList"; // 주문 목록 페이지
    }
}
