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
        List<Tag> tags = createTags(tagNames);
        post.setTags(tags);
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

        // 태그 정보 처리
        List<Tag> tagList = new ArrayList<>();
        for (Tag tag : postUpdateDto.getTags()) {
            // 태그가 데이터베이스에 존재하는지 확인
            Tag existingTag = tagJpaRepository.findByName(tag.getName())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tag.getName());
                        return tagJpaRepository.save(newTag);
                    });
            tagList.add(existingTag);
        }

        // 게시글에 태그 설정 (List로 변환된 태그 사용)
        post.setTags(tagList);
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

    public boolean isTitleDuplicate(String title) {
        return jpaRepositoryV2.existsByTitle(title);
    }
}
