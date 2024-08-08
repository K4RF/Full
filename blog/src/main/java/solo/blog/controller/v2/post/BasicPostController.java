package solo.blog.controller.v2.post;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import solo.blog.entity.v2.Post;
import solo.blog.repository.v2.PostRepository;

import java.util.List;

@Controller
@RequestMapping("/post/postList")
@RequiredArgsConstructor
public class BasicPostController {
    private final PostRepository postRepository;

    @GetMapping
    public String post(Model model) {
        List<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);
        return "/post/basic/postList";
    }

    @PostConstruct
    public void init() {
        postRepository.save(new Post("qwer", "test title1", "test content1"));
        postRepository.save(new Post("asdf", "test title2", "test content2"));

    }
}
