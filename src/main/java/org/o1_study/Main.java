package org.o1_study;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.o1_study.events.CommandEvent;
import org.o1_study.events.SessionEvent;

public class Main
{
	public static JDA jda;

	public static void main(String[] args) throws InterruptedException
	{
		jda = JDABuilder.createDefault(args[0])
				.addEventListeners(new SessionEvent(), new CommandEvent()) //新增事件聆聽
				.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS) //機器人可讀取訊息和查看伺服器成員
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.build();

		jda.updateCommands()
			.addCommands(Commands.slash("shutdown", "Shutdown").setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)))
			.queue();

		jda.awaitReady();
	}
}