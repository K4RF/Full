package solo.blog.service.v3;

import solo.blog.entity.v2.Post;
import solo.blog.model.PostUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostService {

    Post save(String title, String content, String loginId, Set<String> tagNames);

    void update(Long postId, PostUpdateDto updateParam);

    Optional<Post> findById(Long id);

    List<Post> findItems(PostSearchCond cond);
}

