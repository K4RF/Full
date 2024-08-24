package solo.blog.service.jdbc;

import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostDBService {
    Post save(Post post, Set<String> tagNames);
    void update(Long postId, PostUpdateDto updateParam);
    Optional<Post> findById(Long id);
    List<Post> findPosts(PostSearchCond cond);
}
