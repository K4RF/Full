package solo.blog.controller.v2.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solo.blog.entity.v2.Comment;
import solo.blog.service.v2.CommentService;
import solo.blog.service.v2.MemberService;

import java.util.List;

@Controller
@RequestMapping("/post/basic/postList/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;

    @GetMapping
    public String getComments(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        return "post/basic/post";
    }

    @PostMapping("/add")
    public String addComment(@PathVariable Long postId, @RequestParam String comet, Model model) {
        Long memberId = 1L; // 여기에서 세션 또는 토큰에서 실제 로그인된 사용자 ID를 가져와야 합니다.
        String author = memberService.findMember(memberId).getName();
        commentService.addComment(postId, author, comet);
        return "redirect:/post/basic/postList/{postId}";
    }
}
