# SYDER_Android
2020/04/29
-------------
* 가상머신에서 회원가입과 로그인이 잘 이루어졌는데, 실 기계에서 에러가 난 경우
IOException : Cleartext HTTP traffic to "API 주소" not permitted
원인 : Android 9버전 부터 http에 대한 보안정책이 강화
해결 : AndroidManifest.XML 파일에
android : uses CleartextTraffic = "true" 삽입


2020/05/01
-------------
* logout api 구현
* Laravel 에서는 Bearer 형식 Authorization 으로 토큰을 보냄
* Json 파일의 형식에 대해 확실하게 알 것.

2020/05/03
-------------
* requestQueue.add(logoutRequest) 오류
Oncreate 함수내에 requestQueue = Volley.new RequestQueue(this); 초기화 문제 해결
* logout => 만료된 토큰을 가져오면 Access Denied 
- Laravel 에서 오류 Json 파일을 보내줌

2020/05/05
-------------
* Laravel 에서 오류 Json 파일을 Log.cat에 입력이 되는지 확인
OnErrorResponse 함수내에
NetworkResponse response = error.networkResponse;
String jsonError = new String(response.data); 추가
* logout 구현 완료

2020/05/07
-------------
* 주문 출발지 도착지 업데이트
* activity_send 구현 시작
* activity_send 는 get 방식
String url = "http://13.124.189.186/api/user/request?phone="+phoneNumber+"&guard=user";
토큰은 똑같은 방법
* Error -> Access Denied 해결중

2020/05/09
-------------
* Laravel에서 토큰값 부여 오류 admin : guard 에 해당하는 토큰값은 없음
Error -> Access Denied 해결
Laravel 에서 수정 admin : user에 대한 토큰값
* Laravel에서 저장된 데이터 불러온 후 setText 하는데 버튼을 2번 눌러야지 값이 불러와짐. 원인도 찾지 못함.

2020/05/10
-------------
* Volley 콜백에 대해 구현중
서버 통신은 비동기작업이기때문에 checkSender메서드 호출하고 바로 setText를 해버리면 아직 처리 중이니 null 값이 가져옴
Volley에 대한 순서도 이해 부족 아직 해결중
* GoogleAPI Update 출발지, 도착지

2020/05/11
-------------
* 서버 통신 비동기 작업에 대한 오류 해결 
Handler 부여

mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("응답", receiverName + receiverID);
                        binding.senderName.setText(receiverName);

                    }
                }, 100);
                
 - 추후 리팩토링 할때 교수님에게 물어서 코드 수정
 * Laravel에서 Error 메세지를 보내줄때 일치하는 수신자가 없다는 Toast 추가
 
 2020/05/12
 -------------
 * 서버 통신 비동기 작업에 대한오류 해결 2
 onResponse 함수내에 setText 넣으면 해결
 - 콜백으로 등록되서 바로 호출 되는것이 아니고 서버 응답이 오면 호출
 
 2020/05/13
 -------------
 * QR코드 시작
 - 새로운 프로젝트 생성 후 QR코드 생성 및 스캔 구현
 -> 추후 Laravel에서 보내주는 데이터를 이용해 QR코드 생성

2020/05/15
 -------------
* 실 프로젝트에 QR코드 생성( EditText에 입력되는 값) 및 스캔 구현

2020/05/16
 -------------
 * Laravel 에서 보내주는 Data를 가지고 QR코드 생성
 
 2020/05/17
 -------------
 * Laravel에서 보내주는 값(QR코드 인식)에따라 Json파일 읽기
 * 유동적으로 화면이 돌아가도록 구현


 2020/05/18
 -------------
 * FCM 알림 새로운 프로젝트 생성 후 확인
 * FCM Test
 
 2020/05/19
 -------------
 * xml-id, class 파일 refectoring
 * Android Studio 현재까지 구현한 거 코드 합침
 
 2020/05/21
 -------------
 * 실 프로젝트 FCM Test 확인
 
 2020/05/22
 -------------
 * 수신동의 알림 구현
 * 포그라운드 상태인 앱에서 알림 메세지 또는 데이터 메세지를 수신하려면 onMessageReceived 콜백을 처리하는 코드를 작성해야 한다.

 
 2020/05/23
 -------------
 * 수신동의 xml 생성 -> 수신동의완료 알림 구현
 * FCM 데이터 받아오는 것이 안됨..
 * 알림을 클릭 시 원하는 Activity로 가는 것이 안됨...
 -> 해결할것들
 
 2020/05/24
 -------------
 주문정보랑 FCM정보를 구분짓다보니 서로의 필요한 데이터가 있기때문에 효율이 없어
 테스트 모듈로 들어가기 시작
 
 2020/05/25
 -------------
 * Node.js 소켓 통신에 대한 이해
 * on, emit에 대한 이해
 
 2020/05/26
 -------------
 * Socket통신 적용 ( 실시간 차량 위치 가져오기)
 
 
 2020/05/27
 -------------
* 실시간 차량 위치 마커 찍기 ( list 삭제 )

2020/05/30
------------
* 실시간 마커 찍기( 마커 삭제 구현 )
* 주문 정보랑 코드 합침
