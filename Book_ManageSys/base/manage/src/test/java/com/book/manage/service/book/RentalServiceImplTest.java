package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        // When
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());

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
        assertNotNull(rental);
        assertEquals("대출중", rental.getRentalStatus());

        // When
        Rental updatedRental = rentalService.returnBook(rental.getRentalId());

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

        // When
        rentalService.updateRentalStatus();

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

        assertEquals("책을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void testCreateRentalWhenMemberNotFound() {
        // When & Then
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            rentalService.createRental(book.getBookId(), 999L);  // 존재하지 않는 memberId
        });

        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }
}
