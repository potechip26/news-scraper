package oop.search.application;

import oop.search.domain.NewsResult;

import java.util.List;

/// publisher와 provider를 중개해서 함께 사용 (합성)
public class NewsService {
	private final NewsProvider newsProvider;
	private final NewsPublisher newsPublisher;

	public NewsService(NewsProvider newsProvider, NewsPublisher newsPublisher) {
		this.newsProvider = newsProvider;
		this.newsPublisher = newsPublisher;
	}

	public List<NewsResult> search(String searchQuery, int limit) {
		List<NewsResult> results = newsProvider.fetchNews(searchQuery, limit);
		newsPublisher.publish(searchQuery, results); // 외부에 publish할 수도 있음.
		return results; // search를 통해서 자체적으로 결과값을 사용할 수도 있고.
	}

}