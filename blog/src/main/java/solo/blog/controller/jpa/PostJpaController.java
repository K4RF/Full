package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import solo.blog.entity.database.Comment;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.entity.database.tx.Member;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.jpa.post.JpaRepositoryV2;
import solo.blog.service.jpa.CommentJpaService;
import solo.blog.service.jpa.post.PostJpaService;
import solo.blog.service.jpa.post.TagJpaService;
import solo.blog.service.jpa.tx.MemberJpaService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post/jpa/postList")
@RequiredArgsConstructor
public class PostJpaController {
    private final JpaRepositoryV2 postRepository;  // 변경된 부분
    private final PostJpaService postService;  // 변경된 부분
    private final MemberJpaService memberService;  // 변경된 부분
    private final CommentJpaService commentService;  // 변경된 부분
    private final TagJpaService tagService;  // 변경된 부분

    @GetMapping
    public String posts(@ModelAttribute("postSearch") PostSearchCond postSearch, Model model) {
        List<Post> posts = postService.findPosts(postSearch);
        model.addAttribute("posts", posts);
        return "post/search/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postService.findById(postId).get();
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post/search/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model) {
        Long memberId = 1L;
        Member member = memberService.findMember(memberId).orElseThrow();
        model.addAttribute("member", member);
        return "post/search/addForm";
    }

    @PostMapping("/add")
    public String addPostTag(@ModelAttribute Post post, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<Tag> tagSet = tagService.createTags(tagNames);  // 태그 생성 또는 검색
        post.setTags(tagSet);  // 포스트에 태그 추가

        Post savedPost = postService.save(post, tagNames);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/search/postList/{postId}";
    }

    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postRepository.findById(postId).get();  // 변경된 부분
        model.addAttribute("post", post);
        return "post/search/editForm";
    }

    @PostMapping("/{postId}/edit")
    public String editTag(@PathVariable Long postId, @ModelAttribute PostUpdateDto updateParam, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        postService.update(postId, updateParam);
        return "redirect:/post/search/postList/{postId}";
    }
}