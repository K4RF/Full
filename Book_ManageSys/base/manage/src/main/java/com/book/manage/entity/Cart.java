package com.book.manage.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class Cart{
    private Long bookId;
    private String title;
    private int price;
    private int quantity;
    private int totalPrice;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(bookId, cart.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
}
