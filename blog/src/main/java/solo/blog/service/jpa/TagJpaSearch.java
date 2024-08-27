package solo.blog.service.jpa;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import solo.blog.entity.database.Post;
import solo.blog.entity.database.Tag;
import solo.blog.model.PostSearchCond;
import solo.blog.repository.jpa.PostJPARepository;
import solo.blog.repository.jpa.PostJpaRepositoryV1;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagJpaSearch {
    private final PostJPARepository jpaRepository;
    private final TagJpaService tagService;

    public TagJpaSearch(PostJPARepository jpaRepository, TagJpaService tagService) {
        this.jpaRepository = jpaRepository;
        this.tagService = tagService;
    }

    public List<Post> getPostsByTag(String tagName) {
        // 태그를 조회하거나 생성
        Tag tag = tagService.createOrGetTag(tagName);

        // PostSearchCond 객체를 사용하여 검색 조건을 설정 (제목이나 로그인 아이디 필터가 필요하지 않으면 null로 설정)
        PostSearchCond searchCond = new PostSearchCond();

        // findAll 메서드를 호출할 때 PostSearchCond 객체를 전달
        return jpaRepository.findAll(searchCond).stream()
                .filter(post -> post.getTags().contains(tag))
                .collect(Collectors.toList());
    }
}
