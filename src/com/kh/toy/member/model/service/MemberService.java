package com.kh.toy.member.model.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kh.toy.common.db.JDBCTemplate;
import com.kh.toy.common.http.HttpConnector;
import com.kh.toy.common.http.RequestParams;
import com.kh.toy.common.mail.MailSender;
import com.kh.toy.member.model.dao.MemberDao;
import com.kh.toy.member.model.dto.Member;

//Service 
//웹어플리케이션의 비지니스 로직 작성
//사용자의 요청을 컨트롤러로 부터 위임받아 해당 요청을 처리하기 위한 핵심적인 작업을 진행
//요청을 처리하기 위해 데이터베이스에 저장된 데이터가 필요하면 Dao에게 요청
//비지니스 로직을 Service가 담당하기 때문에 Transaction 관리도 Service가 담당.
//commit,rollback을 Service 클래스에서 처리
public class MemberService {
	
	private MemberDao memberDao = new MemberDao();
	private JDBCTemplate template = JDBCTemplate.getInstance();
	
	public Member memberAuthenticate(String userId, String password) {
		Connection conn = template.getConnection();
		Member member = null;
		
		//1. 사용자의 아이디로 DB에서 암호화된 password를 가져온다.
		try {
			member = memberDao.memberAuthenticate(userId, password, conn);
		}finally {
			template.close(conn);
		}
		
		//2. DB에 저장된 password를 복호화 한다.
		//2. 요청이 전달한 아이디 패스워드와, 복호화된 아이디 패스워드가 일치하는지 확인한다.
		//3. 로그인에 성공할 경우 DB에서 해당 회원의 즐겨찾기 정보를 읽어와 session에 저장한다.
		//해당 기능들을 구현하기 위해서는 중간에 실행되다가 에러가 나와도 전체적인 rollback이 필요하므로 service클래스에서 기능 구현해야된다.(dao가아닌)
		
		return member;
	}
	
	public Member selectMemberById(String userId) {
		Connection conn = template.getConnection();
		Member member = null;
		try {
			
			member = memberDao.selectMemberById(userId, conn);
			
		} finally {
			template.close(conn);
		}
		
		return member;
	}
	
	public List<Member> selectMemberList() {
		Connection conn = template.getConnection();
		List<Member> memberList = null;
		
		try {
			memberList = memberDao.selectMemberList(conn);
		} finally {
			template.close(conn);
		}
		
		return memberList;
	}
	
	public int insertMember(Member member) {
		Connection conn = template.getConnection();
		int res = 0;
		
		try {
			//회원가입처리
			res = memberDao.insertMember(member, conn);
			//회원가입이후 자동 로그인
			//방금 가입한 회원의 아이디로 정보를 다시 조회
			Member m = memberDao.selectMemberById(member.getUserId(), conn);
			//다오를 통해 사용자 정보를 받아서 해당 정보로 로그인 처리 진행
			template.commit(conn);
			
		} catch (Exception e) {
			template.rollback(conn);
			throw e;
		} finally {
			template.close(conn);
		}
		
		return res;
	}

	public int updateMember(Member member) {
		Connection conn = template.getConnection();
		int res = 0;
		
		try {
			res = memberDao.updateMember(member, conn);
			template.commit(conn);
		} catch (Exception e) {
			template.rollback(conn);
			e.printStackTrace();
		} finally {
			template.close(conn);
		}
		
		return res;
	}

	public int deleteMember(String userId) {
		Connection conn = template.getConnection();
		int res = 0;
		
		try {
			res = memberDao.deleteMember(userId,conn);
			template.commit(conn);
		} catch (Exception e) {
			template.rollback(conn);
			e.printStackTrace();
		} finally {
			template.close(conn);
		}
		
		return res;
	}

	public void authenticateEmail(Member member, String persistToken) {
		HttpConnector conn = new HttpConnector();
		
		/*Map<String,String> params = new HashMap<String, String>();
		params.put("mail-template", "join-auth-email");
		params.put("persistToken", persistToken);
		params.put("userId", member.getUserId());
		*/
		
		String queryString = conn.urlEncodedForm(RequestParams.builder()
												.param("mail-template", "join-auth-email")
												.param("persisToken", persistToken)
												.param("userId", member.getUserId())
												.build()
												);
		
		String mailTemplate = conn.get("http://localhost:7070/mail?"+queryString);
		MailSender sender = new MailSender();
		sender.sendEmail(member.getEmail(), "환영합니다." + member.getUserId() + "님", mailTemplate);
	}
}
