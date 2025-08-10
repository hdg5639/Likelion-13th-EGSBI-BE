package life.eventory.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLocationRequest {
    private String email;
    private Double latitude;
    private Double longitude;
    private String address;
}
