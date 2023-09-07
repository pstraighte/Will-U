package com.beteam.willu.common.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobCleanupService {

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Transactional
    public void cleanupOldJobData() {

        List<JobInstance> jobInstances = jobExplorer.getJobInstances("job", 0, Integer.MAX_VALUE);

        for (JobInstance jobInstance : jobInstances) {
            List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);

            for (JobExecution jobExecution : jobExecutions) {
                System.out.println("jobExecution = " + jobExecution.getEndTime());
                if (jobExecution.getEndTime() != null) {
                    jobRepository.deleteJobExecution(jobExecution);
                }

            }
        }
    }

}
