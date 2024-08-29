package solo.blog.service.jpa.post;

import solo.blog.entity.database.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostJpaService {
    Post save(Post post, Set<String> tagNames);
    void update(Long postId, PostUpdateDto updateParam);
    Optional<Post> findById(Long id);
    List<Post> findPosts(PostSearchCond cond);
}
