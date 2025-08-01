package org.o1_study.events;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.o1_study.utilities.TimerHandle;

@SuppressWarnings("NullableProblems")
public class SessionEvent extends ListenerAdapter
{
	@Override
	public void onReady(ReadyEvent event)
	{
		TimerHandle.instance.startTimer();
	}

	@Override
	public void onShutdown(ShutdownEvent event)
	{
		TimerHandle.instance.stopTimer();
	}
}