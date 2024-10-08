package solo.blog.repository.jpa.post;


import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

public interface JpaRepository {
    Post save(Post post);
    void update(Long postId, PostUpdateDto updateParam);

    Optional<Post> findById(Long id);
    List<Post> findAll(PostSearchCond cond);
}
