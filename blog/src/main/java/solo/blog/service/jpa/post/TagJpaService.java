package solo.blog.service.jpa.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Tag;
import solo.blog.repository.jpa.post.TagJpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagJpaService {
    private final TagJpaRepository tagRepository;

    public TagJpaService(TagJpaRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createOrGetTag(String name, Long postId) {
        Optional<Tag> optionalTag = tagRepository.findByNameAndPostId(name, postId);
        return optionalTag.orElseGet(() -> {
            Tag newTag = new Tag(name, postId);
            return tagRepository.save(newTag);
        });
    }


    @Transactional
    public Set<Tag> createTags(Set<String> tagNames, Long postId) {
        // 각 태그 이름에 대해 createOrGetTag 메서드를 호출하여 태그를 생성 또는 조회
        return tagNames.stream()
                .map(tagName -> createOrGetTag(tagName, postId))
                .collect(Collectors.toSet());
    }
}