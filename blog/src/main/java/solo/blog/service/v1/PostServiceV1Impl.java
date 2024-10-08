package solo.blog.service.v1;

import solo.blog.entity.v1.Member;
import solo.blog.entity.v1.PostV1;
import solo.blog.repository.v1.MemberRepositoryV1;

public class PostServiceV1Impl implements PostServiceV1 {
    private final MemberRepositoryV1 memberRepositoryV1;

    public PostServiceV1Impl(MemberRepositoryV1 memberRepositoryV1) {
        this.memberRepositoryV1 = memberRepositoryV1;
    }
    @Override
    public PostV1 writePost(Long id, String title, String content, Long memberId) {
        Member member = memberRepositoryV1.findById(memberId);

        return new PostV1(id, title, content, memberId);
    }
}
