package solo.blog.service.jpa.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.QPost;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.repository.jpa.post.PostQueryRepository;
import solo.blog.repository.jpa.post.TagJpaRepository;
import solo.blog.service.jpa.CommentJpaService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        // 게시글 조회
        Post post = jpaRepositoryV2.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 게시글 정보 업데이트
        post.setTitle(postUpdateDto.getTitle());
        post.setContent(postUpdateDto.getContent());

        // 태그 정보 업데이트
        List<Tag> updatedTags = updateTags(postUpdateDto.getTags(), post);

        // 기존 태그와 비교해 사라진 태그 삭제
        changeDelete(post, updatedTags);

        // 게시글 저장
        post.setTags(updatedTags);
        return jpaRepositoryV2.save(post);
    }


    private List<Tag> updateTags(List<Tag> tagsToUpdate, Post post) {
        List<Tag> updatedTags = new ArrayList<>();
        for (Tag tag : tagsToUpdate) {
            Tag existingTag = tagJpaRepository.findByNameAndPostId(tag.getName(), post.getId())
                    .orElseGet(() -> {
                        Tag newTag = new Tag(tag.getName(), post); // 새로운 태그 생성 시 post와 연결
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

    @Transactional
    public void changeDelete(Post post, List<Tag> updatedTags) {
        // 업데이트된 태그 이름만 추출
        List<String> updatedTagNames = updatedTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        // 삭제할 태그 처리
        tagJpaRepository.updateDelete(post.getId(), updatedTagNames);
    }
}
