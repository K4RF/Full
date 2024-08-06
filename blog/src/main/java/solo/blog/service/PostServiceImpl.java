package solo.blog.service;

import solo.blog.entity.Member;
import solo.blog.entity.Post;
import solo.blog.repository.MemberRepository;
import solo.blog.repository.MemoryMemberRepository;

import java.time.LocalDateTime;

public class PostServiceImpl implements PostService{
    private final MemberRepository memberRepository;

    public PostServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Override
    public Post writePost(Long id, String title, String content, Long memberId) {
        Member member = memberRepository.findById(memberId);

        return new Post(id, title, content, memberId);
    }
}
