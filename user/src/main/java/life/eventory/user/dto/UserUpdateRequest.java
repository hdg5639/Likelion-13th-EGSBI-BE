package life.eventory.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String nickname;
    private String profile;
}
