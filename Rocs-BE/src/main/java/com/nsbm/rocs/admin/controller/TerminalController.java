package com.nsbm.rocs.admin.controller;

import com.nsbm.rocs.admin.service.TerminalService;
import com.nsbm.rocs.entity.main.Terminal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/terminals")
public class TerminalController {

    private final TerminalService terminalService;

    @Autowired
    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping
    public ResponseEntity<List<Terminal>> getAllTerminals() {
        return ResponseEntity.ok(terminalService.getAllTerminals());
    }

    @PostMapping
    public ResponseEntity<Terminal> addTerminal(@RequestBody Terminal terminal) {
        return ResponseEntity.status(201).body(terminalService.addTerminal(terminal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Terminal> updateTerminal(@PathVariable Long id, @RequestBody Terminal terminal) {
        return ResponseEntity.ok(terminalService.updateTerminal(id, terminal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerminal(@PathVariable Long id) {
        terminalService.deleteTerminal(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<Void> toggleTerminalStatus(@PathVariable Long id) {
        terminalService.toggleTerminalStatus(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Terminal> getTerminalById(@PathVariable Long id) {
        return ResponseEntity.ok(terminalService.getTerminalById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Terminal>> searchTerminals(@RequestParam("q") String query) {
        return ResponseEntity.ok(terminalService.searchTerminals(query));
    }
}
