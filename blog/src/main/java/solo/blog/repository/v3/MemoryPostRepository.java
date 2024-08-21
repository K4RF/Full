package solo.blog.repository.v3;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import solo.blog.entity.v2.Post;
import solo.blog.model.PostUpdateDto;
import solo.blog.service.v3.PostSearchCond;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MemoryPostRepository implements PostRepository {

    private static final Map<Long, Post> store = new HashMap<>(); //static
    private static long sequence = 0L; //static

    @Override
    public Post save(Post item) {
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = findById(postId).orElseThrow();
        findPost.setTitle(updateParam.getTitle());
        findPost.setTags(updateParam.getTags());
        findPost.setContent(updateParam.getContent());
    }

    @Override
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
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

    public void clearStore() {
        store.clear();
    }
}
