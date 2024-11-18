package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j  // 클래스에 @Slf4j 추가
@SpringBootTest
@Transactional
class RentalServiceImplTest {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RentalServiceImpl rentalService;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDetails("Test details");  // 빈 문자열이나 실제 값을 추가
        book = bookRepository.save(book);

        member = new Member();
        member.setLoginId("testUser");
        member.setPassword("testPassword");
        member.setNickname("testNickname");
        member = memberRepository.save(member);
    }

    @Test
    void testCreateRental() {
        // Given
        assertNotNull(book);
        assertNotNull(member);
        log.info("Book created with ID: {}, Title: {}", book.getBookId(), book.getTitle());  // 로그 추가
        log.info("Member created with ID: {}, Nickname: {}", member.getMemberId(), member.getNickname());  // 로그 추가

        // When
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());
        log.info("Rental created with ID: {}, Status: {}", rental.getRentalId(), rental.getRentalStatus());  // 로그 추가

        // Then
        assertNotNull(rental);
        assertEquals(book.getBookId(), rental.getBook().getBookId());
        assertEquals(member.getMemberId(), rental.getMember().getMemberId());
        assertEquals("대출중", rental.getRentalStatus());
    }


    @Test
    void testReturnBook() {
        // Given
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());
        Long rentalId = rental.getRentalId();
        Long bookId = rental.getBook().getBookId(); // rental의 bookId를 가져옵니다.
        assertNotNull(rental);
        assertEquals("대출중", rental.getRentalStatus());
        log.info("Rental created with ID: {}. Initial Status: {}", rental.getRentalId(), rental.getRentalStatus());

        // When
        // 실제 반납 처리 호출
        rentalService.returnBook(rentalId, bookId);
        // 반납된 대출 기록을 확인
        Rental updatedRental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("대출 기록을 찾을 수 없습니다."));

        log.info("Rental returned with ID: {}. Updated Status: {}, Return Date: {}", updatedRental.getRentalId(), updatedRental.getRentalStatus(), updatedRental.getReturnDate());

        // Then
        assertEquals("반납완료", updatedRental.getRentalStatus());
        assertNotNull(updatedRental.getReturnDate());
    }


    @Test
    void testUpdateRentalStatus() {
        // Given
        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now().minusDays(20));  // 20일 전 대출된 책
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentalStatus("대출중");
        rentalRepository.save(rental);
        log.info("Rental created with ID: {}, Status: {}", rental.getRentalId(), rental.getRentalStatus());

        // When
        rentalService.updateRentalStatus();
        log.info("Rental status updated for ID: {}. New Status: {}", rental.getRentalId(), rental.getRentalStatus());  // 상태 변경 후 로그

        // Then
        Rental updatedRental = rentalRepository.findById(rental.getRentalId()).orElseThrow();
        assertEquals("연체", updatedRental.getRentalStatus());
    }

    @Test
    void testGetRentalByMember() {
        // Given
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());

        // When
        List<Rental> rentals = rentalService.getRentalByMember(member.getMemberId());

        // Then
        assertEquals(1, rentals.size());
        assertEquals(rental.getRentalId(), rentals.get(0).getRentalId());
    }

    @Test
    void testGetRentalByBook() {
        // Given
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());

        // When
        List<Rental> rentals = rentalService.getRentalByBook(book.getBookId());

        // Then
        assertEquals(1, rentals.size());
        assertEquals(rental.getRentalId(), rentals.get(0).getRentalId());
    }

    @Test
    void testCreateRentalWhenBookNotFound() {
        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            rentalService.createRental(999L, member.getMemberId());  // 존재하지 않는 bookId
        });

        log.error("Error occurred: {}", exception.getMessage());  // 에러 발생 시 로그
        assertEquals("책을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void testCreateRentalWhenMemberNotFound() {
        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            rentalService.createRental(book.getBookId(), 999L);  // 존재하지 않는 memberId
        });

        log.error("Error occurred: {}", exception.getMessage());  // 에러 발생 시 로그
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void testRental() {
        // Given
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());
        assertNotNull(rental);
        assertEquals("대출중", rental.getRentalStatus());

        // When
        String rentalStatus = rentalService.getRentalStatusByBookId(book.getBookId());

        // Then
        assertEquals("대출중", rentalStatus);
    }

}
