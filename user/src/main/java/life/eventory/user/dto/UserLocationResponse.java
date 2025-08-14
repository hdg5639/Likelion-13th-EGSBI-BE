package life.eventory.user.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLocationResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private String address;
}
