package oop.search.infrastructure;

import java.net.http.HttpClient;

// abstract -> 구현 책임을 미룸
//public abstract class AbstractHttpScraper implements NewsProvider {
public abstract class AbstractHttpClient {
	protected final HttpClient httpClient = HttpClient.newHttpClient();

	protected final String endpoint; // 생성자 주입될 예정

	protected AbstractHttpClient(String endpoint) {
		this.endpoint = endpoint;
	}
}