package hello.core.singleton;

public class SingletonService {

    // static 영역에 인스턴스 생성
    private static final SingletonService instance = new SingletonService();

    // 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용
    public static SingletonService getInstance() {
        return instance;
    }

    // private 생성자통해 외부에서 객체 생성을 막음
    private SingletonService() {
    }

    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
