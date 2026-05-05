package ma.emsi.gestionactioncharite.controller.api;

import lombok.RequiredArgsConstructor;
import ma.emsi.gestionactioncharite.entity.ActionCharite;
import ma.emsi.gestionactioncharite.service.ActionChariteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
public class ActionChariteApiController {

    private final ActionChariteService actionChariteService;

    @GetMapping
    public List<ActionCharite> listAll() {
        return actionChariteService.findAll();
    }

    @PostMapping
    public ResponseEntity<ActionCharite> create(@RequestBody ActionCharite action) {
        return ResponseEntity.ok(actionChariteService.save(action));
    }
}

