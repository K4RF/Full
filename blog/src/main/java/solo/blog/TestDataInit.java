package solo.blog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.repository.v2.MemberRepository;
import solo.blog.repository.v2.PostRepository;
import solo.blog.service.v2.CommentService;
import solo.blog.service.v2.TagService;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final TagService tagService;  // 태그 서비스 추가

    //@PostConstruct
    public void init() {
        // 태그 생성
        Set<Tag> tagsForPost1 = tagService.createTags(Set.of("Java", "Spring"));
        Set<Tag> tagsForPost2 = tagService.createTags(Set.of("Backend", "Security"));

        // 포스트 생성 및 태그 설정
        Post post1 = new Post("qwer", "Test Title 1", "Test Content 1");
        post1.setTags(tagsForPost1);
        postRepository.save(post1);

        Post post2 = new Post("asdf", "Test Title 2", "Test Content 2");
        post2.setTags(tagsForPost2);
        postRepository.save(post2);

        // 댓글 추가
        commentService.addComment(post1.getId(), "Alice", "Great post, thanks for sharing!");
        commentService.addComment(post1.getId(), "Bob", "Interesting read, looking forward to more!");
        commentService.addComment(post2.getId(), "Charlie", "Helpful information!");

        // 회원 추가
        Member member = new Member();
        member.setLoginId("tester");
        member.setPassword("1234");
        member.setName("테스터");
        memberRepository.save(member);
    }
}