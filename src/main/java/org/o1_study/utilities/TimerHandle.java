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
	private final ScheduledFuture<?> everyHour;
	private int nowHour;

	private TimerHandle()
	{
		LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC+8")); //現在的時間
		nowHour = now.getHour();
		LocalDateTime startTimer = now.withHour(nowHour).withMinute(0).withSecond(0).plusHours(1); //目標時間

		everyHour = executorService.scheduleAtFixedRate(() -> //每小時執行一次
		{
			nowHour++;

			if (nowHour == 9) //雖然是8點時更新 但9點才po吧
				LeetCodeHandle.instance.newDay();
			else if (nowHour == 24)
				nowHour = 0;
		}, Duration.between(now, startTimer).toSeconds(), 60 * 60, TimeUnit.SECONDS);
	}

	public void stopTimer()
	{
		everyHour.cancel(true);
		executorService.shutdown();
	}
}