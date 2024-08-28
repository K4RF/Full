package solo.blog.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import solo.blog.entity.database.Post;

public interface JpaRepositoryV2 extends JpaRepository<Post, Long> {
}
