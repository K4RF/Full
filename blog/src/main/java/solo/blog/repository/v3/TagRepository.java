package solo.blog.repository.v3;

import org.springframework.stereotype.Repository;
import solo.blog.entity.v2.Tag;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TagRepository {
    private Map<Long, Tag> tagStore = new HashMap<>();
    private Long currentId = 0L;

    public Tag save(Tag tag) {
        tag.setId(++currentId);
        tagStore.put(tag.getId(), tag);
        return tag;
    }

    public Tag findByName(String name) {
        return tagStore.values().stream()
                .filter(tag -> tag.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
