package oop.search.application;

import oop.search.domain.NewsPage;

/// publisher와 provider를 중개해서 함께 사용 (합성)
public class NewsService {
	private final NewsProvider newsProvider;
	private final NewsPublisher newsPublisher;

	public NewsService(NewsProvider newsProvider, NewsPublisher newsPublisher) {
		this.newsProvider = newsProvider;
		this.newsPublisher = newsPublisher;
	}

	public NewsPage search(String searchQuery, int display, int start) {
		NewsPage page = newsProvider.fetchNews(searchQuery, display, start);
		newsPublisher.publish(searchQuery, page.items()); // 외부에 publish할 수도 있음.
		return page; // search를 통해서 자체적으로 결과값을 사용할 수도 있고.
	}

	/// 첫 페이지 검색 (하위 호환)
	public NewsPage search(String searchQuery, int limit) {
		return search(searchQuery, limit, 1);
	}

}