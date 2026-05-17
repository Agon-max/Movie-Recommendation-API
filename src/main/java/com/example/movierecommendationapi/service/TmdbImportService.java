package com.example.movierecommendationapi.service;

import com.example.movierecommendationapi.entity.ImportJob;
import com.example.movierecommendationapi.entity.enums.ImportJobStatus;
import com.example.movierecommendationapi.error.ResourceNotFound;
import com.example.movierecommendationapi.repository.ImportJobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TmdbImportService {

    private final ImportJobRepository jobRepository;
    private final TmdbService tmdbService;

    public TmdbImportService(
            ImportJobRepository jobRepository,
            TmdbService tmdbService
    ) {
        this.jobRepository = jobRepository;
        this.tmdbService = tmdbService;
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

            job.setStatus(ImportJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setFinishedAt(LocalDateTime.now());

        } finally {
            jobRepository.save(job);
        }
    }

    public Long startImport(int count){
        ImportJob job = new ImportJob();

        job.setStatus(ImportJobStatus.RUNNING);
        job.setTotalMovies(count);
        job.setProcessedMovies(0);
        job.setStartedAt(LocalDateTime.now());

        job = jobRepository.save(job);

        startImportJob(job.getId(), count);

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