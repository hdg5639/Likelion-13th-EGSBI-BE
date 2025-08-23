package life.eventory.event.dto.external;

import lombok.*;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConvertAddress {
    private String address;
    private Double latitude;
    private Double longitude;
}
