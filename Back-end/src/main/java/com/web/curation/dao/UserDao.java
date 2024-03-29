package com.web.curation.dao;

import java.sql.SQLException;
import java.util.List;

import com.web.curation.dto.BoardDto;
import com.web.curation.dto.FollowDto;
import com.web.curation.dto.GoodDto;
import com.web.curation.dto.UserDto;

public interface UserDao {
	/* 회원가입 */
	public int join(UserDto user);

	/* 로그인 */
	public UserDto getUserInfoSuc(String email);

	/* 회원정보 변경 */
	public UserDto edit(UserDto userDto) throws SQLException;

	/* 회원정보 삭제 */
	public void delete(UserDto userDto) throws SQLException;

	/* 비밀번호 찾기 */
	public String findPassword(String email);

	/* 비밀번호 변경 */
	public void changePassword(UserDto userDto);

	// 팔로우 전체 리스트 출력
	public List<FollowDto> getFollowList(String userEmail);

	// 팔로우 추가
	public int followApply(FollowDto dto);

	// 팔로우 삭제
	public int followDelete(FollowDto dto);

	// 마이페이지 영상, 기사 리스트
	public List<BoardDto> myBoardList(GoodDto gdto);
}
