package solo.blog;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.PostSearchCond;
import solo.blog.repository.jpa.tx.MemberJpaRepository;
import solo.blog.service.jpa.CommentJpaService;
import solo.blog.service.jpa.post.PostJpaServiceV2;
import solo.blog.service.jpa.post.TagJpaService;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataInitJPA {
    private final PostJpaServiceV2 postJpaServiceV2;  // PostSearchService 사용
    private final MemberJpaRepository memberJpaRepository;
    private final CommentJpaService commentJpaService;
    private final TagJpaService tagJpaService;

    @PostConstruct
    public void init() {
        // 태그 생성
        Set<Tag> tagsForPost1 = tagJpaService.createTags(Set.of("Java", "Spring"));
        Set<Tag> tagsForPost2 = tagJpaService.createTags(Set.of("Backend", "Security"));

        // 포스트 생성 및 태그 설정
        Post post1 = new Post("qwer", "Test Title 1", "Test Content 1");
        postJpaServiceV2.save(post1, Set.of("Java", "Spring")); // postService를 통해 저장
        log.info("Saved post1: {}", post1);

        Post post2 = new Post("asdf", "Test Title 2", "Test Content 2");
        postJpaServiceV2.save(post2, Set.of("Backend", "Security")); // postService를 통해 저장
        log.info("Saved post2: {}", post2);

        // 댓글 추가
        commentJpaService.addComment(post1.getId(), "Alice", "Great post, thanks for sharing!");
        commentJpaService.addComment(post1.getId(), "Bob", "Interesting read, looking forward to more!");
        commentJpaService.addComment(post2.getId(), "Charlie", "Helpful information!");

        // 회원 추가
        Member member = new Member();
        member.setLoginId("tester");
        member.setPassword("1234");
        member.setName("테스터");
        memberJpaRepository.save(member);

        // 저장된 포스트 확인
        List<Post> posts = postJpaServiceV2.findPosts(new PostSearchCond(null, null));
        log.info("Stored posts: {}", posts);
    }
}
