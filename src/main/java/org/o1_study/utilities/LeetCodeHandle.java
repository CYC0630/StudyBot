package org.o1_study.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.o1_study.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class LeetCodeHandle
{
	private static final Logger logger = LoggerFactory.getLogger(LeetCodeHandle.class);

	public static LeetCodeHandle instance = new LeetCodeHandle();

	private final byte[] requestInput;

	private LeetCodeHandle()
	{
		requestInput = "{\"query\": \"query {activeDailyCodingChallengeQuestion {link question {title questionId difficulty}}}\"}".getBytes(StandardCharsets.UTF_8);
	}

	public void newDay()
	{
		HttpURLConnection conn;

		try
		{
			conn = createConnection();
		}
		catch (URISyntaxException | IOException e)
		{
			logger.error("query failed: ", e);
			return; //失敗就結束
		}

		StringBuilder responseString = new StringBuilder(); //用於接收資訊
		try
		{
			int responseCode = conn.getResponseCode();
			if (responseCode != 200 && responseCode != 201) //狀態失敗
			{
				logger.error("response failed: {} {}", responseCode, conn.getResponseMessage());
				return;
			}

			//讀取回傳結果
			try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8)))
			{
				while (true)
				{
					String responseLine = in.readLine();
					if (responseLine == null) //讀不到了
						break;
					responseString.append(responseLine);
				}
			}
			conn.disconnect();
		}
		catch (IOException e)
		{
			logger.error("read response failed: ", e);
			return;
		}

		Guild guild = Main.jda.getGuildById(Constants.GUILD_ID);
		if (guild == null) //獲取不到群組
		{
			logger.error("Can't get guild {}", Constants.GUILD_ID);
			return;
		}

		ForumChannel channel = guild.getForumChannelById(Constants.LEET_CODE_CHANNEL_ID);
		if (channel == null) //獲取不到頻道
		{
			logger.error("Can't get channel {}", Constants.LEET_CODE_CHANNEL_ID);
			return;
		}

		archivePost(channel);
		createPost(channel, JsonHandle.instance.readResponse(responseString.toString()));
	}

	private HttpURLConnection createConnection() throws URISyntaxException, IOException
	{
		HttpURLConnection conn = (HttpURLConnection) new URI("https://leetcode.com/graphql").toURL().openConnection();

		//設定請求方法與標頭
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		//寫入請求
		try (OutputStream os = conn.getOutputStream())
		{
			os.write(requestInput, 0, requestInput.length);
		}

		return conn;
	}

	private void archivePost(ForumChannel channel)
	{
		for (ThreadChannel thread : channel.getThreadChannels())
			if (!thread.isArchived())
				thread.getManager().setArchived(true).queue();
	}

	private void createPost(ForumChannel channel, QuestionData response)
	{
		String title = response.questionId + ". " + response.title + "https://leetcode.com" + response.link;

		ForumTag tag = switch (response.difficulty) //難易度
		{
			case "Easy" -> channel.getAvailableTagById(Constants.EASY_TAG_ID);
			case "Medium" -> channel.getAvailableTagById(Constants.MEDIUM_TAG_ID);
			default -> channel.getAvailableTagById(Constants.HARD_TAG_ID); //Hard
		};

		logger.info("Created post {}", title);

		channel.createForumPost(title, MessageCreateData.fromContent(response.link)) //新增貼文
				.setTags(tag) //設定tag
				.queue();
	}

	record QuestionData(String questionId, String title, String link, String difficulty) {}
}