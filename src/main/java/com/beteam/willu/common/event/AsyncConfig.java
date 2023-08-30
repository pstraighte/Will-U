package com.beteam.willu.common.event;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setQueueCapacity(50);
		taskExecutor.setThreadNamePrefix("async-thread-");
		taskExecutor.initialize();
		return taskExecutor;
	}
}
