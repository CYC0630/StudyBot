package org.o1_study.events;

import lombok.NonNull;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandEvent extends ListenerAdapter
{
	@Override
	public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event)
	{
		if ("shutdown".equals(event.getName()))
		{
			event.reply("關閉中").setEphemeral(true).queue();
			event.getJDA().shutdown();
		}
	}
}