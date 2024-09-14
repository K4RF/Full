package solo.blog.service.jpa.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solo.blog.entity.database.Post;
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
    public Tag createOrGetTag(String name, Post post) {
        return tagRepository.findByNameAndPostId(name, post.getId())
                .orElseGet(() -> {
                    Tag newTag = new Tag(name, post); // Post와 연결된 태그 생성
                    return tagRepository.save(newTag);
                });
    }

    @Transactional
    public Set<Tag> createTags(Set<String> tagNames, Post post) {
        return tagNames.stream()
                .map(tagName -> createOrGetTag(tagName, post)) // Post와 연관된 태그 생성
                .collect(Collectors.toSet());
    }

}