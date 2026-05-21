package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.ImportJob;
import com.example.movierecommendationapi.entity.enums.ImportJobStatus;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.ImportJobRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TmdbImportService {

    private final ImportJobRepository jobRepository;
    private final TmdbService tmdbService;
    // Self-injection through the Spring proxy so @Async on startImportJob
    // actually fires when invoked from startImport (this.method() bypasses AOP).
    private final TmdbImportService self;

    public TmdbImportService(
            ImportJobRepository jobRepository,
            TmdbService tmdbService,
            @Lazy TmdbImportService self
    ) {
        this.jobRepository = jobRepository;
        this.tmdbService = tmdbService;
        this.self = self;
    }

    // -------------------------
    // START JOB
    // -------------------------
    @Async
    public void startImportJob(Long jobId, int movieCount) {

        ImportJob job = jobRepository.findById(jobId)
                .orElseThrow();

        try {

            job.setStatus(ImportJobStatus.RUNNING);
            job.setStartedAt(LocalDateTime.now());

            jobRepository.save(job);

            tmdbService.importExternalTMDBInfo(movieCount, job);

            job.setStatus(ImportJobStatus.DONE);
            job.setFinishedAt(LocalDateTime.now());

        } catch (Exception e) {

            e.printStackTrace();
            job.setStatus(ImportJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setFinishedAt(LocalDateTime.now());

        } finally {
            try {
                jobRepository.save(job);
            } catch (Exception persistError) {
                // Last resort: don't let a bad errorMessage payload mask the
                // original failure. Strip the message and try once more.
                persistError.printStackTrace();
                job.setErrorMessage("Import failed; details could not be persisted.");
                jobRepository.save(job);
            }
        }
    }

    public Long startImport(int count){
        ImportJob job = new ImportJob();

        job.setStatus(ImportJobStatus.RUNNING);
        job.setTotalMovies(count);
        job.setProcessedMovies(0);
        job.setStartedAt(LocalDateTime.now());

        job = jobRepository.save(job);

        self.startImportJob(job.getId(), count);

        return job.getId();
    }

    public ImportJob getJobById(Long jobId){
        Optional<ImportJob> job = jobRepository.findById(jobId);

        if(!job.isPresent()){
            throw new ResourceNotFound("Job not found!");
        }
        return job.get();
    }
}