# nicoTransPrepare

## contribute by larry, gram(aka godDurin)

## settings 

```
<Google Cloud Translate auth>
export GOOGLE_APPLICATION_CREDENTIALS ...

<Nginx config>
/usr/local/etc/nginx/nginx.conf
server {
    listen       80;
    server_name  localhost;
    location / {
    proxy_set_header   X-Real-IP $remote_addr;
    proxy_set_header   Host      $http_host;
    proxy_pass         http://127.0.0.1:8080;
    }
}

<Host config>
/etc/hosts
127.0.0.1   nmsg.nicovideo.jp
202.248.252.234 pobi.god
```