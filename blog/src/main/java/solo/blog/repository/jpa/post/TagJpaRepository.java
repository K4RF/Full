package solo.blog.repository.jpa.post;

import org.springframework.stereotype.Repository;
import solo.blog.entity.database.Tag;

import java.util.HashMap;
import java.util.Map;

@Repository
public class TagJpaRepository {
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
