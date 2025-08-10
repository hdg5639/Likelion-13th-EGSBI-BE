package life.eventory.activity.controller;

import life.eventory.activity.dto.HistoryDTO;
import life.eventory.activity.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;
//    private final EventService eventService;

    @PostMapping("/add") // 행사 상세 조회 시 히스토리 기록 남김
    public ResponseEntity<HistoryDTO> saveHistory(@RequestBody HistoryDTO history) {
        HistoryDTO historyDTO = historyService.recordView(history);
        return ResponseEntity.status(HttpStatus.CREATED).body(historyDTO);
    }
    @GetMapping("/list") // 사용자의 히스토리 목록 조회
    public ResponseEntity<Page<HistoryDTO>> getHistoryList(@RequestParam Long userId,@PageableDefault(sort = "viewedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<HistoryDTO> list = historyService.getHistoryList(userId, pageable);
        return ResponseEntity.ok(list);
    }

//    @GetMapping("/{eventId}")
//    public ResponseEntity<EventDTO> getEventInfo(@PathVariable Long eventId,
//                                                 @RequestParam Long userId) {
//        EventDTO event = eventService.getEventById(eventId); // 특정 id를 가진 행사정보 조회 후 event에 담기
//        // 히스토리 기록
//        HistoryDTO historyDTO = new HistoryDTO();
//        historyDTO.setUserId(userId);
//        historyDTO.setEventId(eventId);
//        historyService.recordView(historyDTO);
//        return ResponseEntity.ok(event);
//    }
}
