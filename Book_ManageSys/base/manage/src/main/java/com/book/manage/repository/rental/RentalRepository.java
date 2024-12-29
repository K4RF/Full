package com.book.manage.repository.rental;

import com.book.manage.entity.Rental;
import com.book.manage.entity.dto.RentalSearchDto;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);
    Optional<Rental> findLatestRental(Long bookId, Long memberId);

    Optional<Rental> findById(Long rentalId);

    List<Rental> findByMemberMemberId(Long memberId);

    List<Rental> findByBookBookId(Long bookId);

    List<Rental> findAll();
    void updateRentalStatus();
    Optional<Rental> findByBookIdAndRentalStatus(@Param("bookId") Long bookId, String status);
    void delete(Rental rental);
    List<Rental> findAll(RentalSearchDto searchParam);
}
