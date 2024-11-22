package com.book.manage.service.book;

import com.book.manage.entity.Rental;
import com.book.manage.entity.dto.RentalSearchDto;

import java.util.List;

public interface RentalService {
    void updateRentalStatus();

    List<Rental> getRentalByMember(Long memberId);

    List<Rental> getRentalByBook(Long bookId);
    Rental createRental(Long bookId, Long userId);
    Rental returnBook(Long rentalId, Long bookId);
    String getRentalStatusByBookId(Long bookId);
    Rental findActiveRentalByBookId(Long bookId);
    void deleteRentalsByBookId(Long bookId);
    List<Rental> findRentals(RentalSearchDto searchParam);
}
