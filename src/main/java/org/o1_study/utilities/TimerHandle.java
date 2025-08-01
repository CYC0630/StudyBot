package org.o1_study.utilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerHandle
{
	public static TimerHandle instance = new TimerHandle();

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> everyHour;

	private TimerHandle() {}

	public void startTimer()
	{
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC")); //現在的時間
		LocalDateTime eight = now.withHour(8).withMinute(0).withSecond(1); //目標時間
		LocalDateTime startTimer = now.isAfter(eight) ? eight.plusDays(1) : eight;

		everyHour = executorService.scheduleAtFixedRate(() -> LeetCodeHandle.instance.newDay(),
				Duration.between(now, startTimer).toSeconds(), 60 * 60 * 24, TimeUnit.SECONDS);
	}

	public void stopTimer()
	{
		everyHour.cancel(true);
		executorService.shutdown();
	}
}