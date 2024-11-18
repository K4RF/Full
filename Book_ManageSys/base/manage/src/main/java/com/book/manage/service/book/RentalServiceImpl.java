package com.book.manage.service.book;

import com.book.manage.entity.Rental;
import com.book.manage.repository.book.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService{
    private final RentalRepository rentalRepository;

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
}
