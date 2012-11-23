package edu.buffalo.cse.cse605.project2;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class FuturableUtil implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private AsyncTaskExecutor defaultExecutor;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public void setDefaultExecutor(AsyncTaskExecutor executor) {
		defaultExecutor = executor;
	}

	public AsyncTaskExecutor getExecutor(String executorName) {

		AsyncTaskExecutor executor;

		if (executorName.equals(FuturableConstants.DEFAULT_TASK_EXECUTOR)) {
			executor = defaultExecutor;
		} else {
			executor = applicationContext.getBean(executorName, ThreadPoolTaskExecutor.class);
		}
		return executor;
	}

}
