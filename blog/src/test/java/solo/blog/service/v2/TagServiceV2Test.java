package solo.blog.service.v2;


import org.junit.jupiter.api.Test;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.TagRepositoryV2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TagServiceV2Test {

    @Test
    public void testCreateOrGetTag() {
        TagRepositoryV2 tagRepositoryV2 = new TagRepositoryV2();
        TagServiceV2 tagServiceV2 = new TagServiceV2(tagRepositoryV2);

        Tag tag1 = tagServiceV2.createOrGetTag("Spring");
        assertNotNull(tag1);
        assertEquals("Spring", tag1.getName());

        Tag tag2 = tagServiceV2.createOrGetTag("Spring");
        assertEquals(tag1.getId(), tag2.getId()); // 동일한 태그
    }
}
