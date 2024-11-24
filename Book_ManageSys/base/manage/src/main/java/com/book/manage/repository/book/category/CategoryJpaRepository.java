package com.book.manage.repository.book.category;

import com.book.manage.entity.Book;
import com.book.manage.entity.Category;
import com.book.manage.entity.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryJpaRepository implements CategoryRepository {

    @PersistenceContext
    private EntityManager em;
    private final JPAQueryFactory query;

    public CategoryJpaRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    @Override
    public Category save(Category category) {
        if (category.getId() == null) {
            // 중복 체크
            Optional<Category> existingCat = findByTagAndBookId(category.getCate(), category.getBook().getBookId());
            if (existingCat.isPresent()) {
                return existingCat.get();
            }
            em.persist(category);
        } else {
            em.merge(category);
        }
        return category;
    }

    public Optional<Category> findByTagAndBookId(String cate, Long bookId) {
        QCategory category = QCategory.category;
        Category foundCate = query
                .selectFrom(category)
                .where(category.cate.eq(cate)
                        .and(category.book.bookId.eq(bookId)))
                .fetchOne();
        return Optional.ofNullable(foundCate);
    }

    public Optional<Category> findByCate(String cate) {
        QCategory category = QCategory.category;
        Category foundCategory = query
                .selectFrom(category)
                .where(category.cate.eq(cate))
                .fetchOne();
        return Optional.ofNullable(foundCategory);
    }

    public List<Category> findByBook(Book book) {
        QCategory category = QCategory.category;
        return query
                .selectFrom(category)
                .where(category.book.eq(book)) // 책과 연결된 카테고리 검색
                .fetch();
    }

    public void updateDelete(Long bookId, List<String> cateNames) {
        QCategory category = QCategory.category;

        List<Category> cates = query
                .selectFrom(category)
                .where(category.book.bookId.eq(bookId)
                        .and(category.cate.notIn(cateNames))) // 업데이트된 태그 목록에 없는 태그들만 찾음
                .fetch();

        for (Category cateEntity : cates) {
            if (em.contains(cateEntity)) {
                em.remove(cateEntity);
            } else {
                Category managedTag = em.find(Category.class, cateEntity.getId());
                if (managedTag != null) {
                    em.remove(managedTag);
                }
            }
        }
    }

    public void delete(Category category) {
        if (em.contains(category)) {
            em.remove(category);
        } else {
            Category managedCate = em.find(Category.class, category.getId());
            if (managedCate != null) {
                em.remove(managedCate);
            }
        }
    }

    public void deleteByBookId(Long bookId) {
        QCategory category = QCategory.category;

        List<Category> cates = query
                .selectFrom(category)
                .where(category.book.bookId.eq(bookId))
                .fetch();

        for (Category cateEntity : cates) {
            if (em.contains(cateEntity)) {
                em.remove(cateEntity);
            } else {
                Category managedCate = em.find(Category.class, cateEntity.getId());
                if (managedCate != null) {
                    em.remove(managedCate);
                }
            }
        }
    }

    public List<Category> findAll() {
        QCategory category = QCategory.category;

        return query
                .selectFrom(category)
                .fetch();
    }
    public List<Category> findByBookIdOrderByCateOrder(Long bookId) {
        QCategory category = QCategory.category;
        return query
                .selectFrom(category)
                .where(category.book.bookId.eq(bookId)) // 책과 연결된 카테고리 검색
                .orderBy(category.cateOrder.asc())  // cate_order 순으로 정렬
                .fetch();
    }
}
