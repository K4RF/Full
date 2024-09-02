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
import solo.blog.service.jpa.post.PostJpaServiceV2;
import solo.blog.service.jpa.post.TagJpaService;
import solo.blog.service.jpa.tx.MemberJpaService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post/jpa/postList")
@RequiredArgsConstructor
public class PostJpaController {
    private final JpaRepositoryV2 postRepository;  // 변경된 부분
    private final PostJpaServiceV2 postJpaServiceV2;  // 변경된 부분
    private final MemberJpaService memberJpaService;  // 변경된 부분
    private final CommentJpaService commentJpaService;  // 변경된 부분
    private final TagJpaService tagJpaService;  // 변경된 부분

    @GetMapping
    public String posts(@ModelAttribute("postSearch") PostSearchCond postSearch, Model model) {
        List<Post> posts = postJpaServiceV2.findPosts(postSearch);
        model.addAttribute("posts", posts);
        return "post/jpa/postList";
    }

    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postJpaServiceV2.findById(postId).get();
        List<Comment> comments = commentJpaService.getCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post/jpa/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model, @SessionAttribute("loginMember") Member loginMember) {
        // 로그인된 사용자의 ID를 사용
        Long memberId = loginMember.getId();
        Member member = memberJpaService.findMember(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with ID: " + memberId));

        model.addAttribute("member", member);
        return "post/jpa/addForm";
    }

    @PostMapping("/add")
    public String addPostTag(@ModelAttribute Post post, @RequestParam String tags, RedirectAttributes redirectAttributes) {
        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        // 태그 생성 또는 검색
        Set<Tag> tagSet = tagJpaService.createTags(tagNames);

        post.setTags(tagSet);  // 포스트에 태그 추가

        Post savedPost = postJpaServiceV2.save(post, tagNames);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/jpa/postList/{postId}";
    }



    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postRepository.findById(postId).get();  // 변경된 부분
        model.addAttribute("post", post);
        return "post/jpa/editForm";
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, @ModelAttribute PostUpdateDto postUpdateDto, RedirectAttributes redirectAttributes) {
        Post updatedPost = postJpaServiceV2.update(postId, postUpdateDto);
        redirectAttributes.addFlashAttribute("message", "Post updated successfully");
        return "redirect:/post/jpa/postList/" + updatedPost.getId();  // 업데이트된 포스트 ID를 사용하여 리다이렉트
    }
}