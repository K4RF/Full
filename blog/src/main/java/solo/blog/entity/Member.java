package solo.blog.entity;

import solo.blog.priory.Priory;

public class Member {
    private Long id;
    private String name;
    private Priory priory;

    public Member(Long id, String name, Priory priory) {
        this.id = id;
        this.name = name;
        this.priory = priory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Priory getPriory() {
        return priory;
    }

    public void setGrade(Priory priory) {
        this.priory = priory;
    }
}
