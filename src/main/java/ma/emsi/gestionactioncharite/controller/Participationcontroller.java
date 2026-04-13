package ma.emsi.gestionactioncharite.controller;

import ma.emsi.gestionactioncharite.entity.Participation;
import ma.emsi.gestionactioncharite.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participations")
@RequiredArgsConstructor

public class Participationcontroller {
    private final ParticipationService participationService;

    @GetMapping
    public ResponseEntity<List<Participation>> findAll() {
        return ResponseEntity.ok(participationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participation> findById(@PathVariable Long id) {
        return participationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Participation>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(participationService.findByUserId(userId));
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<Participation>> findByAction(@PathVariable Long actionId) {
        return ResponseEntity.ok(participationService.findByActionId(actionId));
    }

    @PostMapping
    public ResponseEntity<?> inscrire(@RequestBody Participation participation) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(participationService.inscrire(participation));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/presence")
    public ResponseEntity<Participation> marquerPresence(
            @PathVariable Long id,
            @RequestParam boolean present) {
        try {
            return ResponseEntity.ok(participationService.marquerPresence(id, present));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
