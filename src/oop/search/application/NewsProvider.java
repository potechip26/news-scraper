package oop.search.application;

import oop.search.domain.NewsPage;

/**
 * 뉴스 검색을 위한 인터페이스
 */
public interface NewsProvider {
	/// searchQuery : 검색, display : 갯수 제한, start : 시작 위치 (1부터)
	NewsPage fetchNews(String searchQuery, int display, int start);
}
