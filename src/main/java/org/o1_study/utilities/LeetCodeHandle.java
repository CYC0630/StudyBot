package org.o1_study.utilities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LeetCodeHandle
{
	private static final Logger logger = LoggerFactory.getLogger(LeetCodeHandle.class);

	public static LeetCodeHandle instance = new LeetCodeHandle();

	private LeetCodeHandle() {}

	public void newDay()
	{
		HttpURLConnection conn;

		try
		{
			URL url = new URI("https://leetcode.com/graphql").toURL();
			conn = (HttpURLConnection) url.openConnection();

			//設定請求方法與標頭
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			//寫入請求
			try (OutputStream os = conn.getOutputStream())
			{
				byte[] input = "{\"query\": \"query {activeDailyCodingChallengeQuestion {link question {title questionId difficulty}}}\"}".getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}
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

		QuestionData response = JsonHandle.instance.readResponse(responseString.toString());
		createPost(response.questionId + ". " + response.title, "https://leetcode.com/" + response.link, response.difficulty);
	}

	private void createPost(String title, String link, String difficulty)
	{
		Guild guild = Main.jda.getGuildById(Constants.GUILD_ID);
		if (guild == null) //獲取不到群組
			return;

		ForumChannel channel = guild.getForumChannelById(Constants.LEET_CODE_CHANNEL_ID);
		if (channel == null) //獲取不到頻道
			return;

		ForumTag tag = switch (difficulty) //難易度
		{
			case "Easy" -> channel.getAvailableTagById(Constants.EASY_TAG_ID);
			case "Medium" -> channel.getAvailableTagById(Constants.MEDIUM_TAG_ID);
			default -> channel.getAvailableTagById(Constants.HARD_TAG_ID); //Hard
		};

		channel.createForumPost(title, MessageCreateData.fromContent(link)) //新增貼文
				.setTags(tag) //設定tag
				.queue();
	}

	record QuestionData(String questionId, String title, String link, String difficulty) {}
}