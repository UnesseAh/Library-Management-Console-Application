package service;

import domain.Member;
import repository.MemberRepository;

import java.sql.SQLException;

public class MemberService {
    private MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void addMember(Member member) {
        memberRepository.addMember(member);
    }

    public boolean isMemberExists(int memberNumber) throws SQLException {
        return memberRepository.isMemberExists(memberNumber);
    }
    public int getMemberId(int memberNumber) throws SQLException {
        return memberRepository.getMemberId(memberNumber);
    }
}