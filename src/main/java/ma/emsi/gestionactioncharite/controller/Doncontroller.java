package ma.emsi.gestionactioncharite.controller;
import ma.emsi.gestionactioncharite.entity.Don;
import ma.emsi.gestionactioncharite.service.DonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dons")
@RequiredArgsConstructor

public class Doncontroller {
    private final DonService donService;

    @GetMapping
    public ResponseEntity<List<Don>> findAll() {
        return ResponseEntity.ok(donService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Don> findById(@PathVariable Long id) {
        return donService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Don>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(donService.findByUserId(userId));
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<Don>> findByAction(@PathVariable Long actionId) {
        return ResponseEntity.ok(donService.findByActionId(actionId));
    }

    @PostMapping
    public ResponseEntity<Don> save(@RequestBody Don don) {
        return ResponseEntity.status(HttpStatus.CREATED).body(donService.save(don));
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<Don> confirmer(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(donService.confirmer(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/rembourser")
    public ResponseEntity<Don> rembourser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(donService.rembourser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/echouer")
    public ResponseEntity<Don> echouer(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(donService.echouer(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        donService.delete(id);
        return ResponseEntity.noContent().build();
    }}
