package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트 후 데이터베이스가 롤백되어 상태를 유지
public class RentalServiceImplTest {

    @Autowired
    private RentalServiceImpl rentalService;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Book book;
    private Member member;

    @BeforeEach
    void setUp() {
        // 샘플 데이터 준비
        book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDetails("Test Details");
        bookRepository.save(book);

        Member member = new Member("testUser", "testNickname", "testPassword");
        memberRepository.save(member);
    }

    @Test
    void testCreateRental() {
        // 대출 생성
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());

        // 생성된 대출이 제대로 저장되었는지 확인
        assertThat(rental).isNotNull();
        assertThat(rental.getRentalStatus()).isEqualTo("대출중");
        assertThat(rental.getBook().getTitle()).isEqualTo("Test Book");
        assertThat(rental.getMember().getLoginId()).isEqualTo("testUser");
        assertThat(rental.getRentalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void testReturnBook() {
        // 먼저 대출을 생성해야 반납 테스트가 가능
        Rental rental = rentalService.createRental(book.getBookId(), member.getMemberId());

        // 대출 ID로 반납 처리
        Rental returnedRental = rentalService.returnBook(rental.getRentalId());

        // 반납 상태가 "반납완료"로 변경되었는지 확인
        assertThat(returnedRental.getRentalStatus()).isEqualTo("반납완료");
        assertThat(returnedRental.getReturnDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void testGetRentalByMember() {
        // 대출 생성
        rentalService.createRental(book.getBookId(), member.getMemberId());

        // 해당 회원의 대출 목록 조회
        List<Rental> rentals = rentalService.getRentalByMember(member.getMemberId());

        // 대출 목록에 1개의 대출이 포함되어 있는지 확인
        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getMember().getLoginId()).isEqualTo("testUser");
    }

    @Test
    void testGetRentalByBook() {
        // 대출 생성
        rentalService.createRental(book.getBookId(), member.getMemberId());

        // 해당 도서의 대출 목록 조회
        List<Rental> rentals = rentalService.getRentalByBook(book.getBookId());

        // 대출 목록에 1개의 대출이 포함되어 있는지 확인
        assertThat(rentals).hasSize(1);
        assertThat(rentals.get(0).getBook().getTitle()).isEqualTo("Test Book");
    }
}
