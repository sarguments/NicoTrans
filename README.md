# nicoTransPrepare

## contribute by larry, gram(aka godDurin)

<img width="882" alt="saru_key" src="https://user-images.githubusercontent.com/25028828/44003138-1cf4c94a-9e89-11e8-82dd-92fcb809a71c.png">

## settings 

```
<Google Cloud Translate auth>
export GOOGLE_APPLICATION_CREDENTIALS ...


<API key>
GOOGLE_API_KEY ...


<Nginx config>
/usr/local/etc/nginx/nginx.conf - MAC
/etc/nginx/nginx.conf - LINUX

server {
    listen 80;
    server_name localhost;
    location / {
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   Host      $http_host;
    proxy_pass         http://127.0.0.1:9090;
    }
}

server {
    listen 443 ssl;
    server_name localhost;
    ssl_certificate 파일경로.crt;
    ssl_certificate_key /etc/ssl/private/nmsg.nicovideo.jp.key; (예시)
    location / {
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   Host      $http_host;
    proxy_pass         http://127.0.0.1:9090;
    }
}

참고 : https://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-nginx-in-ubuntu-16-04

<Host config>
/etc/hosts
127.0.0.1   nmsg닷nicovideo.jp
202닷248닷252닷234 pobi.god

```

## 원래 작동 순서
1. http://nicovideo.jp/watch/sm5361236 와 같이 동영상 주소로 접속을 한다.
2. 웹 페이지가 1차적으로 로딩이 되고 다시 브라우저에서 post 요청이 코멘트 서버인 nmsg.nicovideo.jp/api.json 으로 간다.
3. 코멘트 서버에서 해당 동영상에 맞는 코멘트를 json으로 리턴한다.
4. 브라우저는 코멘트를 받아서 웹브라우저의 동영상 위에 렌더링 한다.

코멘트의 경우 실제로 하나하나가 실시간으로 전송되는 것은 아니고 전체 데이터가 한꺼번에 온다. 동영상 재생 중 유저가 코멘트를 남기면 그 타이밍이 저장되고 나중에 다른 유저가 해당 동영상을 재생했을때 같은 타이밍에 다른 유저가 남긴 코멘트를 볼 수 있다.

## 프로젝트 작동 순서
1. http://nicovideo.jp/watch/sm5361236 와 같이 동영상 주소로 접속을 한다.
2. 웹 페이지가 1차적으로 로딩이 되고 다시 브라우저에서 post 요청이 코멘트 서버인 nmsg.nicovideo.jp/api.json 으로 간다.
3. nmsg.nicovideo.jp/Api.json 로의 요청이 host 설정에 의해 localhost:80 으로 간다.
4. Localhost:80 으로 간 요청이 nginx에 의해 다시 localhost:8080으로 간다.
5. 스프링부트 서버가 8080포트로의 요청을 받는다.
6. 요청의 헤더를 사용해서 컨텐트 타입과 Origin 등의 정보가 담긴 일종의 복제된 헤더 객체를 생성하고, 원래 요청의 바디를 합쳐 HttpEntity 인스턴스를 생성한다.
7. 생성한 인스턴스로 http://pobi.god/api.json에 요청을 보내서 json 객체를 스프링부트 서버로 받아온다. ( 여기서 https 때문에 문제 발생 )
   * 원래 코멘트 서버의 url로 보내는게 아닌 http://pobi.god/api.json 로 보내는 이유는 이전에 host 설정에 의해 다시 localhost:80으로 이동하기 때문이다. host설정에 별도로 http://pobi.god/api.json 을 202.248.252.234 으로 가도록 설정해 두었다.
   * 받아올때 아파치의 http 라이브러리를 사용해서 gzip으로 압축된 json 파일을 받는다.
8. 받은 json을 List<Item> 형으로 역 직렬화 한다. Item은 HashMap<String, Contents>을 확장한 객체이다.
9. List<Item>에서 Item의 key가 ‘content’ 인 value에 해당하는 Content 인스턴스를 찾는다. Contents는 HashMap<String, Object>을 확장한 객체이다.
10. 찾은 Content 안에서 다시 key가 ‘content’인 요소를 찾아서 해당 요소의 레퍼런스와 value인 번역할 텍스트를 Pair클래스의 새로운 인스턴스로 반환한다.
11. 그리고 이렇게 생성된 Pair 인스턴스들을 모아서 List<Pair>로 다시 반환한다.
12. List<Pair>에서 번역할 텍스트만 따로 리스트로 추출해서 번역한다.
    * 이때 번역을 한꺼번에 할 경우 번역이 되지 않는다. 따라서 GUAVA를 활용해 100개 단위의 리스트로 나눈 후 나눠진 리스트를 스트림으로 각각 번역후 다시 합쳐서 하나의 리스트로 리턴한다.
13. List<Pair>를 돌면서 Pair에서 Content의 레퍼런스를 이용해서 원래 Content의 코멘트 부분을 번역한 코멘트로 교체한다.
14. 번역이 완료된 List<Item>을 json으로 변환해 리턴한다. 리턴된 json은 브라우저로 간다.
15. 브라우저는 코멘트를 받아서 웹브라우저의 동영상 위에 렌더링 한다.
