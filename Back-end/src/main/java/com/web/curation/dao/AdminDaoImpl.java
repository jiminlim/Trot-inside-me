package com.web.curation.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.web.curation.dto.AdminDto;
import com.web.curation.dto.SingerDto;

@Repository
public class AdminDaoImpl implements AdminDao{
	@Autowired
	SqlSession session;

	String ns = "com.web.curation.dao.AdminDao.";

	@Override
	public int addBroadSchedule(AdminDto dto) {
		return session.insert(ns+"addBroadSchedule",dto);
	}

	@Override
	public int	deleteBroadSchedule(int a_idx) {
		return session.delete(ns+"deleteBroadSchedule",a_idx);
	}

	@Override
	public List<AdminDto> getBroadScheduleList() {
		return session.selectList(ns+"getBroadScheduleList");
	}
	@Override
	public List<SingerDto> getSingerList() {
		return session.selectList(ns+"getSingerList");
	}

	@Override
	public int addSinger(SingerDto dto) {
		return session.insert(ns+"addSinger",dto);
	}

	@Override
	public int deleteSinger(int s_idx) {
		return session.delete(ns+"deleteSinger",s_idx);
	}

	
}
