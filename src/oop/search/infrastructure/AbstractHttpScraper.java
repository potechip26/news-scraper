package oop.search.infrastructure;

import oop.search.application.NewsProvider;

import java.net.http.HttpClient;

// abstract -> 구현 책임을 미룸
public abstract class AbstractHttpScraper implements NewsProvider {
	protected final HttpClient httpClient = HttpClient.newHttpClient();

	protected final String endpoint; // 생성자 주입될 예정

	protected AbstractHttpScraper(String endpoint) {
		this.endpoint = endpoint;
	}
}