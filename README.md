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
