package com.woopaca.jdbc.service;

import com.woopaca.jdbc.domain.Member;
import com.woopaca.jdbc.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 트랜잭션 - 트랜잭션 템플릿
 */
@Slf4j
public class MemberServiceV3_2 {

    //    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;
    private final MemberRepositoryV3 memberRepository;

    // TrnasactionTemplate를 주입받지 않고 TransactionManager를 주입받는 이유는, 관례도 있지만 유연함을 위해 Interface인 TransactionManager를 주입
    public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.memberRepository = memberRepository;
    }

    private void executeLogic(BusinessLogic2 logic) {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            // 람다에서 체크 예외를 밖으로 던질 수 없어 try-catch 사용
            try {
                logic.doing();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    public void accountTransfer(String fromId, String toId, int money) {
        executeLogic(() -> {
            Member fromMember = memberRepository.findById(fromId);
            Member toMember = memberRepository.findById(toId);

            memberRepository.update(fromId, fromMember.getMoney() - money);
            validation(toMember);
            memberRepository.update(toId, toMember.getMoney() + money);
        });
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}

