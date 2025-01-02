package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Cart;
import com.book.manage.entity.Member;
import com.book.manage.service.book.BookService;
import com.book.manage.service.order.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
    public String removeFromCart(
            @PathVariable Long bookId,
            @SessionAttribute(value = "cart", required = false) List<Cart> cart) {
        if (cart != null) {
            cart.removeIf(item -> item.getBookId().equals(bookId));
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "redirect:/cart";
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

}
