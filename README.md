# nicoTransPrepare

## contribute by larry, gram(aka godDurin)

<img width="882" alt="saru_key" src="https://user-images.githubusercontent.com/25028828/44003138-1cf4c94a-9e89-11e8-82dd-92fcb809a71c.png">

## settings 

```
<Google Cloud Translate auth>
export GOOGLE_APPLICATION_CREDENTIALS ...

<Nginx config>
/usr/local/etc/nginx/nginx.conf - MAC
/etc/nginx/nginx.conf - LINUX
server {
    listen       80;
    server_name  localhost;
    location / {
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   Host      $http_host;
    proxy_pass         http://127.0.0.1:8080;
    }
}iii

<Host config>
/etc/hosts
127.0.0.1   nmsg닷nicovideo.jp
202닷248닷252닷234 pobi.god

```
