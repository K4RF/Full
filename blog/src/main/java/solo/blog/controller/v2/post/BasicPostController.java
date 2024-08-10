package solo.blog.controller.v2.post;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.PostRepository;
import solo.blog.service.v2.MemberService;

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList")
@RequiredArgsConstructor
public class BasicPostController {
    private final PostRepository postRepository;
    private final MemberService memberService; // 멤버 정보를 가져오는 서비스 추가

    @GetMapping
    public String post(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "post/basic/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postRepository.findById(postId);
        model.addAttribute("post", post);
        return "post/basic/post";
    }

    //@GetMapping("/add")
    public String addPost() {
        return "post/basic/addPost";
    }

    @GetMapping("/add")
    public String addPostName(Model model) {
        // 세션에서 로그인한 사용자 ID 가져오기 (여기서는 가정하여 memberId = 1L로 사용)
        Long memberId = 1L; // 실제 구현에서는 세션이나 토큰에서 가져와야 합니다.

        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        return "post/basic/addPost";
    }

//    @PostMapping("/add")
    public String addPostV1(@RequestParam String title, @RequestParam String content, @RequestParam String loginId, Model model) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setLoginId(loginId);

        postRepository.save(post);

        model.addAttribute("post", post);

        return "post/basic/post";
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

    @PostConstruct
    public void init() {
        postRepository.save(new Post("qwer", "test title1", "test content1"));
        postRepository.save(new Post("asdf", "test title2", "test content2"));

    }
}
