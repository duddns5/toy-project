package com.kh.toy.common.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kh.toy.common.code.ErrorCode;
import com.kh.toy.common.exception.HandlableException;

public class HttpConnector {

	private static Gson gson = new Gson();

	public String get(String url) {

		String responseBody = "";

		try {
			HttpURLConnection conn = getConnection(url, "GET");
			responseBody = getResponseBody(conn);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR, e);
		}

		return responseBody;
	}

	public String get(String url, Map<String, String> headers) {
		String responseBody = "";

		try {
			HttpURLConnection conn = getConnection(url, "GET");
			setHeaders(headers, conn);
			responseBody = getResponseBody(conn);// 외부로 받은 http 헤더를 적용
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR, e);
		}

		return responseBody;
	}

	public JsonElement getAsJson(String url, Map<String, String> headers) {
		String responseBody = "";
		JsonElement datas = null;

		try {
			HttpURLConnection conn = getConnection(url, "GET");
			setHeaders(headers, conn);
			responseBody = getResponseBody(conn);// 외부로 받은 http 헤더를 적용
			datas = gson.fromJson(responseBody, JsonObject.class);

		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR, e);
		}

		return datas;
	}

	public String post(String url, Map<String, String> headers, String body) {

		String responseBody = "";

		try {
			HttpURLConnection conn = getConnection(url, "POST");
			setHeaders(headers, conn);
			setBody(body, conn);
			responseBody = getResponseBody(conn);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR, e);
		}

		return responseBody;

	}

	public String postAsJson(String url, Map<String, String> headers, String body) {

		String responseBody = "";
		JsonElement datas = null;
		try {
			HttpURLConnection conn = getConnection(url, "POST");
			setHeaders(headers, conn);
			setBody(body, conn);
			responseBody = getResponseBody(conn);
			datas = gson.fromJson(responseBody, JsonObject.class);
		} catch (IOException e) {
			throw new HandlableException(ErrorCode.HTTP_CONNECT_ERROR, e);
		}

		return responseBody;

	}
	
	public String urlEncodedForm(RequestParams requestParams) {

		String res = "";
		Map<String,String> params = requestParams.getParams();

		try {
			for (String key : params.keySet()) {
				res += "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(params.get(key), "UTF-8");
				System.out.println("인코딩 foreach 실행" + res);
			}

			if (res.length() > 0) {
				res = res.substring(1);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}


	private HttpURLConnection getConnection(String url, String method) throws IOException {

		// 연결 수립해주는 메서드들 별도 분리
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod(method);
		// connection 연결이 내마음처럼 안될수도 있어서 무한정되기를 해버리면 우리 시스템에서 사용하는 사용자도 무한정 연결이 수립이 되어서
		// 알맞은 응답이 올때까지 기다려야 하므로 좋지 않다. 그러므로 timeout메서드 지정

		// POST 방식일 경우 HttpURLConnection의 출력스트림 사용여부를 true 지정해야 outputstream을 활용할수있다.
		if (method.equals("POST")) {
			conn.setDoOutput(true);
		}

		conn.setConnectTimeout(5000);
		conn.setReadTimeout(5000);
		// 5초뒤 반대편에서 문제가 있다고 판단하여 연결을 끊어버린다.
		return conn;
	}

	private String getResponseBody(HttpURLConnection conn) throws IOException {

		StringBuffer responseBody = new StringBuffer();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));) {
			String line = null;
			while ((line = br.readLine()) != null) {
				responseBody.append(line);
			}

		}

		return responseBody.toString();
	}

	private void setHeaders(Map<String, String> headers, HttpURLConnection conn) {

		for (String key : headers.keySet()) {
			conn.setRequestProperty(key, headers.get(key));
		}
	}

	private void setBody(String body, HttpURLConnection conn) throws IOException {

		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));) {
			bw.write(body);
			bw.flush();
		}
	}
}
