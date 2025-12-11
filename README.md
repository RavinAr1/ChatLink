# ChatLink

ChatLink is a real-time chat application built with Spring Boot and Thymeleaf.
It uses MySQL for data storage, supports WebSocket messaging, 
user authentication, email notifications, and file attachments.

---

## Features

- User registration and authentication
- Real-time messaging with WebSocket
- File attachments in chats
- Email notifications using Gmail SMTP
- MySQL database for persistence
- Dockerized for easy deployment

---

## Environment Variables
Create a .env file or provide these variables when running locally or in Docker

```yaml
DB_URL=${db_url} #jdbc:mysql://<host>:3306/<database>?useSSL=false&serverTimezone=UTC
  DB_USERNAME=<db_user>
  DB_PASSWORD=<db_password>
  MAIL_USERNAME=<gmail_email>
  MAIL_PASSWORD=<google_app_password>

```
