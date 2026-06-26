package oop.search.infrastructure;

import java.util.*;

/// 외부 라이브러리 없이 JSON을 파싱하는 경량 재귀 하강 파서
public class JsonParser {
	private final String json;
	private int pos;

	private JsonParser(String json) {
		this.json = json;
		this.pos = 0;
	}

	public static Map<String, Object> parseObject(String json) {
		Object result = new JsonParser(json.trim()).parseValue();
		if (result instanceof Map<?, ?> map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> typedMap = (Map<String, Object>) map;
			return typedMap;
		}
		throw new RuntimeException("JSON 최상위가 객체가 아닙니다");
	}

	private Object parseValue() {
		skipWhitespace();
		char c = peek();
		return switch (c) {
			case '"' -> parseString();
			case '{' -> parseMap();
			case '[' -> parseArray();
			case 't', 'f' -> parseBoolean();
			case 'n' -> parseNull();
			default -> parseNumber();
		};
	}

	private String parseString() {
		expect('"');
		StringBuilder sb = new StringBuilder();
		while (pos < json.length()) {
			char c = json.charAt(pos++);
			if (c == '\\') {
				char next = json.charAt(pos++);
				switch (next) {
					case '"', '\\', '/' -> sb.append(next);
					case 'n' -> sb.append('\n');
					case 't' -> sb.append('\t');
					case 'r' -> sb.append('\r');
					case 'b' -> sb.append('\b');
					case 'f' -> sb.append('\f');
					case 'u' -> {
						String hex = json.substring(pos, pos + 4);
						sb.append((char) Integer.parseInt(hex, 16));
						pos += 4;
					}
					default -> {
						sb.append('\\');
						sb.append(next);
					}
				}
			} else if (c == '"') {
				return sb.toString();
			} else {
				sb.append(c);
			}
		}
		throw new RuntimeException("문자열이 닫히지 않았습니다");
	}

	private Map<String, Object> parseMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		expect('{');
		skipWhitespace();
		if (peek() == '}') {
			pos++;
			return map;
		}
		while (true) {
			skipWhitespace();
			String key = parseString();
			skipWhitespace();
			expect(':');
			Object value = parseValue();
			map.put(key, value);
			skipWhitespace();
			if (peek() == ',') {
				pos++;
			} else {
				break;
			}
		}
		skipWhitespace();
		expect('}');
		return map;
	}

	private List<Object> parseArray() {
		List<Object> list = new ArrayList<>();
		expect('[');
		skipWhitespace();
		if (peek() == ']') {
			pos++;
			return list;
		}
		while (true) {
			list.add(parseValue());
			skipWhitespace();
			if (peek() == ',') {
				pos++;
			} else {
				break;
			}
		}
		skipWhitespace();
		expect(']');
		return list;
	}

	private Number parseNumber() {
		int start = pos;
		while (pos < json.length() && "0123456789+-.eE".indexOf(json.charAt(pos)) >= 0) {
			pos++;
		}
		String num = json.substring(start, pos);
		if (num.contains(".") || num.contains("e") || num.contains("E")) {
			return Double.parseDouble(num);
		}
		return Long.parseLong(num);
	}

	private Boolean parseBoolean() {
		if (json.startsWith("true", pos)) {
			pos += 4;
			return true;
		}
		if (json.startsWith("false", pos)) {
			pos += 5;
			return false;
		}
		throw new RuntimeException("불리언 파싱 실패: 위치 " + pos);
	}

	private Object parseNull() {
		if (json.startsWith("null", pos)) {
			pos += 4;
			return null;
		}
		throw new RuntimeException("null 파싱 실패: 위치 " + pos);
	}

	private void skipWhitespace() {
		while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
			pos++;
		}
	}

	private char peek() {
		return json.charAt(pos);
	}

	private void expect(char c) {
		if (json.charAt(pos) != c) {
			throw new RuntimeException("'" + c + "' 기대, '" + json.charAt(pos) + "' 발견 (위치: " + pos + ")");
		}
		pos++;
	}
}
