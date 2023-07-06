package com.woopaca.jdbc.repository;

import com.woopaca.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV0Test {

    public static final int UPDATE_MONEY = 20_000;
    public static final int INITIAL_MONEY = 10_000;
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV2", INITIAL_MONEY);
        memberRepository.save(member);

        // findById
        Member findMember = memberRepository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update (money: 10,000 -> 20,000)
        memberRepository.update(member.getMemberId(), UPDATE_MONEY);
        Member updatedMember = memberRepository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(UPDATE_MONEY);

        // delete
        memberRepository.delete(member.getMemberId());
        assertThatThrownBy(() -> memberRepository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);
    }
}