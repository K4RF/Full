package solo.blog.controller.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import solo.blog.entity.database.Comment;
import solo.blog.entity.database.tx.Member;
import solo.blog.service.jpa.CommentJpaService;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/post/jpa/postList/{postId}/comments")
@RequiredArgsConstructor
public class CommentJpaController {
    private final CommentJpaService commentJpaService;

    @GetMapping
    public String getComments(@PathVariable Long postId, Model model) {
        List<Comment> comments = commentJpaService.getCommentsByPostId(postId);
        model.addAttribute("comments", comments);
        return "post/jpa/post";
    }

    @PostMapping
    public String addComment(@PathVariable Long postId,
                             @RequestParam String comet,
                             @SessionAttribute(value = "loginMember", required = false) Member loginMember) {
        // 로그인 여부 확인
        if (loginMember == null) {
            // 로그인 페이지로 리다이렉트하고, 원래 페이지로 돌아오도록 설정
            return "redirect:/login?redirectURL=/post/jpa/postList/" + postId;
        }

        // 댓글 작성 (작성자 이름은 로그인한 사용자의 이름)
        commentJpaService.addComment(postId, loginMember.getName(), comet);
        return "redirect:/post/jpa/postList/" + postId;
    }
}
