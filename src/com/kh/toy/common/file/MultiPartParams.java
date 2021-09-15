package com.kh.toy.common.file;

import java.util.List;
import java.util.Map;


public class MultiPartParams {
	
	private Map<String,List> params;
	
	public MultiPartParams(Map<String,List> params) {
		this.params = params;
	}
	
	public String getParameter(String name) {
		// TODO Auto-generated method stub
		
		if(name.equals("com.kh.toy.files")) {
			throw new RuntimeException("com.kh.toy.files는 사용할 수 어뵤는 파라미터 명입니다.");
		}
		return (String)params.get(name).get(0);
	}
	
	public String[] getParameterValues(String name) {
		
		List<String> res = params.get(name);
		
		return res.toArray(new String[res.size()]);
	}

	public List<FileDTO> getFilesInfo() {
		// TODO Auto-generated method stub
		return params.get("com.kh.toy.files");
	}

	

}
