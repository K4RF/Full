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
        String loginId = cond.getAuthorName();

        // 저장된 데이터 로그 출력
        System.out.println("Current stored posts: " + store.values());

        return store.values().stream()
                .filter(post -> {
                    if (title == null || title.isEmpty()) {
                        return true;
                    }
                    boolean titleMatch = post.getTitle().contains(title);
                    System.out.println("Title filter: Post title: " + post.getTitle() + ", Search title: " + title + ", Match: " + titleMatch);
                    return titleMatch;
                })
                .filter(post -> {
                    if (loginId == null || loginId.isEmpty()) {
                        return true;
                    }
                    boolean loginIdMatch = post.getLoginId().contains(loginId);
                    System.out.println("LoginId filter: Post loginId: " + post.getLoginId() + ", Search loginId: " + loginId + ", Match: " + loginIdMatch);
                    return loginIdMatch;
                })
                .collect(Collectors.toList());
    }

    public void update(Long postId, PostUpdateDto updateParam) {
        Post findPost = findById(postId).orElseThrow();
        //findPost.setTags(updateParam.getTags()); // 추가
        findPost.setTitle(updateParam.getTitle());
        findPost.setContent(updateParam.getContent());
    }

    public void clearStore()
    {
        store.clear();
    }
}
