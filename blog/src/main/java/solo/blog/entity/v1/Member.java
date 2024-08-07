package solo.blog.entity.v1;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import solo.blog.priory.Priory;

@Getter
@Setter
@Data
public class Member {
    private Long id;
    private String name;
    private Priory priory;

    public Member(){}

    public Member(String name, Priory priory) {
        this.name = name;
        this.priory = priory;
    }
}
