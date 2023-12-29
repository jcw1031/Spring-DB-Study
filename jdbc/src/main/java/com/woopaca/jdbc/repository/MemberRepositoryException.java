package com.woopaca.jdbc.repository;

import com.woopaca.jdbc.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryException {

    Member save(Member member) throws SQLException;

    Member findById(String memberId) throws SQLException;

    void update(String memberId, int money) throws SQLException;

    void delete(String memberId) throws SQLException;
}
