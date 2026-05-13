package ma.emsi.gestionactioncharite.controller.api;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.entity.ActionImage;
import ma.emsi.gestionactioncharite.repository.ActionChariteRepository;
import ma.emsi.gestionactioncharite.repository.ActionImageRepository;
import ma.emsi.gestionactioncharite.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionChariteApiController {

    private final ActionChariteRepository actionChariteRepository;
    private final ActionImageRepository actionImageRepository;
    private final FileStorageService fileStorageService;

    @PostMapping("/{id}/gallery")
    public ResponseEntity<?> uploadGallery(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        ActionCharite action = actionChariteRepository.findById(id).orElse(null);
        if (action == null) return ResponseEntity.notFound().build();

        List<ActionImage> uploadedImages = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                String url = fileStorageService.store(file, "actions/gallery/" + id);
                ActionImage image = ActionImage.builder()
                        .imageUrl(url)
                        .imageName(file.getOriginalFilename())
                        .actionCharite(action)
                        .build();
                uploadedImages.add(actionImageRepository.save(image));
            } catch (IOException e) {
                // Skip failed file or handle error
            }
        }
        return ResponseEntity.ok(uploadedImages);
    }

    @DeleteMapping("/gallery/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        ActionImage image = actionImageRepository.findById(imageId).orElse(null);
        if (image == null) return ResponseEntity.notFound().build();

        fileStorageService.delete(image.getImageUrl());
        actionImageRepository.delete(image);
        return ResponseEntity.ok(Map.of("message", "Image supprimée"));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<?> getImages(@PathVariable Long id) {
        ActionCharite action = actionChariteRepository.findById(id).orElse(null);
        if (action == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(action.getImages());
    }
}
