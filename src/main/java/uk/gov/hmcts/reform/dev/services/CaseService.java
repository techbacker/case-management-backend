package uk.gov.hmcts.reform.dev.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.hmcts.reform.dev.models.Case;
import uk.gov.hmcts.reform.dev.repositories.CaseRepository;

@Service
public class CaseService {

    private final CaseRepository caseRepository;

    @Autowired
    public CaseService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    public Case createCase(Case caseEntity) {
        caseEntity.setCreatedDate(LocalDateTime.now());
        caseEntity.setUpdatedDate(LocalDateTime.now());
        return caseRepository.save(caseEntity);
    }

    public Optional<Case> getCaseById(Long id) {
        return caseRepository.findById(id);
    }

    public List<Case> getAllCases() {
        return caseRepository.findAll();
    }

    public Case updateCaseStatus(Long id, String status) {
        Optional<Case> caseOptional = caseRepository.findById(id);
        if (caseOptional.isPresent()) {
            Case caseEntity = caseOptional.get();
            caseEntity.setStatus(status);
            caseEntity.setUpdatedDate(LocalDateTime.now());
            return caseRepository.save(caseEntity);
        }
        return null;
    }

    public boolean deleteCase(Long id) {
        if (caseRepository.existsById(id)) {
            caseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}