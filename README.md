# OTP-проект

Java-приложение для генерации и валидации одноразовых паролей (OTP) с поддержкой многоканальной доставки и административного управления.

## Возможности

- Регистрация и вход с выдачей JWT
- Генерация и проверка OTP-кодов
- Доставка OTP через Email, SMS, Telegram и файлы
- Ограничение числа попыток ввода кода (защита от брутфорса)
- Администрирование: настройка параметров, список пользователей, удаление
- JWT-защита всех пользовательских и админских маршрутов

## Технологии

- Java 17
- Spring Boot
- Spring Security
- PostgreSQL
- Maven

## Сборка и запуск

```bash
git clone <URL_вашего_репозитория>
cd otp-project
mvn clean install
mvn spring-boot:run

## Конфигурация (application.yaml)
spring:
  application:
    name: phoenix-code
  datasource:
    url: jdbc:postgresql://localhost:5432/otp
    username: postgres
    password: postgres  
    driver-class-name: org.postgresql.Driver


### API
## POST /api/auth/register
{
  "login": "username",
  "password": "your_password",
  "role": "USER",
  "email": "user@example.com",
  "phoneNumber": "+1234567890",
  "telegram": "telegram_handle"
}

## POST /api/auth/login
{
  "login": "username",
  "password": "your_password"
}

## POST /api/user/generate-otpcode
{
  "operationId": "some_operation_id",
  "deliveryType": "EMAIL"
}

## POST /api/user/validate-otp
{
  "operationId": "some_operation_id",
  "code": "123456"
}
