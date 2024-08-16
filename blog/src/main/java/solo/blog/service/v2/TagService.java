package solo.blog.service.v2;

import org.springframework.stereotype.Service;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.TagRepository;

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
}
