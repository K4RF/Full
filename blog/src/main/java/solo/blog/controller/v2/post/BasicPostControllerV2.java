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
import solo.blog.repository.v2.PostRepositoryV2;
import solo.blog.service.v2.MemberServiceV2;
import solo.blog.service.v2.CommentServiceV2;
import solo.blog.service.v2.TagServiceV2;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post/basic/postList")
@RequiredArgsConstructor
public class BasicPostControllerV2 {
    private final PostRepositoryV2 postRepositoryV2;
    private final MemberServiceV2 memberServiceV2;
    private final CommentServiceV2 commentServiceV2;
    private final TagServiceV2 tagServiceV2;

    @GetMapping
    public String post(Model model) {
        List<Post> posts = postRepositoryV2.findAll();
        model.addAttribute("posts", posts);
        return "post/basic/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postRepositoryV2.findById(postId);
        List<Comment> comments = commentServiceV2.getCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post/basic/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model) {
        Long memberId = 1L;
        Member member = memberServiceV2.findMember(memberId);
        model.addAttribute("member", member);
        return "post/basic/addPost";
    }

    //@PostMapping("/add")
    public String addPostRedirect(Post post, RedirectAttributes redirectAttributes) {
        Post savedPost = postRepositoryV2.save(post);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/basic/postList/{postId}";
    }
    @PostMapping("/add")
    public String addPostTag(Post post, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<Tag> tagSet = tagServiceV2.createTags(tagNames);  // 태그 생성 또는 검색
        post.setTags(tagSet);  // 포스트에 태그 추가

        Post savedPost = postRepositoryV2.save(post);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/basic/postList/{postId}";
    }

    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postRepositoryV2.findById(postId);
        model.addAttribute("post", post);
        return "post/basic/editPost";
    }

    //@PostMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId, @ModelAttribute Post post) {
        postRepositoryV2.update(postId, post);
        return "redirect:/post/basic/postList/{postId}";
    }
    @PostMapping("/{postId}/edit")
    public String editTag(@PathVariable Long postId, @ModelAttribute Post post, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        Post existingPost = postRepositoryV2.findById(postId);

        if (existingPost != null) {
            existingPost.setTitle(post.getTitle());
            existingPost.setContent(post.getContent());

            Set<Tag> updatedTags = tagServiceV2.createTagsFromInput(tags);
            existingPost.setTags(updatedTags);

            // 기존 포스트를 수정만 하도록 변경
            postRepositoryV2.update(postId, existingPost);

            redirectAttributes.addAttribute("postId", existingPost.getId());
        }

        return "redirect:/post/basic/postList/{postId}";
    }
}
