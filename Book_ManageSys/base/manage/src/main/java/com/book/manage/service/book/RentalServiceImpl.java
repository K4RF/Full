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

    // 반납 처리
    public Rental returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new EntityNotFoundException("대출 기록을 찾을 수 없습니다."));

        rental.setReturnDate(LocalDate.now());
        rental.setRentalStatus("반납완료");

        return rentalRepository.save(rental);  // QueryDSL을 통한 저장
    }

    public String getRentalStatusByBookId(Long bookId) {
        return rentalRepository.findByBookBookId(bookId).stream()
                .sorted((r1, r2) -> r2.getRentalDate().compareTo(r1.getRentalDate())) // 최신순 정렬
                .findFirst() // 최신 대출 기록 가져오기
                .map(Rental::getRentalStatus)
                .orElse("대출 가능");
    }


}
