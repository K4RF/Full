package solo.blog.service.v2;


import org.junit.jupiter.api.Test;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.TagRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TagServiceTest {

    @Test
    public void testCreateOrGetTag() {
        TagRepository tagRepository = new TagRepository();
        TagService tagService = new TagService(tagRepository);

        Tag tag1 = tagService.createOrGetTag("Spring");
        assertNotNull(tag1);
        assertEquals("Spring", tag1.getName());

        Tag tag2 = tagService.createOrGetTag("Spring");
        assertEquals(tag1.getId(), tag2.getId()); // 동일한 태그
    }
}
