package com.book.manage.repository.book;

import com.book.manage.entity.Rental;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);

    Optional<Rental> findById(Long rentalId);

    List<Rental> findByMemberMemberId(Long memberId);

    List<Rental> findByBookBookId(Long bookId);

    List<Rental> findAll();
    void updateRentalStatus();
}
