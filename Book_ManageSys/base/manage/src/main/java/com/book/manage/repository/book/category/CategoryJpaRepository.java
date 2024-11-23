package com.book.manage.repository.book.category;

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

    public void updateDelete(Long bookId, List<String> tagNames) {
        QCategory category = QCategory.category;

        List<Category> tags = query
                .selectFrom(category)
                .where(category.book.bookId.eq(bookId)
                        .and(category.tag.notIn(tagNames))) // 업데이트된 태그 목록에 없는 태그들만 찾음
                .fetch();

        for (Category tagEntity : tags) {
            if (em.contains(tagEntity)) {
                em.remove(tagEntity);
            } else {
                Category managedTag = em.find(Category.class, tagEntity.getId());
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
            Category managedTag = em.find(Category.class, category.getId());
            if (managedTag != null) {
                em.remove(managedTag);
            }
        }
    }

    public void deleteByBookId(Long bookId) {
        QCategory category = QCategory.category;

        List<Category> tags = query
                .selectFrom(category)
                .where(category.book.bookId.eq(bookId))
                .fetch();

        for (Category tagEntity : tags) {
            if (em.contains(tagEntity)) {
                em.remove(tagEntity);
            } else {
                Category managedTag = em.find(Category.class,tagEntity.getId());
                if (managedTag != null) {
                    em.remove(managedTag);
                }
            }
        }
    }
}
