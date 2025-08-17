package life.eventory.event.service;

public interface QrService {
    Long generatePng(int size, Long eventId);
    Long findQrImageById(Long eventId);
}
