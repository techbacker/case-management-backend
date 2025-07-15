package uk.gov.hmcts.reform.dev.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.services.CaseService;

@RestController
@RequestMapping("/api/cases")
public class CaseController {

    private final CaseService caseService;

    @Autowired
    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<Case> createCase(@Valid @RequestBody Case caseEntity) {
        if (caseEntity.getTitle() == null || caseEntity.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Case createdCase = caseService.createCase(caseEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCase);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Case> getCaseById(@PathVariable Long id) {
        Optional<Case> caseEntity = caseService.getCaseById(id);
        return caseEntity.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Case>> getAllCases() {
        List<Case> cases = caseService.getAllCases();
        return ResponseEntity.ok(cases);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Case> updateCaseStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        String status = statusUpdate.get("status");
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Case updatedCase = caseService.updateCaseStatus(id, status);

        if (updatedCase != null) {
            return ResponseEntity.ok(updatedCase);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        boolean deleted = caseService.deleteCase(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}