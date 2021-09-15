package com.kh.toy.common.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kh.toy.common.code.ErrorCode;

import com.kh.toy.common.code.member.MemberGrade;
import com.kh.toy.common.exception.HandlableException;
import com.kh.toy.member.model.dto.Member;

/**
 * Servlet Filter implementation class AuthorizeFilter
 */
@WebFilter("/AuthorizeFilter")
public class AuthorizationFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public AuthorizationFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String[] uriArr = httpRequest.getRequestURI().split("/");

		if (uriArr.length != 0) {
			switch (uriArr[1]) {
			case "member":
				memberAuthorize(httpRequest, httpResponse, uriArr);
				break;
			case "admin":
				adminAuthorize(httpRequest, httpResponse, uriArr);
				break;
			case "board":
				boardAuthorize(httpRequest, httpResponse, uriArr);
				break;
			default:
				break;
			}
		}

		chain.doFilter(request, response);
	}

	private void boardAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr) {

		HttpSession session = httpRequest.getSession();
		Member member = (Member) session.getAttribute("authentication");

		switch (uriArr[2]) {
		case "board-form":
			if (member == null) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE);
			}
			break;
		case "upload":
			if (member == null) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE);
			}
			break;

		default:
			break;
		}

	}

	private void adminAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr) {

		Member member = (Member) httpRequest.getSession().getAttribute("authentication");

		// 넘어온 인증정보가 관리자인지 사용자인지 판단.
		if (member == null || MemberGrade.valueOf(member.getGrade()).ROLE == "user") {
			throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE);
		}

		MemberGrade adminGrade = MemberGrade.valueOf(member.getGrade());
		if (adminGrade.DESC.equals("super"))
			return;

		switch (uriArr[2]) {
		case "member":
			// 회원과 관련된 관리를 수행하는 admin 등급은 AD01
			if (!adminGrade.DESC.equals("member")) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE);
			}
			break;
		case "board":
			// 회원과 관련된 관리를 수행하는 admin 등급은 AD02
			if (!adminGrade.DESC.equals("board")) {
				throw new HandlableException(ErrorCode.UNAUTHORIZED_PAGE);
			}

			break;
		default:
			break;
		}
	}

	private void memberAuthorize(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String[] uriArr)
			throws ServletException, IOException {

		switch (uriArr[2]) {
		case "join-impl":
			String serverToken = (String) httpRequest.getSession().getAttribute("persist-token");
			String clientToken = httpRequest.getParameter("persist-token");
			if (serverToken == null || !serverToken.equals(clientToken)) {
				throw new HandlableException(ErrorCode.AUTHENTICATION_FAILED_ERROR);
			}
			break;
		case "mypage":
			if (httpRequest.getSession().getAttribute("authentication") == null) {
				throw new HandlableException(ErrorCode.REDIRECT_LOGIN_PAGE_NO_MESSAGE);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
