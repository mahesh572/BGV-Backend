package com.org.bgv.vendor.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.ActivityType;
import com.org.bgv.service.ActivityFactory;
import com.org.bgv.service.ActivityTimelineService;
import com.org.bgv.vendor.dto.CreateVerificationExecutionNoteRequest;
import com.org.bgv.vendor.dto.VerificationExecutionNoteDto;
import com.org.bgv.vendor.entity.VerificationExecutionNote;
import com.org.bgv.vendor.entity.VerificationMethodExecution;
import com.org.bgv.vendor.repository.VerificationExecutionNoteRepository;
import com.org.bgv.vendor.repository.VerificationMethodExecutionRepository;
import com.org.bgv.vendor.verification.methods.service.VerificationContext;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class VerificationExecutionNoteService {

    private final VerificationExecutionNoteRepository noteRepository;
    private final VerificationMethodExecutionRepository executionRepository;
    private final VerificationContextUtil verificationContextUtil;
    private final ActivityTimelineService activityTimelineService;

    public VerificationExecutionNoteDto addNote(
            CreateVerificationExecutionNoteRequest request) {

        VerificationMethodExecution execution =
                executionRepository.findById(request.getExecutionId())
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Execution not found"));

        VerificationCaseCheck check =
                execution.getVerificationCheck();

        VerificationExecutionNote note =
                VerificationExecutionNote.builder()
                        .caseId(check.getVerificationCase().getCaseId())
                        .checkId(check.getCaseCheckId())
                        .execution(execution)
                        .objectId(execution.getObjectId())
                        .note(request.getNote())
                        //.type(request.getType())
                        .createdBy(SecurityUtils.getCurrentUserId())
                        .createdByRole("VENDOR")
                        .createdAt(LocalDateTime.now())
                        .build();

        note = noteRepository.save(note);

        VerificationContext context =
                verificationContextUtil.build(
                        check.getCaseCheckId(),
                        execution.getObjectId(),
                        CheckCategoryEnum.fromName(check.getCategory().getName()).name());

        activityTimelineService.log(
                ActivityFactory.create(
                        check.getVerificationCase().getCaseId(),
                        check.getCaseCheckId(),
                        execution.getExecutionId(),
                        null,
                        note.getNoteId(),
                        ActivityType.NOTE_ADDED,
                        "Note added",
                        request.getNote(),
                        SecurityUtils.getCurrentUserId(),
                        "VENDOR",
                        null,
                        null,
                        null,
                        context.getCandidate()
                )
        );

        return toDto(note);
    }

    @Transactional(readOnly = true)
    public List<VerificationExecutionNoteDto> getExecutionNotes(
            Long executionId) {

        log.info("Fetching notes for executionId={}", executionId);

        List<VerificationExecutionNoteDto> notes =
                noteRepository
                        .findByExecutionExecutionIdOrderByCreatedAtDesc(executionId)
                        .stream()
                        .map(this::toDto)
                        .toList();

        log.info(
                "Retrieved {} notes for executionId={}",
                notes.size(),
                executionId
        );

        return notes;
    }

    @Transactional(readOnly = true)
    public List<VerificationExecutionNoteDto> getCheckNotes(
            Long checkId) {

        return noteRepository
                .findByCheckIdOrderByCreatedAtDesc(checkId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VerificationExecutionNoteDto> getCaseNotes(
            Long caseId) {

        return noteRepository
                .findByCaseIdOrderByCreatedAtDesc(caseId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void deleteNote(Long noteId) {

        VerificationExecutionNote note =
                noteRepository.findById(noteId)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Note not found"));

        noteRepository.delete(note);
    }

    private VerificationExecutionNoteDto toDto(
            VerificationExecutionNote note) {

        return VerificationExecutionNoteDto.builder()
                .noteId(note.getNoteId())
                .caseId(note.getCaseId())
                .checkId(note.getCheckId())
                .executionId(note.getExecution().getExecutionId())
                .objectId(note.getObjectId())
                .note(note.getNote())
               // .type(note.getType())
                .createdBy(note.getCreatedBy())
                .createdByRole(note.getCreatedByRole())
                .createdAt(note.getCreatedAt())
                .build();
    }
}