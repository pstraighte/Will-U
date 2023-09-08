package com.beteam.willu.common.batch;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			System.out.println("작업 사이즈: " + jobExecutions.size());
			for (JobExecution jobExecution : jobExecutions) {

				if (jobExecution.getExitStatus().toString().equals("COMPLETED")) {
					System.out.println("jobExecution DELETION = " + jobExecution.getEndTime());
					jobRepository.deleteJobExecution(jobExecution);
					jobRepository.deleteJobInstance(jobInstance);
				}
			}
		}
	}

}
