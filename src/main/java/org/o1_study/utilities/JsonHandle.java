package org.o1_study.utilities;

import org.json.JSONObject;

public class JsonHandle
{
	public static JsonHandle instance = new JsonHandle();

	private final JSONObject dummy = new JSONObject();

	private JsonHandle() {}

	LeetCodeHandle.QuestionData readResponse(String s)
	{
		JSONObject response = new JSONObject(s);
		JSONObject data = response.optJSONObject("data", dummy).optJSONObject("activeDailyCodingChallengeQuestion", dummy);
		String link = data.optString("link", "");

		JSONObject question = data.optJSONObject("question", dummy);
		String title = question.optString("title", "");
		String difficulty = question.optString("difficulty", "");
		String questionId = question.optString("questionId", "");

		return new LeetCodeHandle.QuestionData(questionId, title, link, difficulty);
	}
}