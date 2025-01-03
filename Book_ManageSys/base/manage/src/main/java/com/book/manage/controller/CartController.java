package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Cart;
import com.book.manage.entity.Member;
import com.book.manage.service.book.BookService;
import com.book.manage.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final BookService bookService;
    private final OrderService orderService;

    @GetMapping
    public String viewCart(HttpSession session, @SessionAttribute(value = "cart", required = false) List<Cart> cart, Model model) {
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cart.stream().mapToInt(Cart::getTotalPrice).sum());
        return "order/cartForm"; // 장바구니 페이지
    }

    @PostMapping("/{bookId}/add")
    public String addToCart(
            @PathVariable Long bookId,
            @RequestParam int quantity,
            @SessionAttribute(value = "cart", required = false) List<Cart> cart,
            HttpSession session) {

        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("도서를 찾을 수 없습니다."));

        // 동일한 도서가 있는지 확인 후 처리
        Optional<Cart> existingItem = cart.stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            Cart newItem = new Cart();
            newItem.setBookId(bookId);
            newItem.setTitle(book.getTitle());
            newItem.setPrice(book.getPrice());
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }

        return "redirect:/bookList/" + bookId;
    }

    @PostMapping("/{bookId}/remove")
    @ResponseBody
    public ResponseEntity<String> removeFromCart(
            @PathVariable Long bookId,
            @SessionAttribute(value = "cart", required = false) List<Cart> cart,
            HttpSession session) {

        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().body("장바구니가 비어 있습니다.");
        }

        boolean isRemoved = cart.removeIf(item -> item.getBookId().equals(bookId));

        if (!isRemoved) {
            return ResponseEntity.badRequest().body("해당 도서가 장바구니에 없습니다.");
        }

        if (cart.isEmpty()) {
            session.removeAttribute("cart"); // 장바구니 비우기
        } else {
            session.setAttribute("cart", cart); // 세션에 업데이트된 장바구니 저장
        }

        return ResponseEntity.ok("장바구니에서 삭제되었습니다.");
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String viewCartCheck(HttpSession session,
                                @SessionAttribute(value = "cart", required = false) List<Cart> cart, Model model,
                                @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        if (loginMember == null) {
            String redirectUrl = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectUrl;
        }
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // 총 합계 계산
        int totalPrice = cart.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice); // 모델에 totalPrice 추가
        return "order/orderConfirm"; // 장바구니 페이지
    }

    @PostMapping("/checkout")
    public String checkout(
            @SessionAttribute(value = "cart", required = false) List<Cart> cart,
            @SessionAttribute(value = "loginMember", required = false) Member loginMember,
            HttpSession session) {
        if (loginMember == null) {
            return "redirect:/login?redirectURL=/cart";
        }
        if (cart != null && !cart.isEmpty()) {
            cart.forEach(item -> orderService.createOrder(loginMember.getMemberId(), item.getBookId(), item.getQuantity()));
            session.removeAttribute("cart"); // 장바구니 비우기
        }
        return "redirect:/orderList";
    }
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<String> updateQuantity(
            @RequestParam Long bookId,
            @RequestParam int quantity,  // quantity를 @RequestParam으로 받기
            @SessionAttribute(value = "cart", required = false) List<Cart> cart,
            HttpSession session, Model model) {

        // 장바구니가 비어있는 경우
        if (cart == null || cart.isEmpty()) {
            return ResponseEntity.badRequest().body("장바구니가 비어 있습니다.");
        }

        // 장바구니에서 해당 도서를 찾고 수량 업데이트
        Optional<Cart> itemToUpdate = cart.stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();

        if (!itemToUpdate.isPresent()) {
            return ResponseEntity.badRequest().body("장바구니에 해당 도서가 없습니다.");
        }

        Cart item = itemToUpdate.get();
        item.setQuantity(quantity);  // 수량 업데이트
        item.setTotalPrice(item.getPrice() * quantity); // 총 가격 업데이트

        session.setAttribute("cart", cart);  // 업데이트된 장바구니 세션에 저장
        return ResponseEntity.ok("수량이 업데이트되었습니다.");
    }
}
