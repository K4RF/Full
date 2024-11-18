package com.book.manage.service.book;

import com.book.manage.entity.Rental;

import java.util.List;

public interface RentalService {
    void updateRentalStatus();

    List<Rental> getRentalByMember(Long memberId);

    List<Rental> getRentalByBook(Long bookId);
    Rental createRental(Long bookId, Long userId);
    Rental returnBook(Long rentalId);
}
