## HttpServletRequest 개요
#### HttpServletRequest 역할
- 서블릿은 HTTP 요청 메시지를 파싱하고 그 결과를 `HttpServletRequest` 객체에 담아서 제공
- 
#### HTTP 요청 메시지
- START LINE
  - HTTP 메소드
  - URL
  - 쿼리 스트링
  - 스키마, 프로토콜
- 헤더
- 바디
  - form 파라미터 형식 조회
  - message body 데이터 직접 조회

#### HttpServletRequest 부가 기능
- 임시 저장소 기능
  - 해당 HTTP 요청이 시작부터 끝까지 유지되는 임시 저장소 기능
  - 저장 : `request.setAttribute(name, value)`
  - 조회 : `request.getAttribute(name`
- 세션 관리 기능
  - `request.getSession(create: true)`
