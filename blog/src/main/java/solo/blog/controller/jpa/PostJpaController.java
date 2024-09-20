package solo.blog.controller.jpa;

import jakarta.servlet.http.HttpServletRequest;
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
import solo.blog.service.jpa.CommentJpaService;
import solo.blog.service.jpa.post.PostJpaServiceV2;
import solo.blog.service.jpa.tx.MemberJpaService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/post/jpa/postList")
@RequiredArgsConstructor
public class PostJpaController {
    private final PostJpaServiceV2 postJpaServiceV2;
    private final MemberJpaService memberJpaService;
    private final CommentJpaService commentJpaService;

    // 모든 요청에서 loginMember 모델을 추가
    @ModelAttribute
    public void addLoginMemberToModel(@SessionAttribute(value = "loginMember", required = false) Member loginMember, Model model) {
        model.addAttribute("loginMember", loginMember);
    }

    // 게시글 목록 조회에서 loginId 대신 authorName을 사용
    @GetMapping
    public String posts(@ModelAttribute("postSearch") PostSearchCond postSearch, Model model) {
        List<Post> posts = postJpaServiceV2.findPosts(postSearch);
        model.addAttribute("posts", posts);
        return "post/jpa/postList";
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        postJpaServiceV2.incrementViewCount(postId); // 조회수만 증가

        Post post = postJpaServiceV2.findById(postId).orElseThrow();
        List<Comment> comments = commentJpaService.getCommentsByPostId(postId);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);

        return "post/jpa/post";
    }

    @GetMapping("/add")
    public String addPostName(Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember, HttpServletRequest request) {
        // 로그인되지 않은 경우 로그인 페이지로 리다이렉트하며, 원래 URL을 함께 전달
        if (loginMember == null) {
            String redirectURL = request.getRequestURI();
            return "redirect:/login?redirectURL=" + redirectURL;
        }

        Long memberId = loginMember.getId();
        Member member = memberJpaService.findMember(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with ID: " + memberId));

        Post post = new Post();
        post.setLoginId(member.getLoginId());  // DB에 loginId는 저장되지만 화면에서는 표시 안 됨
        post.setAuthorName(member.getName());  // 작성자 이름 설정

        model.addAttribute("post", post);
        model.addAttribute("member", member);
        return "post/jpa/addForm";
    }

    // 게시글 등록 처리
    @PostMapping("/add")
    public String addPostTag(@ModelAttribute @Validated Post post, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 태그 처리
        List<String> tagNames = Arrays.stream(post.getTagsFormatted().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        // 태그 중복 검증
        if (postJpaServiceV2.hasDuplicateTags(tagNames)) {
            bindingResult.rejectValue("tagsFormatted", "duplicateTags", "중복된 태그가 있습니다.");
        }

        // 제목 중복 검증
        if (postJpaServiceV2.isTitleDuplicate(post.getTitle())) {
            bindingResult.rejectValue("title", "duplicateTitle", "이미 존재하는 제목입니다.");
        }

        // 검증에 실패한 경우 다시 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/jpa/addForm";
        }

        // 작성자 이름과 loginId 설정
        post.setAuthorName(loginMember.getName());
        post.setLoginId(loginMember.getLoginId());

        // 나머지 로직 처리 후 게시글 저장
        Post savedPost = postJpaServiceV2.save(post, new HashSet<>(tagNames));
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/jpa/postList/{postId}";
    }

    // 게시글 수정 폼 (GET)
    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model, HttpServletRequest request, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow();

        if (!post.getLoginId().equals(loginMember.getLoginId())) {
            model.addAttribute("errorMessage", "해당 게시글을 수정할 권한이 없습니다.");
            return "error/accessDenied";
        }

        PostUpdateDto postUpdateDto = new PostUpdateDto(post.getId(), post.getTitle(), post.getContent(), post.getLoginId(), post.getTags(), post.getAuthorName());
        model.addAttribute("post", postUpdateDto);
        return "post/jpa/editForm";
    }

    // 게시글 수정 처리 (POST)
    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId,
                           @Validated @ModelAttribute("post") PostUpdateDto postUpdateDto,
                           BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow();

        if (!post.getLoginId().equals(loginMember.getLoginId())) {
            model.addAttribute("errorMessage", "해당 게시글을 수정할 권한이 없습니다.");
            return "error/accessDenied";
        }

        // 태그 중복 확인
        List<String> tagNames = postUpdateDto.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        if (postJpaServiceV2.hasDuplicateTags(tagNames)) {
            bindingResult.rejectValue("tags", "duplicateTags", "중복된 태그가 있습니다.");
        }

        // 검증에 실패한 경우 다시 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postUpdateDto);
            return "post/jpa/editForm";
        }

        // 게시글 업데이트
        postJpaServiceV2.update(postId, postUpdateDto);
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
        return "redirect:/post/jpa/postList/" + postId;
    }

    // 포스트 삭제 처리
    @PostMapping("/{postId}/delete")
    public String deletePost(@PathVariable Long postId, RedirectAttributes redirectAttributes, Model model, @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow();

        if (!post.getLoginId().equals(loginMember.getLoginId())) {
            model.addAttribute("errorMessage", "해당 게시글을 삭제할 권한이 없습니다.");
            return "error/accessDenied";
        }

        postJpaServiceV2.delete(postId);

        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다.");
        return "redirect:/post/jpa/postList";
    }
}
