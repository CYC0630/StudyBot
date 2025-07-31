package org.o1_study.events;

import lombok.NonNull;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.o1_study.utilities.TimerHandle;

public class SessionEvent extends ListenerAdapter
{
	@Override
	public void onReady(@NonNull ReadyEvent event)
	{
		TimerHandle.instance.startTimer();
	}

	@Override
	public void onShutdown(@NonNull ShutdownEvent event)
	{
		TimerHandle.instance.stopTimer();
	}
}