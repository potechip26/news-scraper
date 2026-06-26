package oop.search.infrastructure;

import oop.search.application.NewsProvider;
import oop.search.domain.NewsCategory;
import oop.search.domain.NewsPage;
import oop.search.domain.NewsResult;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//public class NaverNewsProvider extends AbstractHttpScraper {
public class NaverNewsProvider extends AbstractHttpClient implements NewsProvider {
	// 생성자 레벨에서 사용할 상수는 static
	private static final String NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";
	private final String clientId;
	private final String clientSecret;
	private final NewsCategory category;

	// clientId, clientSecret, category
	public NaverNewsProvider() {
		super(NEWS_API_URL);
		this.clientId = System.getenv("NAVER_CLIENT_ID");
		this.clientSecret = System.getenv("NAVER_CLIENT_SECRET");
		this.category = NewsCategory.valueOf(System.getenv("NEWS_CATEGORY"));
		// SIM, DATE -> 변환 (Enum - NewsCategory.SIM, NewsCategory.DATE)
		System.out.println("clientId = " + clientId.substring(0, 3) + "...");
		System.out.println("clientSecret = " + clientSecret.substring(0, 3) + "...");
		System.out.println("category = " + category);
	}

	@Override
	@SuppressWarnings("unchecked")
	public NewsPage fetchNews(String searchQuery, int display, int start) {
		String url = endpoint + "?query="
				+ URLEncoder.encode(searchQuery, StandardCharsets.UTF_8)
				+ "&display=" + display
				+ "&sort=" + category.getQueryValue()
				+ "&start=" + start;
		HttpRequest request = HttpRequest.newBuilder()
				.GET()
				.uri(URI.create(url))
				.header("X-Naver-Client-Id", clientId)
				.header("X-Naver-Client-Secret", clientSecret)
				.build();

		try {
			HttpResponse<String> response = httpClient.send(
					request,
					HttpResponse.BodyHandlers.ofString()
			);
			Map<String, Object> json = JsonParser.parseObject(response.body());

			int total = ((Number) json.get("total")).intValue();
			int resStart = ((Number) json.get("start")).intValue();
			int resDisplay = ((Number) json.get("display")).intValue();

			List<Object> rawItems = (List<Object>) json.get("items");
			List<NewsResult> results = new ArrayList<>();
			for (Object rawItem : rawItems) {
				Map<String, Object> item = (Map<String, Object>) rawItem;
				results.add(new NewsResult(
						(String) item.get("title"),
						(String) item.get("description"),
						(String) item.get("link"),
						(String) item.get("pubDate")
				));
			}
			return new NewsPage(results, total, resStart, resDisplay);
		} catch (Exception e) {
			e.printStackTrace();
			return new NewsPage(List.of(), 0, start, display);
		}
	}

	public static void main(String[] args) {
		NewsProvider provider = new NaverNewsProvider();
		NewsPage page = provider.fetchNews("프리티걸", 10, 1);
		System.out.println("총 %d건 중 %d~%d".formatted(
				page.total(), page.start(),
				Math.min(page.start() + page.display() - 1, page.total())));
		for (NewsResult newsItem : page.items()) {
			System.out.println("newsItem = " + newsItem);
		}
	}
}