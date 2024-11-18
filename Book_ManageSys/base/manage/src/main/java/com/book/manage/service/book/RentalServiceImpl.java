package com.book.manage.service.book;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.book.RentalRepository;
import com.book.manage.repository.member.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService{
    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    public void updateRentalStatus() {
        List<Rental> rentals = rentalRepository.findAll();

        for (Rental rental : rentals) {
            if (rental.getReturnDate() != null) {
                rental.setRentalStatus("반납완료");
            } else if (LocalDate.now().isAfter(rental.getRentalDate().plusDays(14))) { // 2주후 연체 처리
                rental.setRentalStatus("연체");
            }else{
                rental.setRentalStatus("대출");
            }
            rentalRepository.save(rental);  // 상태 갱신
        }
    }

    @Override
    public List<Rental> getRentalByMember(Long memberId) {
        return rentalRepository.findByMemberId(memberId);
    }

    @Override
    public List<Rental> getRentalByBook(Long bookId) {
        return rentalRepository.findByBookId(bookId);
    }

    // 대출 생성
    public Rental createLoan(Long bookId, Long userId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new EntityNotFoundException("책을 찾을 수 없습니다."));
        Member member = memberRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        Rental rental = new Rental();
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentalDate(LocalDate.now());
        rental.setRentalStatus("대출중");

        return rentalRepository.save(rental);
    }

    // 반납 처리
    public Rental returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(() -> new EntityNotFoundException("대출 기록을 찾을 수 없습니다."));

        rental.setReturnDate(LocalDate.now());
        rental.setRentalStatus("반납완료");

        return rentalRepository.save(rental);
    }
}
