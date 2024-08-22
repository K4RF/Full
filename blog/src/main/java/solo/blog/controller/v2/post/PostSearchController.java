package solo.blog.controller.v2.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import solo.blog.entity.v2.Member;
import solo.blog.entity.v2.Post;
import solo.blog.entity.v2.Comment;
import solo.blog.entity.v2.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.model.PostUpdateDto;
import solo.blog.repository.v2.PostRepository;
import solo.blog.repository.v2.PostSearchRepository;
import solo.blog.service.v2.MemberService;
import solo.blog.service.v2.CommentService;
import solo.blog.service.v2.PostSearchService;
import solo.blog.service.v2.TagService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post/search/postList")
@RequiredArgsConstructor
public class PostSearchController {
    private final PostSearchRepository postRepository;
    private final PostSearchService postService;
    private final MemberService memberService;
    private final CommentService commentService;
    private final TagService tagService;

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
        Member member = memberService.findMember(memberId);
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
        Post post = postRepository.findById(postId).get();
        model.addAttribute("post", post);
        return "post/search/editForm";
    }

    @PostMapping("/{postId}/edit")
    public String editTag(@PathVariable Long postId, @ModelAttribute PostUpdateDto updateParam, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        postService.update(postId, updateParam);
        return "redirect:/post/search/postList/{postId}";
    }
}
