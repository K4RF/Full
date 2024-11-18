package com.book.manage.repository.book;

import com.book.manage.entity.Rental;
import com.book.manage.entity.QRental;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.book.manage.entity.QRental.rental;

@Slf4j
@Repository
public class RentalJpaRepository implements RentalRepository {

    private final EntityManager em;
    private final JPAQueryFactory query;

    public RentalJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getRentalId() == null) {
            em.persist(rental);
            return rental;
        } else {
            return em.merge(rental);  // 업데이트 처리
        }
    }

    @Override
    public Optional<Rental> findById(Long rentalId) {
        Rental rental = em.find(Rental.class, rentalId);
        return Optional.ofNullable(rental);
    }

    @Override
    public List<Rental> findByMemberMemberId(Long memberId) {
        return query.select(rental)
                .from(rental)
                .where(rental.member.memberId.eq(memberId))
                .fetch();
    }

    @Override
    public List<Rental> findByBookBookId(Long bookId) {
        return query.select(rental)
                .from(rental)
                .where(rental.book.bookId.eq(bookId))
                .fetch();
    }

    // 연체 여부 및 상태 갱신
    @Override
    public List<Rental> findAll() {
        return query.select(rental)
                .from(rental)
                .fetch();
    }

    // 상태 변경 로직 (연체, 대출, 반납)
    public void updateRentalStatus() {
        List<Rental> rentals = findAll();

        for (Rental rental : rentals) {
            if (rental.getReturnDate() != null) {
                rental.setRentalStatus("반납완료");
            } else if (rental.getRentalDate().plusDays(14).isBefore(LocalDate.now())) { // 2주 후 연체 처리
                rental.setRentalStatus("연체");
            } else {
                rental.setRentalStatus("대출");
            }
            save(rental);  // 상태 갱신
        }
    }
}
