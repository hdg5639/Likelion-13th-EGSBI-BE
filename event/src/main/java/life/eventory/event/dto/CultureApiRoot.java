package life.eventory.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CultureApiRoot(
        @JsonProperty("DESCRIPTION") Map<String, String> DESCRIPTION,
        @JsonProperty("DATA") List<CultureData> DATA
) {}