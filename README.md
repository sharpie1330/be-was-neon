# be-was-2024
코드스쿼드 백엔드 교육용 WAS 2024 개정판

## Web Server 구현 1
- 웹 서버 1단계 - index.html 응답

### 학습 내용
[소켓, 입출력 스트림, 프로세스와 스레드](https://github.com/sharpie1330/be-was-neon/wiki/%EC%86%8C%EC%BC%93,-%EC%9E%85%EC%B6%9C%EB%A0%A5-%EC%8A%A4%ED%8A%B8%EB%A6%BC,-%ED%94%84%EB%A1%9C%EC%84%B8%EC%8A%A4%EC%99%80-%EC%8A%A4%EB%A0%88%EB%93%9C)
- New Client Connect! Connected IP : {}, Port : {} 로그 메시지에서 포트 번호가 매번 바뀌는 이유
  - 클라이언트 측에서 요청을 보낼 때 운영체제가 동적으로 포트를 할당해 준다. 요청을 보낸 해당 포트로 응답을 보내면 된다.
  - 서버 측에서는 8080포트로 지정해 주었기 때문에 해당 포트로 항상 listen 중. 이 어플리케이션을 구분하는 포트 번호가 8080이다.

### 구현 내용
- 요청의 첫 줄을 읽으면 GET과 같은 request method와 request url, HTTP/1.1과 같은 프로토콜 순서로 표시된다. 
- 따라서 해당 request line의 request url과 method를 파싱해 로거로 출력했다.
- request url에는 클라이언트가 GET을 요청하는 경로가 담겨있고, 해당 파일의 확장자에 따라 ContentType을 다르게 처리해 응답해주어야 한다.
- 우선 해당 경로의 파일을 찾아 바이트 배열로 읽고, 파일의 확장자로 MimeType을 결정한다. 이때 nio 라이브러리를 사용하지 않기 위해 FileInputStream 클래스를 사용해 구현했다.
- MimeType은 enum 클래스로 만들어서 파일의 확장자와 같은 이름의 enum 상수가 있으면 해당 상수의 mimeType값을 구할 수 있도록 했다.
