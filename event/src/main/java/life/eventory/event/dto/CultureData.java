package life.eventory.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CultureData(
        @JsonProperty("org_name") String org_name,
        @JsonProperty("use_fee") String use_fee,
        @JsonProperty("player") String player,
        @JsonProperty("org_link") String org_link,
        @JsonProperty("guname") String guname,
        @JsonProperty("main_img") String main_img,
        @JsonProperty("themecode") String themecode,
        @JsonProperty("date") String date,
        @JsonProperty("etc_desc") String etc_desc,
        @JsonProperty("end_date") Long end_date,          // millis
        @JsonProperty("title") String title,
        @JsonProperty("ticket") String ticket,
        @JsonProperty("codename") String codename,
        @JsonProperty("use_trgt") String use_trgt,
        @JsonProperty("program") String program,
        @JsonProperty("lot") String lot,                  // 위도(Y)
        @JsonProperty("rgstdate") String rgstdate,
        @JsonProperty("strtdate") Long strtdate,          // millis
        @JsonProperty("place") String place,
        @JsonProperty("hmpg_addr") String hmpg_addr,
        @JsonProperty("lat") String lat,                  // 경도(X)
        @JsonProperty("is_free") String is_free
) {}