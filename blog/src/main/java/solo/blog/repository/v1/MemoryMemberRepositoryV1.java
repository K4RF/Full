package solo.blog.repository.v1;

import solo.blog.entity.v1.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryMemberRepositoryV1 implements MemberRepositoryV1 {
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    private static final MemoryMemberRepositoryV1 instance = new MemoryMemberRepositoryV1();
    public static MemoryMemberRepositoryV1 getInstance(){
        return instance;
    }
    @Override
    public Member save(Member member) {
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }

    @Override
    public List<Member> findAll(){
        return new ArrayList<>(store.values());
    }

    @Override
    public void clearStore() {
        store.clear();
    }
}
