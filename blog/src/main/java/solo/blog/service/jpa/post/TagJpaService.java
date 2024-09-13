package solo.blog.service.jpa.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Tag;
import solo.blog.repository.jpa.post.TagJpaRepository;

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
    public Tag createOrGetTag(String name) {
        // TagRepository에서 이름으로 태그를 조회
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    // 태그가 없으면 새로 생성하여 저장
                    Tag newTag = new Tag(name);
                    return tagRepository.save(newTag);
                });
    }

    
    @Transactional
    public Set<Tag> createTags(Set<String> tagNames) {
        // 각 태그 이름에 대해 createOrGetTag 메서드를 호출하여 태그를 생성 또는 조회
        return tagNames.stream()
                .map(this::createOrGetTag)
                .collect(Collectors.toSet());
    }


}