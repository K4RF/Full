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
    private final JpaRepositoryV2 postRepository;
    private final PostJpaServiceV2 postJpaServiceV2;
    private final MemberJpaService memberJpaService;
    private final CommentJpaService commentJpaService;
    private final TagJpaService tagJpaService;

    // 게시글 목록 조회
    @GetMapping
    public String posts(@ModelAttribute("postSearch") PostSearchCond postSearch, Model model) {
        List<Post> posts = postJpaServiceV2.findPosts(postSearch);
        model.addAttribute("posts", posts);
        return "post/jpa/postList";
    }

    // 특정 게시글 조회
    @GetMapping("/{postId}")
    public String post(@PathVariable long postId, Model model) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow();
        List<Comment> comments = commentJpaService.getCommentsByPostId(postId);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        return "post/jpa/post";
    }

    // 게시글 작성 폼
    @GetMapping("/add")
    public String addPostName(Model model, @SessionAttribute("loginMember") Member loginMember) {
        Long memberId = loginMember.getId();
        Member member = memberJpaService.findMember(memberId)
                .orElseThrow(() -> new NoSuchElementException("Member not found with ID: " + memberId));

        Post post = new Post();  // 빈 Post 객체를 생성하여 모델에 추가
        post.setLoginId(member.getLoginId());  // 로그인한 사용자의 ID를 Post 객체에 설정

        model.addAttribute("post", post);  // 빈 Post 객체를 뷰로 전달
        model.addAttribute("member", member);  // 작성자 정보도 뷰로 전달
        return "post/jpa/addForm";
    }

    // 게시글 등록 처리
    @PostMapping("/add")
    public String addPostTag(@ModelAttribute @Validated Post post, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        // 태그 처리
        List<String> tagNames = Arrays.stream(post.getTagsFormatted().split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        Set<String> tagNamesSet = new HashSet<>(tagNames);

        // 태그 중복 검증
        if (tagNames.size() != tagNamesSet.size()) {
            bindingResult.rejectValue("tagsFormatted", "duplicateTags", "중복된 태그가 있습니다.");
        }

        // 제목 중복 검증
        boolean isTitleDuplicate = postJpaServiceV2.isTitleDuplicate(post.getTitle());
        if (isTitleDuplicate) {
            bindingResult.rejectValue("title", "duplicateTitle", "이미 존재하는 제목입니다.");
        }

        // 검증에 실패한 경우 다시 폼으로
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", post);
            return "post/jpa/addForm";
        }

        // 태그를 생성하고 저장
        List<Tag> tagList = new ArrayList<>(tagJpaService.createTags(tagNamesSet));
        post.setTags(tagList);

        Post savedPost = postJpaServiceV2.save(post, tagNamesSet);
        redirectAttributes.addAttribute("postId", savedPost.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/post/jpa/postList/{postId}";
    }

    // 게시글 수정 폼 (GET)
    @GetMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        Post post = postJpaServiceV2.findById(postId).orElseThrow();
        PostUpdateDto postUpdateDto = new PostUpdateDto(post.getId(), post.getTitle(), post.getContent(), post.getLoginId(), post.getTags());

        // 템플릿에서 'post'로 접근하기 위해 'postUpdateDto'를 'post'로 변경
        model.addAttribute("post", postUpdateDto);
        return "post/jpa/editForm";
    }

    // 게시글 수정 처리 (POST)
    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable Long postId,
                           @Validated @ModelAttribute("post") PostUpdateDto postUpdateDto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postUpdateDto);
            return "post/jpa/editForm";
        }

        // 태그 중복 확인
        List<String> tagNames = postUpdateDto.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        Set<String> tagNamesSet = new HashSet<>(tagNames);

        if (tagNames.size() != tagNamesSet.size()) {
            bindingResult.rejectValue("tags", "duplicate", "중복된 태그가 있습니다.");
            model.addAttribute("tags", postUpdateDto.getTags());
            return "post/jpa/editForm";
        }

        postJpaServiceV2.update(postId, postUpdateDto);
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
        return "redirect:/post/jpa/postList/" + postId;
    }
}

