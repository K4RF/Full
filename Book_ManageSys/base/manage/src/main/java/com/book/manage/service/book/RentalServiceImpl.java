package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    public void updateRentalStatus() {
        rentalRepository.updateRentalStatus(); // QueryDSL로 상태 갱신
    }

    @Override
    public List<Rental> getRentalByMember(Long memberId) {
        return rentalRepository.findByMemberMemberId(memberId); // QueryDSL 사용
    }

    @Override
    public List<Rental> getRentalByBook(Long bookId) {
        return rentalRepository.findByBookBookId(bookId); // QueryDSL 사용
    }

    // 대출 생성
    public Rental createRental(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("책을 찾을 수 없습니다."));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Rental rental = new Rental();
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentalDate(LocalDate.now());
        rental.setRentalStatus("대출중");

        return rentalRepository.save(rental);  // QueryDSL을 통한 저장
    }

    // 반납 처리 메서드
    public Rental returnBook(Long rentalId, Long bookId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("대출 기록을 찾을 수 없습니다."));

        // 대출 중인 책인지 확인
        if (!rental.getBook().getBookId().equals(bookId)) {
            throw new IllegalArgumentException("책 ID가 일치하지 않습니다.");
        }

        rental.setReturnDate(LocalDate.now());
        rental.setRentalStatus("대출 가능");  // 반납 상태로 업데이트

        return rentalRepository.save(rental);  // 반납된 정보 저장
    }

    // 책 ID를 기반으로 렌탈 상태 가져오기
    public String getRentalStatusByBookId(Long bookId) {
        // "대출중" 상태로 대출 중인 책을 확인
        Optional<Rental> rental = rentalRepository.findByBookIdAndRentalStatus(bookId, "대출중");

        // 대출 중인 책이 있으면 "대출중" 상태, 없으면 "대출 가능" 상태 반환
        return rental.isPresent() ? "대출중" : "대출 가능";
    }

    // 대출 중인 도서에 대한 대출 기록을 가져오는 메서드
    public Rental findActiveRentalByBookId(Long bookId) {
        // "대출중" 상태인 대출 기록을 조회
        return rentalRepository.findByBookIdAndRentalStatus(bookId, "대출중")
                .orElse(null);  // 대출 중인 기록이 없으면 null 반환
    }

}
