package solo.blog.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

public interface SpringDataJpaRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleLike(String title);

    List<Post> findByLoginIdLike(String loginId);

    // 쿼리 메서드
    List<Post> findByTitleLikeAndLoginIdLike(String title, String loginId);

    // 쿼리 직접 실행
    @Query("select i from Post i where i.title like :title and i.loginId like :loginId")
    List<Post> findPosts(@Param("title") String title, @Param("loginId") String loginId);
}
