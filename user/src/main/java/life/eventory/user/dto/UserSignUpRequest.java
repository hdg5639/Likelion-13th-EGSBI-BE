package life.eventory.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String nickname;
    private Long profile;
}
