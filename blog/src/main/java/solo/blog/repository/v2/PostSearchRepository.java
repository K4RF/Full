package solo.blog.repository.v2;

import org.springframework.stereotype.Repository;
import solo.blog.entity.v2.Post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostSearchRepository {
    private static final Map<Long, Post> store = new HashMap<>();
    private static long sequence = 0L;

    public Post save(Post post) {
        post.setId(++sequence);
        store.put(post.getId(), post);
        return post;
    }

    public Post findById(Long id) {
        return store.get(id);
    }

    public List<Post> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long postId, Post updateParam) {
        Post findPost = findById(postId);
        findPost.setLoginId(updateParam.getLoginId()); // 추가
        findPost.setTitle(updateParam.getTitle());
        findPost.setContent(updateParam.getContent());
    }

    public void clearStore()
    {
        store.clear();
    }
}
