package solo.blog.controller.v2.post;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.PostRepository;

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList")
@RequiredArgsConstructor
public class BasicPostController {
    private final PostRepository postRepository;

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

    @GetMapping("/add")
    public String addPost() {
        return "post/basic/addPost";
    }

    @PostMapping("/add")
    public String addPostV1(@RequestParam String title, @RequestParam String content, @RequestParam String loginId, Model model) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setLoginId(loginId);

        postRepository.save(post);

        model.addAttribute("post", post);

        return "post/basic/post";
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
