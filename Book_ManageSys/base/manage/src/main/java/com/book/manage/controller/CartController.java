package com.book.manage.controller;

import com.book.manage.entity.Book;
import com.book.manage.entity.Cart;
import com.book.manage.service.book.BookService;
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

    @GetMapping
    public String viewCart(@SessionAttribute(value = "cart", required = false) List<Cart> cart, Model model) {
        if (cart == null) {
            cart = new ArrayList<>();
        }
        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", cart.stream().mapToInt(Cart::getTotalPrice).sum());
        return "order/cartView"; // 장바구니 페이지
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

        return "redirect:/cart";
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
}
