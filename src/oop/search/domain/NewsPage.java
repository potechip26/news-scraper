package oop.search.domain;

import java.util.List;

/// 페이징 정보를 포함한 뉴스 검색 결과
public record NewsPage(
		List<NewsResult> items,
		int total,
		int start,
		int display
) {
	public boolean hasNextPage() {
		return start + display <= total;
	}

	public boolean hasPrevPage() {
		return start > 1;
	}

	public int nextStart() {
		return start + display;
	}

	public int prevStart() {
		return Math.max(1, start - display);
	}
}
