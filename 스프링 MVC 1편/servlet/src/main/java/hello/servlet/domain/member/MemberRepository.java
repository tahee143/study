package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 고려
 */
public class MemberRepository {

    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    // 싱글톤
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance(){
        return instance;
    }

    private MemberRepository() {
    }

    /**
     * 회원 저장
     */
    public Member save(Member member){
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    /**
     * 회원 아이디로 조회
     */
    public Member findById(Long id){
        return store.get(id);
    }

    /**
     * 회원 모두 조회
     */
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * 회원 모두 삭제
     */
    public void clearStore() {
        store.clear();
    }

}
