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

        //  "job"이라는 작업 이름을 가진 모든 작업 인스턴스를 JobExplorer를 통해 가져와서 jobInstances 리스트에 저장
        List<JobInstance> jobInstances = jobExplorer.getJobInstances("job", 0, Integer.MAX_VALUE);

        for (JobInstance jobInstance : jobInstances) {
            // JobInstance에 대한 실행 이력들을 JobExplorer를 통해 검색하여 jobExecutions 리스트에 저장하는 것
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
