package com.waben.option.job.quartz.support;

import com.waben.option.job.quartz.BaseJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public abstract class BaseJobSupporter extends QuartzJobBean implements BaseJob {
	
	protected transient String displayName;
	protected final boolean retryable ;
	
	public BaseJobSupporter(boolean retryable) {
		this.retryable = retryable;
	}
	
	protected String getDisplayName() {
		if (displayName == null) {
			StringBuilder buff = new StringBuilder();
			buff.append(ClassUtils.getSimpleName(getClass()));
			buff.append(" @" + Integer.toHexString(hashCode()));
			displayName = buff.toString();
		}
		return displayName;
	}

	@Override
	protected final void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		int count = 0;
		if (dataMap.containsKey("retryCount")) {
			count = dataMap.getIntValue("retryCount");
			// allow 5 retries
			if (count >= 5) {
				JobExecutionException e = new JobExecutionException("Retries exceeded");
				// unschedule it so that it doesn't run again
				e.setUnscheduleAllTriggers(true);
				throw e;
			}
		}
		
		try {
			dataMap.putAsString("retryCount", 0);
			doExecute(jobExecutionContext);
		} catch (Exception e) {
			log.error(new StringBuilder("Exception encountered during executeInternal()").toString(), e);
			if (retryable) {
				count++;
				dataMap.putAsString("retryCount", count);
				JobExecutionException e2 = new JobExecutionException(e);
				e2.setRefireImmediately(true);
				throw e2;
			}
		}
	}

	protected abstract void doExecute(JobExecutionContext jobExecutionContext) throws Exception;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}