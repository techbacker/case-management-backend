package uk.gov.hmcts.reform.dev.controllers;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.ok;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.hmcts.reform.dev.models.ExampleCase;

@RestController
public class CaseController {

    @GetMapping(value = "/get-example-case", produces = "application/json")
    public ResponseEntity<ExampleCase> getExampleCase() {
        return ok(new ExampleCase(1, "ABC12345", "Case Title", "Case Description", "Case Status", LocalDateTime.now()
        ));
    }
}
