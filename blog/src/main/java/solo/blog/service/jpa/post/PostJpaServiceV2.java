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
        // 1. 먼저 Post 저장
        Post savedPost = jpaRepositoryV2.save(post);

        // 2. Tag 생성 후 저장
        List<Tag> tags = createTags(tagNames, savedPost); // Post와 연관된 Tag 생성
        savedPost.setTags(tags);

        // 3. Post 업데이트
        return jpaRepositoryV2.save(savedPost);
    }


    private List<Tag> createTags(Set<String> tagNames, Post post) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagJpaRepository.findByNameAndPostId(tagName, post.getId())
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tagName, post); // post와 연결된 태그 생성
                        return tagJpaRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    @Transactional
    public Post update(Long postId, PostUpdateDto postUpdateDto) {
        // 포스트 가져오기
        Post post = jpaRepositoryV2.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 태그가 null인 경우 빈 리스트로 초기화
        if (postUpdateDto.getTags() == null) {
            postUpdateDto.setTags(new ArrayList<>());
        }

        // 기존 태그 삭제 및 업데이트
        List<Tag> updatedTags = updateTags(postUpdateDto.getTags(), post);
        post.setTags(updatedTags);

        // 제목, 내용 업데이트
        post.setTitle(postUpdateDto.getTitle());
        post.setContent(postUpdateDto.getContent());

        return jpaRepositoryV2.save(post);
    }

    private List<Tag> updateTags(List<Tag> tagsToUpdate, Post post) {
        // 기존 태그 목록 가져오기
        List<Tag> existingTags = post.getTags();

        // 기존 태그 중 삭제할 태그 찾아 삭제
        for (Tag existingTag : existingTags) {
            if (tagsToUpdate.stream().noneMatch(t -> t.getName().equals(existingTag.getName()))) {
                tagJpaRepository.delete(existingTag);
            }
        }

        // 새로운 태그 추가 또는 업데이트
        List<Tag> updatedTags = new ArrayList<>();
        for (Tag tag : tagsToUpdate) {
            Tag existingTag = tagJpaRepository.findByNameAndPostId(tag.getName(), post.getId())
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tag.getName(), post);
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

    // PostJpaService.java
    @Transactional
    public void delete(Long postId) {
        Post post = jpaRepositoryV2.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        // 연관된 태그 삭제
        tagJpaRepository.deleteByPostId(postId);

        // 댓글 삭제
        commentJpaService.deleteByPostId(postId);

        // 포스트 삭제
        jpaRepositoryV2.delete(post);
    }


    @Transactional
    public void incrementViewCount(Long postId) {
        Post post = jpaRepositoryV2.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
        post.incrementViewCount();
        jpaRepositoryV2.save(post); // 조회수만 업데이트
    }
}
