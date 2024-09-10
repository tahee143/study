package hello.springmvc.basic;

import lombok.Data;

@Data // 롬복이 @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor 자동 적용해줌
public class HelloData {
    private String username;
    private int age;
}
