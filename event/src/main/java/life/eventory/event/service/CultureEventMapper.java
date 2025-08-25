package life.eventory.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.ComponentFilter;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import jakarta.transaction.Transactional;
import life.eventory.event.dto.CultureApiRoot;
import life.eventory.event.dto.CultureData;
import life.eventory.event.dto.external.ConvertAddress;
import life.eventory.event.entity.Event;
import life.eventory.event.repository.EventRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class CultureEventMapper {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final GeoApiContext geoApiContext;

    private final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    public CultureEventMapper(
            EventRepository eventRepository,
            ObjectMapper objectMapper,
            GeoApiContext geoApiContext
    ) {
        this.eventRepository = eventRepository;
        this.objectMapper = objectMapper;
        this.geoApiContext = geoApiContext;
    }

    @Transactional
    public int importTop50FromResource(Long organizerId) {
        try {
            ClassPathResource res = new ClassPathResource("SoulCulture.json");
            if (!res.exists()) return 0;

            try (InputStream is = res.getInputStream()) {
                CultureApiRoot root = objectMapper.readValue(is, CultureApiRoot.class);

                var candidates = root.DATA().stream()
                        .filter(d -> d.strtdate() != null)
                        .sorted(Comparator.comparing(CultureData::strtdate))
                        .limit(50)
                        .map(d -> {
                            try { return toEvent(d, organizerId); }
                            catch (IOException | InterruptedException | ApiException e) { throw new RuntimeException(e); }
                        })
                        .filter(Objects::nonNull)
                        .toList();

                List<Event> toSave = new ArrayList<>();
                for (Event e : candidates) {
                    boolean exists = eventRepository.existsByNameAndStartTimeAndAddress(
                            e.getName(), e.getStartTime(), e.getAddress()
                    );
                    if (!exists) toSave.add(e);
                }
                eventRepository.saveAll(toSave);
                return toSave.size();
            }
        } catch (IOException e) {
            throw new RuntimeException("SoulCulture.json 읽기 실패", e);
        }
    }

    public Event toEvent(CultureData d, Long organizerId)
            throws IOException, InterruptedException, ApiException {

        LocalDateTime start = toDateTime(d.strtdate());
        LocalDateTime end   = toDateTime(d.end_date());

        // 주소 상세/좌표 변환 (성공 시 우선 적용)
        ConvertAddress converted = convertToDetail(d.place(), d.guname());

        Double latitude  = converted != null ? converted.getLatitude()  : safeParseDouble(d.lot());
        Double longitude = converted != null ? converted.getLongitude() : safeParseDouble(d.lat());

        int entryFee = parseEntryFee(d.is_free(), d.use_fee());
        String desc  = buildDescription(d);

        return Event.builder()
                .organizerId(organizerId)
                .name(nullToEmpty(d.title()))
                .posterId(null)
                .description(desc.isBlank() ? "-" : desc)
                .startTime(start != null ? start : LocalDateTime.now(ZONE))
                .endTime(end != null ? end : (start != null ? start : LocalDateTime.now(ZONE)))
                .address(converted != null ? converted.getAddress() : nullToEmpty(d.place()))
                .latitude(latitude != null ? latitude : 0.0)
                .longitude(longitude != null ? longitude : 0.0)
                .entryFee(entryFee)
                .createTime(LocalDateTime.now(ZONE))
                .qrImage(null)
                .build();
    }

    private ConvertAddress convertToDetail(String place, String guname)
            throws IOException, InterruptedException, ApiException {

        GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, (nullToEmpty(place) + " " + nullToEmpty(guname)).trim())
                .components(ComponentFilter.administrativeArea("서울특별시"))
                .language("ko")
                .await();

        if (results.length == 0) return null;

        LatLng loc = results[0].geometry.location;
        return ConvertAddress.builder()
                .address(results[0].formattedAddress)
                .latitude(loc.lat)
                .longitude(loc.lng)
                .build();
    }

    private LocalDateTime toDateTime(Long millis) {
        if (millis == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZONE);
    }

    private Double safeParseDouble(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return null; }
    }

    private int parseEntryFee(String isFree, String feeText) {
        if ("무료".equals(isFree)) return 0;
        if (feeText != null && !feeText.isBlank()) {
            String digits = feeText.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                try { return Integer.parseInt(digits); } catch (NumberFormatException ignore) {}
            }
        }
        return 0;
    }

    private String buildDescription(CultureData d) {
        StringBuilder sb = new StringBuilder();
        append(sb, d.etc_desc());
        append(sb, label("프로그램", d.program()));
        append(sb, label("이용요금", d.use_fee()));
        append(sb, label("이용대상", d.use_trgt()));
        append(sb, label("자치구", d.guname()));
        append(sb, label("출연", d.player()));
        append(sb, label("테마", d.themecode()));
        append(sb, label("분류", d.codename()));
        append(sb, label("신청일", d.rgstdate()));
        append(sb, label("일정(원문)", d.date()));
        append(sb, label("홈페이지", d.org_link()));
        append(sb, label("문화포털", d.hmpg_addr()));
        append(sb, label("대표이미지", d.main_img()));
        return sb.toString().trim();
    }

    private void append(StringBuilder sb, String v) { if (v != null && !v.isBlank()) sb.append(v).append('\n'); }
    private String label(String k, String v) { return (v == null || v.isBlank()) ? null : k + ": " + v; }
    private String nullToEmpty(String s) { return s == null ? "" : s; }
}
