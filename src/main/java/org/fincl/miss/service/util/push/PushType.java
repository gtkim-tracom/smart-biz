/*
 * @Project Name : miss-biz
 * @Package Name : org.fincl.miss.service.util.push
 * @파일명          : PushType.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */ 
package org.fincl.miss.service.util.push;

/**
 * @파일명          : PushType.java
 * @작성일          : 2015. 8. 20.
 * @작성자          : ymshin
 * @수정내용
 * -------------------------------------------------------------
 *      수정일      |      수정자      |              수정내용
 * -------------------------------------------------------------
 *    2015. 8. 20.   |   ymshin   |  최초작성
 */
public enum PushType {
	DEFAULT(""),
	PUSH_001("<따릉이> 자전거 반납 추천 대여소 주변에 추천 반납 대여소 확인하기"), //반납 10분전 경고.
	PUSH_002("<따릉이> 선물 받으러 고고씽~반납한 대여소 주변 혜택정보 확인하기"); //회원탈퇴
	private String code;
	
	public String getCode() {
		return code;
	}

	private PushType(String code){
		this.code = code;
	}
}
