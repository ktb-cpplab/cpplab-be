# cpplab-be

# **📈 ERD**
<details>
  <summary>📘사진</summary>
  <div markdown="1">
  <img src="https://github.com/user-attachments/assets/8b6e4368-2f4b-43de-9492-a7f84491614b">
</details>

## 💎 왜 이 기술을 사용했는가?
<details>
    <summary>📘소셜로그인 순서도</summary>
    <div markdown="1">
    <img src="https://github.com/user-attachments/assets/88d90995-aebd-4ed5-bbd2-be6f8c588351">
    (스프링의 oauth2-client 라이브러리 + JWT + Redis)를 결합하여 소셜로그인을 구현했습니다.</br>
    프론트 서버에서 엑세스 토큰으로 로그인 상태처리를 위해 AccessToken를 전달해야 합니다.</br>
    href요청은 데이터 요청 전달이 불가하기에 useEffect로 AccessToken 재요청하였습니다.</br>
</details>
       
<details>
    <summary>📘토큰 저장소 선정 방식</summary>
    <div markdown="1">
    <img width="655" alt="image" src="https://github.com/user-attachments/assets/9723eaaf-8df1-4e9d-9c9a-6e384683113e" /> </br>
    클라이언트 </br>
      - cookie: Refresh Token </br>
      - local stoarge: Access Token </br>
    서버 </br>
      - Redis: Refresh Token</br>
      </br>
      총 3곳에 JWT 로그인 방식으로 토큰을 저장했습니다.</br></br>
      2 가지 문제점과 해결방법</br>
      첫째, Refresh Token이 클라이언트에서만 관리되면 로그아웃 시점에 토큰 무효화 방법이 없었습니다.</br>
      서버에도 Refresh Token을 추가하여 로그아웃 시 해당 토큰을 서버에서 직접 만료 시키도록 구현했습니다.</br>
      둘째, MySQL은 토큰 만료 후 삭제 작업이 부담되는 작업이므로 Redis로 토큰 저장소를 바꾸고 TTL 기능을 사용하여 expire을 자동 만료하도록 하였습니다.</br>
</details>

<details>
    <summary>📘부하 테스트</summary>
    <div markdown="1">
       
### nGrinder

<img src="https://github.com/user-attachments/assets/1b4e8ba3-b8d1-47f9-9954-af945a7964e4">

local 테스트를 Jmeter를 이용해서 확인했습니다.

하지만 운영환경 테스트에서 인스턴스 스펙 대비 Vuser의 많은 요청을 만들기 위해서 분산 Agent 환경을 구축해야 합니다.

nGrinder는 기본적으로 여러 개의 Agent를 사용해 분산 테스트를 쉽게 설정할 수 있기에, 요청만 보내는 간단한 로직에서 스크립팅 설정 없이 직관적인 UI로 빠르게 부하테스트를 진행했습니다.

---

