package com.kh.toy.admin.member.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kh.toy.common.exception.PageNotFoundException;
import com.kh.toy.member.model.dto.Member;
import com.kh.toy.member.model.service.MemberService;

/**
 * Servlet implementation class AdminMemberController
 */
@WebServlet("/admin/member/*")
public class AdminMemberController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	private MemberService memberService = new MemberService();
	
	public AdminMemberController() {
        super();
        // TODO Auto-generated constructor stub
        System.out.println("라라라");
        System.out.println("마마마");
        System.out.println("기기기");
        System.out.println("구구구");
        System.out.println("라라라");
        System.out.println("각각각");
        System.out.println("아아아");
        System.out.println("가가가");
        System.out.println("기기기");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String[] uriArr = request.getRequestURI().split("/");
		switch (uriArr[uriArr.length-1]) {
		case "member-list":
			memberList(request,response);
			break;

		default:throw new PageNotFoundException();
		}
	}

	private void memberList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<Member> memberList = memberService.selectMemberList();
		request.setAttribute("members", memberList);
		request.getRequestDispatcher("/admin/member-list").forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
