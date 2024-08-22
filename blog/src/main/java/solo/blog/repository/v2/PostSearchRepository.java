package solo.blog.repository.v2;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostSearchRepository {
    private static final Map<Long, Post> store = new HashMap<>();
    private static long sequence = 0L;

    public Post save(Post post) {
        post.setId(++sequence);
        store.put(post.getId(), post);
        return post;
    }

    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Post> findAll(PostSearchCond cond) {
        String title = cond.getTitle();
        String loginId = cond.getLoginId();
        return store.values().stream()
                .filter(post -> {
                    if (ObjectUtils.isEmpty(title)) {
                        return true;
                    }
                    return post.getTitle().contains(title);
                }).filter(post -> {
                    if (ObjectUtils.isEmpty(loginId)) {
                        return true;
                    }
                    return post.getLoginId().contains(loginId);
                })
                .collect(Collectors.toList());
    }

    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = findById(postId).orElseThrow();
        findPost.setTags(updateParam.getTags()); // 추가
        findPost.setTitle(updateParam.getTitle());
        findPost.setContent(updateParam.getContent());
    }

    public void clearStore()
    {
        store.clear();
    }
}
