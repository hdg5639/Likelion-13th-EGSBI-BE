package life.eventory.event.dto.external;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DaeguEventDTO {
    private Long event_seq;
    private String event_gubun;
    private String subject;
    private LocalDate start_date;
    private LocalDate end_date;
    private String place;
    private String event_area;
    private String host;
    private String contact;
    private String pay_gubun;
    private String pay;
    private String homepage;
    private String content;
}
