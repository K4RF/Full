package solo.blog;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.repository.v2.MemberRepository;
import solo.blog.service.v2.CommentService;
import solo.blog.service.v2.PostSearchService;
import solo.blog.service.v2.TagService;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInitJDBC {
    private final PostSearchService postService;  // PostSearchService 사용
    private final MemberRepository memberRepository;
    private final CommentService commentService;
    private final TagService tagService;

    //@PostConstruct
    public void init() {
        // 태그 생성
        Set<Tag> tagsForPost1 = tagService.createTags(Set.of("Java", "Spring"));
        Set<Tag> tagsForPost2 = tagService.createTags(Set.of("Backend", "Security"));

        // 포스트 생성 및 태그 설정
        Post post1 = new Post("qwer", "Test Title 1", "Test Content 1");
        postService.save(post1, Set.of("Java", "Spring")); // postService를 통해 저장
        log.info("Saved post1: {}", post1);

        Post post2 = new Post("asdf", "Test Title 2", "Test Content 2");
        postService.save(post2, Set.of("Backend", "Security")); // postService를 통해 저장
        log.info("Saved post2: {}", post2);

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

        // 저장된 포스트 확인
        List<Post> posts = postService.findPosts(new PostSearchCond(null, null));
        log.info("Stored posts: {}", posts);
    }
}