### 문제점

   <img src="https://github.com/user-attachments/assets/c7b59832-8f25-4458-a6d9-2a758ed6b382">
      
   프로젝트 추천 기능은 응답이 느리더라도 답변 품질 향상이 중요합니다.
   
   저희 팀은 정확도를 올리기 위해 2가지를 포기했습니다.
   
   - 금전적 손해: openAI 호출횟수 1→3회
   - 응답 시간 지연 : 프롬프트 세분화로 30→40초
   
   ---
   
   ### 가설
   
   긴 시간동안 백엔드가 커넥션을 잡지말고 비동기 처리로 그 동안 비즈니스 처리를 한다고 생각했습니다.
   
   - 백엔드 서버: 버츄어 쓰레드로 비동기처리
   - AI서버: async로 비동기 처리
   
   로 비동기 처리를 진행하면 TPS가 올라가고 비즈니스 로직 처리에 집중할 수 있다.
   
   #### 환경세팅
   
   - Spring 서버에서 Virtual Thread로 비동기를 내리기 위해서 자바(17→21)로 변경했습니다.
   
   ---
   
   ### 백엔드 로컬 테스트
   
   |  | VU | Loop Count | Throughput | Received KB/sec | Sent KB/sec |
   | --- | --- | --- | --- | --- | --- |
   | Platform Thread | 1000 | 100 | 207.4/sec | 109.21 | 67.05 |
   | Platform Thread | 3000 | 100 | 197.5/sec | 82.76 | 67.33 |
   | Platform Thread | 3000 | 100 | 198.5/sec | 83.77 | 68.75 |
   |  |  |  |  |  |  |
   | Virtual Thread | 1000 | 100 | 673.4/sec | 320.27 | 223.25 |
   | Virtual Thread | 3000 | 100 | 1732.2/sec | 750.68 | 586.53 |
   | Virtual Thread | 3000 | 100 | 1806.0/sec | 769.06 | 613.46 |
   
   백엔드 먼저 로컬테스트를 통해서 Vuser 3000명일 때 Throughput `9배`가 증가되는 것을 확인했습니다.

   <table>
        <tr>
          <td align="center">
            <img width="1203" alt="Platform Thread TPS" src="https://github.com/user-attachments/assets/bf57dba9-a77c-4dd0-b4ef-b5a6f93068d4" width="400"/>
            <br>
            <b>&lt;Platform Thread TPS&gt;</b>
          </td>
          <td align="center">
            <img width="1212" alt="Virtual Thread TPS" src="https://github.com/user-attachments/assets/b6831090-5551-4378-aeed-4a613b5cb82e" width="400"/>
            <br>
            <b>&lt;Virtual Thread TPS&gt;</b>
          </td>
        </tr>
   </table>
   
   ---
   
   ### 운영환경 테스트
   
   <img src="https://github.com/user-attachments/assets/3ad203b9-9fcd-445d-9ada-4c899a7ba69b">
   
   부하테스트 요금을 아끼기 위해 2가지 방법을 사용했습니다.
   
   첫째, openAI의 1회 요청은 0.002$이기에 총 90,000번의 부하테스트 요청은 부담됩니다.
   
   따라서 Mock Data와 time.sleep()을 통해 실제와 비슷한 운영환경을 만들었습니다.
   
   둘째, EC2 ondemand를 사용하지 않고 spot 인스턴스를 사용해서 ~~~
   
   3배 가격을 절약했습니다.(제 맘대로 적은거라 수정해주세요.)
   
   ---
   
   ### 테스트 진행
   
   비동기와 동기를 기준으로 4가지를 테스트했습니다.
   
   | BE | AI | vUser | TPS | 실행횟수 | 성공비율 |  |
   | --- | --- | --- | --- | --- | --- | --- |
   | 동기 | 동기 | 10 | 0.2 | 66 | 94% |  |
   |  |  | 40 | 1 | 250 | 98% |  |
   |  |  | 100 | 0.9 | 100 | 45% | 실패: timeout |
   | 비동기 | 동기 | 40 | 1 | 252 | 97% |  |
   |  |  | 100 | 0.8 | 100 | 43% | 실패: Too many errors |
   | 동기 | 비동기 | 40 | 0.9 | 244 | 98% |  |
   |  |  | 100 | 2.4 | 628 | 98% |  |
   |  |  | 300 | 2.6 | 1,291 | 52% | 경고: 30% 이상 실패 |
   | 비동기 | 비동기 | 100 | 2.5 | 632 | 98% |  |
   |  |  | 300 | 6.8 | 1830 | 98% |  |
   |  |  | 1000 | 22.6 | 5,626 | 98% |  |
   |  |  | 1500 | 31.4 | 7,823 | 98% |  |
   |  |  | 2000 | 14.5 | 6,602 | 35% | 실패: 백엔드 종료 |
   
   ---
   
   ### 테스트 결과
   
   초기 설계인 동기-동기 아키텍처는 100명의 vUSer를 버티지 못하고 테스트를 실패했습니다.
   
   반면 비동기-비동기 아키텍처는 동일 스펙임에도 불구하고 1500명의 vUSer를 버티는 것을 확인했습니다.
   
   ---
   
   ### 테스트 트러블슈팅
   
   <img width="381" alt="image (3)" src="https://github.com/user-attachments/assets/2f3a4576-3479-4df8-8f28-9af38527ee92" />

   인스턴스 t2.small의 agent 스펙을 사용했는데 vUser가 1500일 때 인스턴스가 lack of free memory로 죽어서 인스턴스의 스펙을 t3a.large로 올려서 MEM 20%로 안정적으로 Vuser 환경을 만들었습니다.

</details>

<details>
    <summary>📘추가작성 예정</summary>
    <div markdown="1">
   
   1. 모니터링
   
   AOP를 사용해서 마이크로미터에서 @Counted 로 매트릭을 프로메테우스에게 응답
      
   시스템 메트릭(CPU, 메모리)
   
   애플리케이션 메트릭(톰캣 쓰레드 풀, DB 커넥션 풀, 애플리케이션 호출 수) 
   
   비즈니스 메트릭(주문수, 취소수)
      
   처럼 해서 내렸다 그림~~~
   
   
   2. 알람
      
   경고와 심각을 구분해서 알람을 내렸다
   
   디스크 사용량 70% 경고 
   
   디스크 사용량 80% 심각 
   
   CPU 사용량 40% 경고 
   
   CPU 사용량 50% 심각
   
   </details>
