package life.eventory.event.controller;

import life.eventory.event.controller.api.QrApi;
import life.eventory.event.service.QrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
public class QrController implements QrApi {
    private final QrService qrService;

    @Override
    public ResponseEntity<Long> joinQr(@RequestParam Long eventId)
            throws UnsupportedEncodingException {
        /*
        아래 url은 작업하다가 바뀔 수도 있음.
        프론트쪽에서 행사 참여 처리 페이지를 만들면 QR 스캔 시 해당 페이지로 행사 id 파라미터와 함께 리다이렉션하는 방식
        거기에서 받아온 행사 id와 해당 유저의 id를 토대로 서버에 행사 참여 처리 요청을 보내야함.
        */
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(qrService.generatePng(320, eventId));
    }

    public ResponseEntity<Long> getQrId(@RequestParam Long eventId) {
        return ResponseEntity.ok(qrService.findQrImageById(eventId));
    }
}
