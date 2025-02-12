package com.book.manage.service.rental;

import com.book.manage.entity.Book;
import com.book.manage.entity.Member;
import com.book.manage.entity.Rental;
import com.book.manage.entity.dto.RentalSearchDto;
import com.book.manage.repository.book.BookRepository;
import com.book.manage.repository.rental.RentalRepository;
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
        rentalRepository.updateRentalStatus(); // 연체, 대출, 반납 상태 갱신
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
        // 책을 찾고 대출 가능 여부를 확인
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("책을 찾을 수 없습니다."));

        // 책이 대출 불가능한 경우
        if (!book.getRentalAbleBook()) {
            throw new IllegalStateException("이 책은 대출할 수 없습니다.");
        }

        // 대출 중인 도서인지 확인
        Rental activeRental = findActiveRentalByBookId(bookId);
        if (activeRental != null) {
            throw new IllegalStateException("이미 대출 중인 도서입니다.");
        }

        // 유저 확인
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

        // 새로운 대출 기록 생성
        Rental rental = new Rental();
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(14)); // 반납 마감일 설정
        rental.setRentalStatus("대출중");  // 대출 상태 설정

        // 책의 대출 가능 여부를 false로 설정
        book.setRentalAbleBook(false);
        bookRepository.save(book);  // 책 정보 업데이트

        // 대출 기록 저장
        return rentalRepository.save(rental);
    }

    // 반납 처리 메서드 (최신 대출 기록 기준)
    @Override
    public Rental returnBook(Long bookId, Long memberId) {
        // 최신 대출 기록을 조회 (반납되지 않은 대출 중 가장 최신 대출)
        Rental rental = rentalRepository.findLatestRental(bookId, memberId)
                .orElseThrow(() -> new EntityNotFoundException("대출 기록을 찾을 수 없습니다."));

        // 대출 상태가 '대출중'인지 확인
        if (!rental.getRentalStatus().equals("대출중")) {
            throw new IllegalStateException("이 책은 이미 반납된 상태입니다.");
        }

        // 반납 처리
        rental.setReturnDate(LocalDate.now());
        rental.setRentalStatus("반납완료");  // 반납 상태로 업데이트

        // 책의 대출 가능 여부를 true로 설정 (반납되었으므로 대출 가능)
        Book book = rental.getBook();
        book.setRentalAbleBook(true);
        bookRepository.save(book);  // 책 정보 업데이트

        return rentalRepository.save(rental);  // 반납된 정보 저장
    }

    // 책 ID를 기반으로 렌탈 상태 가져오기
    public String getRentalStatusByBookId(Long bookId) {
        // "대출중" 상태로 대출 중인 책을 확인
        Optional<Rental> rental = rentalRepository.findByBookIdAndRentalStatus(bookId, "대출중");

        // 대출 중인 책이 있으면 "대출중" 상태, 없으면 "대출 가능" 상태 반환
        return rental.isPresent() ? "대출중" : "";
    }

    // 대출 중인 도서에 대한 대출 기록을 가져오는 메서드
    public Rental findActiveRentalByBookId(Long bookId) {
        // "대출중" 상태인 대출 기록을 조회
        return rentalRepository.findByBookIdAndRentalStatus(bookId, "대출중")
                .orElse(null);  // 대출 중인 기록이 없으면 null 반환
    }

    // 도서와 관련된 대출 데이터 삭제
    public void deleteRentalsByBookId(Long bookId) {
        List<Rental> rentals = rentalRepository.findByBookBookId(bookId); // 책 ID로 대출 목록 조회
        for (Rental rental : rentals) {
            rentalRepository.delete(rental); // 대출 기록 삭제
        }
    }

    // 연체 상태 갱신 메서드
    public void updateOverdueRentals() {
        rentalRepository.updateRentalStatus();  // 연체 상태를 포함한 모든 대출 상태 갱신
    }


    // 검색 조건에 따른 대출 목록 조회
    @Override
    public List<Rental> findRentals(RentalSearchDto searchParam) {
        return rentalRepository.findAll(searchParam);
    }

    @Override
    public void deleteRentalsByMemberId(Long memberId) {
        List<Rental> rentals = rentalRepository.findByMemberMemberId(memberId); // 책 ID로 대출 목록 조회
        for (Rental rental : rentals) {
            rentalRepository.delete(rental); // 대출 기록 삭제
        }
    }

    @Override
    // 대출 연장 처리
    public boolean extendRental(Long bookId, Long memberId) {
        Optional<Rental> rentalOptional = rentalRepository.findLatestRental(bookId, memberId);

        if (rentalOptional.isPresent()) {
            Rental rental = rentalOptional.get();

            // 연장 가능 여부 확인
            if (rental.getExtensionCount() >= 2) {
                throw new IllegalStateException("최대 연장 횟수를 초과했습니다.");
            }

            // 연장 처리
            rental.setDueDate(rental.getDueDate().plusDays(7)); // 기본 7일 연장
            rental.setExtensionCount(rental.getExtensionCount() + 1);
            rentalRepository.save(rental);

            return true;
        }
        throw new IllegalStateException("대출 기록을 찾을 수 없습니다.");
    }

    // 대출 연장 횟수 조회
    public int getExtensionCount(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록을 찾을 수 없습니다."));
        return rental.getExtensionCount(); // Rental 엔티티의 extensionCount 반환
    }

    @Override
    public void updateAdminRentalStatus(Long rentalId, String rentalStatus) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 존재하지 않습니다."));

        // 반납 완료 상태라면 반납일을 자동으로 설정
        if ("반납 완료".equals(rentalStatus) && rental.getReturnDate() == null) {
            rental.setReturnDate(java.time.LocalDate.now());
        }

        // 반납 완료 상태에서 다시 대출중으로 변경하면 반납일을 null로 설정
        if ("대출중".equals(rentalStatus)) {
            rental.setReturnDate(null);
        }

        rental.setRentalStatus(rentalStatus);
        rentalRepository.save(rental);
    }

    @Override
    public void deleteRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("대출 기록이 존재하지 않습니다."));

        // 도서의 대출 가능 여부를 true로 변경
        Book book = rental.getBook();
        book.setRentalAbleBook(true);
        bookRepository.save(book);

        rentalRepository.delete(rental);
    }

    @Override
    @Transactional
    public List<Rental> findAllRentals() {
        return rentalRepository.findAllRentals();
    }
}
