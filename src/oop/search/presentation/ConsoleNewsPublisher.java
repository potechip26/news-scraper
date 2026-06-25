package oop.search.presentation;

import oop.search.application.NewsPublisher;
import oop.search.domain.NewsResult;

import java.util.List;

public class ConsoleNewsPublisher implements NewsPublisher {
	@Override
	public void publish(String topic, List<NewsResult> newsResults) {
//        System.out.println("뉴스 주제 : " + topic);
		System.out.println("뉴스 주제 : %s".formatted(topic));
		for (NewsResult newsResult : newsResults) {
//            System.out.println(newsResult);
			System.out.println("=".repeat(16));
			String output = """
                    제목 : %s
                    링크 : %s
                    설명 : %s
                    발행일자 : %s
                    """.formatted(newsResult.title(),
							newsResult.url(),
							newsResult.description(),
							newsResult.pubDate())
					.trim(); // 앞뒤의 공백이나 줄바꿈을 제거
			System.out.println(output);
		}
	}

	public static void main(String[] args) {
		NewsPublisher cnp = new ConsoleNewsPublisher();
		cnp.publish("창억떡", List.of(new NewsResult(
				"창억떡 맛있다",
				"창억떡 먹어봤니?",
				"https://naver.com",
				"2026.12.32"
		), new NewsResult(
				"창억떡 무봤나",
				"창억떡 직이네...",
				"https://naver2.com",
				"2026.12.33"
		)));
	}
}