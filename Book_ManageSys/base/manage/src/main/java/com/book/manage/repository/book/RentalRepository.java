package com.book.manage.repository.book;

import com.book.manage.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    // 특정 유저의 대출 상태 검색
    List<Rental> findByMemberId(Long memberId);

    // 특정 도서의 대출 상태 검색
    List<Rental> findByBookId(Long bookId);

    //특정 상태의 대출 검색
    List<Rental> findByRentalStatus(String rentalStatus);
}
