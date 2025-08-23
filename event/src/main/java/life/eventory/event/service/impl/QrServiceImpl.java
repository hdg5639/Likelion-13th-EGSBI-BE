package life.eventory.event.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import life.eventory.event.entity.Event;
import life.eventory.event.repository.EventRepository;
import life.eventory.event.service.CommunicationService;
import life.eventory.event.service.InMemoryMultipartFile;
import life.eventory.event.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {
    private final CommunicationService communicationService;
    private final EventRepository eventRepository;

    @Override
    public Long generatePng(int size, Long eventId) {
        String url = "https://eventory.life/join?e=" + URLEncoder.encode(eventId.toString(), StandardCharsets.UTF_8);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 행사가 없음"));

        try {
            int s = Math.max(128, Math.min(size, 1024)); // 안전 범위
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // 로고 없으면 M이면 충분
            hints.put(EncodeHintType.MARGIN, 1); // quiet zone

            BitMatrix matrix = new QRCodeWriter()
                    .encode(url, BarcodeFormat.QR_CODE, s, s, hints);

            BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, "PNG", baos);
                String filename = eventId+"-event-qr.png"; // 어짜피 저장할 때 UUID로 바뀜
                MultipartFile file = new InMemoryMultipartFile(
                        "file",                 // form field name
                        filename,               // original filename  (hasFile() 통과용)
                        "image/png",            // content type       (hasFile() 통과용)
                        baos.toByteArray()                     // content
                );

                Long qrId = communicationService.uploadPoster(file);
                event.setQrImage(qrId);
                eventRepository.save(event);
                return qrId;
            }
        } catch (Exception e) {
            throw new IllegalStateException("QR 생성 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public Long findQrImageById(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 행사가 없음");
        }
        if (!eventRepository.hasQrImage(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "발급된 QR 이미지가 없음");
        }
        return eventRepository.findQrImageById(eventId)
                .orElseThrow(() -> new RuntimeException("서버 오류"));
    }
}
