package solo.blog.controller.v2.post;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Comment;
import solo.blog.repository.v2.PostRepository;
import solo.blog.service.v2.MemberService;
import solo.blog.service.v2.CommentService;

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList")
@RequiredArgsConstructor
public class BasicPostController {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final CommentService commentService;

    @GetMapping
    public String post(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "post/basic/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postRepository.findById(postId);
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post/basic/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model) {
        Long memberId = 1L;
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
                             @RequestParam String author,
                             @RequestParam String comet) {
        commentService.addComment(postId, author, comet);
        return "redirect:/post/basic/postList/" + postId;
    }

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
    }
}
