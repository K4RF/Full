package com.book.manage.entity;

import lombok.Data;

@Data
public class Cart{
    private Long bookId;
    private String title;
    private int price;
    private int quantity;

    public int getTotalPrice() {
        return price * quantity;
    }
}
