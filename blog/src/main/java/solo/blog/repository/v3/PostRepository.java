package solo.blog.repository.v3;

import org.springframework.stereotype.Repository;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostUpdateDto;
import solo.blog.service.v3.PostSearchCond;

import java.util.*;

public interface PostRepository {

    Post save(Post post);

    void update(Long postId, PostUpdateDto updateParam);

    Optional<Post> findById(Long id);

    List<Post> findAll(PostSearchCond cond);

}
