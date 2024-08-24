package solo.blog.repository.jdbc;

import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;

public interface PostDBRepository {
    Post save(Post post);
    void update(Long postId, PostUpdateDto updateParam);

    Optional<Post> findById(Long id);
    List<Post> findAll(PostSearchCond cond);
}
