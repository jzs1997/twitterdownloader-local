# README

## Intro

+ A Spring Boot + React project which can extract media(image, video, GIF) from tweets and do format conversion.
+ The front end part: go to `twitterdownloader-front-local`

### Deployment

+ Requirement:
  + nodejs >= 21.1.0
  + npm >= 10.2.0
  + java == 17
  + MySQL == 8.0.29
  + Redis == 7.2.1

+ Deployment:

Add your configurations into the `application-local.yml`

```yaml
server:
  port: 8091

  jpa:
    hibernate:
      ddl-auto: create-drop
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: # username
    password: # password
    url: # db url
  session:
    store-type: redis
    redis:
      flush-mode: immediate
      namespace: 'spring:session' # can be modified
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

cors:
  origins: http://localhost:3000 # Port may vary depends on which port your front end app is running on, should be changed if your front end app is not running on 3000
```