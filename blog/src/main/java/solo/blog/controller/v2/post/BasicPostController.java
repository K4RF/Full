package solo.blog.controller.v2.post;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Comment; // Comment 엔티티 추가
import solo.blog.repository.v2.PostRepository;
import solo.blog.service.v2.MemberService;
import solo.blog.service.v2.CommentService; // CommentService 추가

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList")
@RequiredArgsConstructor
public class BasicPostController {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final CommentService commentService; // CommentService 주입

    @GetMapping
    public String post(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "post/basic/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postRepository.findById(postId);
        List<Comment> comments = commentService.getCommentsByPostId(postId); // 댓글 리스트 가져오기
        model.addAttribute("post", post);
        model.addAttribute("comments", comments); // 댓글 리스트 모델에 추가
        return "post/basic/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model) {
        Long memberId = 1L; // 실제 구현에서는 세션이나 토큰에서 가져와야 합니다.

        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        return "post/basic/addPost";
    }

    @PostMapping("/add")
    public String addPostRedirect(Post post, RedirectAttributes redirectAttributes) {
        Post savedPost = postRepository.save(post);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/basic/postList/{postId}";
    }

    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postRepository.findById(postId);
        model.addAttribute("post", post);
        return "post/basic/editPost";
    }

    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId, @ModelAttribute Post post) {
        postRepository.update(postId, post);
        return "redirect:/post/basic/postList/{postId}";
    }

    @PostMapping("/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestParam String name,
                             @RequestParam String content) {
        commentService.createComment(name, content, postId); // 댓글 생성
        return "redirect:/post/basic/postList/" + postId;
    }

    @PostConstruct
    public void init() {
        postRepository.save(new Post("qwer", "test title1", "test content1"));
        postRepository.save(new Post("asdf", "test title2", "test content2"));
    }
}
