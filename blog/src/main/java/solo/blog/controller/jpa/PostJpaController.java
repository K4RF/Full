package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
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
        List<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // List<String>을 Set<String>으로 변환
        Set<String> tagNamesSet = new HashSet<>(tagNames);

        // 태그 생성 또는 검색
        List<Tag> tagList = tagJpaService.createTags(tagNamesSet).stream().collect(Collectors.toList());

        post.setTags(tagList);  // 포스트에 태그 추가

        Post savedPost = postJpaServiceV2.save(post, tagNamesSet);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/jpa/postList/{postId}";
    }

    // 게시글 수정 폼 (GET)
    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow(() -> new NoSuchElementException("Post not found"));
        model.addAttribute("post", post);
        return "post/jpa/editForm";
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId,
                           @Validated @ModelAttribute PostUpdateDto postUpdateDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        // 1. BindingResult에 에러가 있는지 확인
        if (bindingResult.hasErrors()) {
            return "post/jpa/editForm";
        }

        // 2. 태그를 ','로 분리하고 중복 여부 확인
        List<String> tagNames = Arrays.stream(postUpdateDto.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining(","))
                        .split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        Set<String> tagNamesSet = new HashSet<>(tagNames);

        if (tagNames.size() != tagNamesSet.size()) {
            // 3. 중복된 태그가 발견되면 에러 메시지 바인딩
            bindingResult.rejectValue("tags", "duplicate", "중복된 태그가 있습니다.");
            model.addAttribute("post", postJpaServiceV2.findById(postId).get());
            return "post/jpa/editForm"; // 폼에 다시 돌아가 에러 메시지를 보여줌
        }

        try {
            // 4. 중복이 없을 경우 게시글 수정 진행
            Post updatedPost = postJpaServiceV2.update(postId, postUpdateDto);
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/post/jpa/postList/" + updatedPost.getId();
        } catch (Exception e) {
            log.error("Error occurred during post update", e);
            bindingResult.reject("saveFail", "게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "post/jpa/editForm";
        }
    }
}
