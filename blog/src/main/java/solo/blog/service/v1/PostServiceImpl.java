package solo.blog.service.v1;

import solo.blog.entity.v1.Member;
import solo.blog.entity.Post;
import solo.blog.repository.v1.MemberRepository;
import solo.blog.service.v1.PostService;

public class PostServiceImpl implements PostService {
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
