package com.book.manage.repository.book;

import com.book.manage.entity.Category;
import com.book.manage.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CategoryRepository {
    @PersistenceContext
    private EntityManager em;
    private final JPAQueryFactory query;

    public CategoryRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }


    public Category save(Category category) {
        if(category.getId() == null) {
            // 중복 체크
            Optional<Category> existingCat = findByTagAndBookId(category.getTag(), category.getBook().getBookId());
            if(existingCat.isPresent()) {
                return existingCat.get();
            }
            em.persist(category);
        }else{
            em.merge(category);
        }return category;
    }
    public Optional<Category> findByTagAndBookId(String tag, Long bookId) {
        QCategory category = QCategory.category;
        Category foundTag = query
                .selectFrom(category)
                .where(category.tag.eq(tag)
                        .and(category.book.bookId.eq(bookId)))
                .fetchOne();
        return Optional.ofNullable(foundTag);
    }
}
