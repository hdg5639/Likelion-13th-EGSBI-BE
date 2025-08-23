package life.eventory.event.dto;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartFileResource extends InputStreamResource {

    private final MultipartFile file;

    public MultipartFileResource(MultipartFile file) throws IOException {
        super(file.getInputStream());
        this.file = file;
    }

    @Override
    public String getFilename() {
        return file.getOriginalFilename();
    }

    public long contentLength() throws IOException {
        return file.getSize();
    }
}