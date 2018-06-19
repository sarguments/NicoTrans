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
127.0.0.1   nmsg닷nicovideo.jp
202닷248닷252닷234 pobi.god

<Server port>
server.port=8912
```

SpringBootAdmin Config - server
```
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

Pull in the Spring Boot Admin Server configuration via adding @EnableAdminServer to your configuration:

@Configuration
@EnableAutoConfiguration
@EnableAdminServer
public class SpringBootAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootAdminApplication.class, args);
    }
}
```
SpringBootAdmin Config - client

```
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

application.yml

spring.boot.admin.client.url: "http://localhost:8912" - 1  
management.endpoints.web.exposure.include: "*"        - 2

1 - The URL of the Spring Boot Admin Server to register at.
2 - As with Spring Boot 2 most of the endpoints aren’t exposed via http by default, we expose all of them. For production you should carefully choose which endpoints to expose.


Make the actuator endpoints accessible:
@Configuration
public static class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll() - 1  
            .and().csrf().disable();
    }
}

1 - For the sake of brevity we’re disabling the security for now. Have a look at the security section on how to deal with secured endpoints.
```