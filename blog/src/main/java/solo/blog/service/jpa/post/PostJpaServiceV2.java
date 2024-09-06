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

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Primary
public class PostJpaServiceV2 implements PostJpaService {
    private final JpaRepositoryV2 jpaRepositoryV2;
    private final PostQueryRepository postQueryRepository;
    private final TagJpaRepository tagJpaRepository;  // TagRepository를 주입받습니다.

    @Transactional
    public Post save(Post post, Set<String> tagNames) {
        // 태그 생성 및 저장
        List<Tag> tags = createTags(tagNames);
        post.setTags(tags);

        // 게시글 저장
        return jpaRepositoryV2.save(post);
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
        List<Tag> tags = updateTags(postUpdateDto.getTags());
        post.setTags(tags);

        // 게시글 저장
        return jpaRepositoryV2.save(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        return jpaRepositoryV2.findById(id);
    }

    @Override
    public List<Post> findPosts(PostSearchCond cond) {
        return postQueryRepository.findAll(cond);
    }

    /**
     * 태그 생성 로직을 분리하여 재사용 가능하도록 처리
     * @param tagNames 태그 이름 Set
     * @return 태그 리스트
     */
    private List<Tag> createTags(Set<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = tagJpaRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagJpaRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    /**
     * 태그 업데이트 로직
     * @param tagsToUpdate 업데이트할 태그 리스트
     * @return 업데이트된 태그 리스트
     */
    private List<Tag> updateTags(List<Tag> tagsToUpdate) {
        List<Tag> updatedTags = new ArrayList<>();
        for (Tag tag : tagsToUpdate) {
            Tag existingTag = tagJpaRepository.findByName(tag.getName())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tag.getName());
                        return tagJpaRepository.save(newTag);
                    });
            updatedTags.add(existingTag);
        }
        return updatedTags;
    }

    /**
     * 제목 중복 여부를 확인하는 메서드
     * @param title 확인할 제목
     * @return 중복 여부
     */
    public boolean isTitleDuplicate(String title) {
        return jpaRepositoryV2.existsByTitle(title);
    }

    /**
     * 태그 중복 여부를 확인하는 메서드
     * @param tagNames 태그 이름 리스트
     * @return 중복 여부
     */
    public boolean hasDuplicateTags(List<String> tagNames) {
        Set<String> tagSet = new HashSet<>(tagNames);
        return tagSet.size() != tagNames.size();
    }
}

