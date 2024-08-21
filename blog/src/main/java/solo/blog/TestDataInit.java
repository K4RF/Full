package solo.blog;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.MemberRepositoryV2;
import solo.blog.repository.v2.PostRepositoryV2;
import solo.blog.service.v2.CommentServiceV2;
import solo.blog.service.v2.TagServiceV2;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final PostRepositoryV2 postRepositoryV2;
    private final MemberRepositoryV2 memberRepositoryV2;
    private final CommentServiceV2 commentServiceV2;
    private final TagServiceV2 tagServiceV2;  // 태그 서비스 추가


    @PostConstruct
    public void init() {
        // 태그 생성
        Set<Tag> tagsForPost1 = tagServiceV2.createTags(Set.of("Java", "Spring"));
        Set<Tag> tagsForPost2 = tagServiceV2.createTags(Set.of("Backend", "Security"));

        // 포스트 생성 및 태그 설정
        Post post1 = new Post("qwer", "Test Title 1", "Test Content 1");
        post1.setTags(tagsForPost1);
        postRepositoryV2.save(post1);

        Post post2 = new Post("asdf", "Test Title 2", "Test Content 2");
        post2.setTags(tagsForPost2);
        postRepositoryV2.save(post2);

        // 댓글 추가
        commentServiceV2.addComment(post1.getId(), "Alice", "Great post, thanks for sharing!");
        commentServiceV2.addComment(post1.getId(), "Bob", "Interesting read, looking forward to more!");
        commentServiceV2.addComment(post2.getId(), "Charlie", "Helpful information!");

        // 회원 추가
        Member member = new Member();
        member.setLoginId("tester");
        member.setPassword("1234");
        member.setName("테스터");
        memberRepositoryV2.save(member);
    }
}