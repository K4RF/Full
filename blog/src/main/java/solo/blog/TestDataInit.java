package solo.blog;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.MemberRepository;
import solo.blog.repository.v2.PostRepository;
import solo.blog.service.v2.CommentService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentService commentService;

    @PostConstruct
    public void init() {
        // Initialize posts
        Post post1 = postRepository.save(new Post("qwer", "Test Title 1", "Test Content 1"));
        Post post2 = postRepository.save(new Post("asdf", "Test Title 2", "Test Content 2"));

        // Initialize comments for the first post
        commentService.addComment(post1.getId(), "Alice", "Great post, thanks for sharing!");
        commentService.addComment(post1.getId(), "Bob", "Interesting read, looking forward to more!");

        // Initialize comments for the second post
        commentService.addComment(post2.getId(), "Charlie", "Helpful information!");

        Member member = new Member();
        member.setLoginId("tester");
        member.setPassword("1234");

        member.setName("테스터");
        memberRepository.save(member);
    }
}
