package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.TagRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createOrGetTag(String name) {
        Tag tag = tagRepository.findByName(name);
        if (tag == null) {
            tag = new Tag(name);
            tagRepository.save(tag);
        }
        return tag;
    }

    public Set<Tag> createTags(Set<String> tagNames) {
        return tagNames.stream()
                .map(this::createOrGetTag)
                .collect(Collectors.toSet());
    }

    public Set<Tag> createTagsFromInput(String tagsInput) {
        // 쉼표로 구분된 태그 문자열을 Set<Tag>로 변환
        Set<String> tagNames = Arrays.stream(tagsInput.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = createOrGetTag(name);  // 이미 있는 메서드로 태그 생성/검색
            tags.add(tag);
        }
        return tags;
    }
}
