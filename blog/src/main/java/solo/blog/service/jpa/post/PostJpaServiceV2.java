package solo.blog.service.jpa.post;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.repository.jpa.post.PostQueryRepository;
import solo.blog.repository.jpa.post.TagJpaRepository;
import solo.blog.service.jpa.CommentJpaService;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Primary
public class PostJpaServiceV2 implements PostJpaService {
    private final JpaRepositoryV2 jpaRepositoryV2;
    private final PostQueryRepository postQueryRepository;
    private final TagJpaRepository tagJpaRepository;
    private final CommentJpaService commentJpaService;

    @Transactional
    public Post save(Post post, Set<String> tagNames) {
        // 게시글 저장 (이때 post.getId()는 null일 수 있음)
        Post savedPost = jpaRepositoryV2.save(post);

        // 태그 생성 및 저장
        List<Tag> tags = createTags(tagNames, savedPost.getId()); // savedPost.getId()를 전달
        savedPost.setTags(tags);

        // 태그가 설정된 게시글 다시 저장
        return jpaRepositoryV2.save(savedPost);
    }

    @Transactional
    public Post update(Long postId, PostUpdateDto postUpdateDto) {
        // 게시글 조회
        Post post = jpaRepositoryV2.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 게시글 정보 업데이트
        post.setTitle(postUpdateDto.getTitle());
        post.setContent(postUpdateDto.getContent());

        // 태그 정보 업데이트
        List<Tag> tags = updateTags(postUpdateDto.getTags(), postId); // postId를 전달
        post.setTags(tags);

        // 게시글 저장
        return jpaRepositoryV2.save(post);
    }

    private List<Tag> createTags(Set<String> tagNames, Long postId) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagJpaRepository.findByNameAndPostId(tagName, postId)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName, postId);
                        return tagJpaRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    private List<Tag> updateTags(List<Tag> tagsToUpdate, Long postId) {
        List<Tag> updatedTags = new ArrayList<>();
        for (Tag tag : tagsToUpdate) {
            Tag existingTag = tagJpaRepository.findByNameAndPostId(tag.getName(), postId)
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tag.getName(), postId);
                        return tagJpaRepository.save(newTag);
                    });
            updatedTags.add(existingTag);
        }
        return updatedTags;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jpaRepositoryV2.findById(id);
    }

    @Override
    public List<Post> findPosts(PostSearchCond cond) {
        return postQueryRepository.findAll(cond);
    }

    public boolean isTitleDuplicate(String title) {
        return jpaRepositoryV2.existsByTitle(title);
    }

    public boolean hasDuplicateTags(List<String> tagNames) {
        Set<String> tagSet = new HashSet<>(tagNames);
        return tagSet.size() != tagNames.size();
    }

    public List<Post> findByLoginId(String loginId) {
        return postQueryRepository.findByLoginId(loginId);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = jpaRepositoryV2.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        commentJpaService.deleteByPostId(postId);

        jpaRepositoryV2.delete(post);
    }

    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = jpaRepositoryV2.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
        post.incrementViewCount();
        jpaRepositoryV2.save(post); // 조회수만 업데이트
    }
}
