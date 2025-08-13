package life.eventory.event.service;

import org.springframework.stereotype.Component;

@Component
public class TagNormalizer {
    // "#야시장 " -> "야시장" / 영문은 소문자 / 공백 제거 / 허용문자 제한 등
    public String normalize(String raw) {
        if (raw == null) return null;
        String t = java.text.Normalizer.normalize(raw, java.text.Normalizer.Form.NFKC).trim();
        if (t.startsWith("#")) t = t.substring(1);
        t = t.replaceAll("\\s+", "");                                // 공백 제거
        t = t.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}_-]", "");  // 허용 문자만
        if (t.isEmpty()) return null;
        t = t.toLowerCase();                                        // 영문 소문자 통일
        return t.length() > 60 ? t.substring(0, 60) : t;
    }
}
