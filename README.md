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
  DB_URL={db_url} #jdbc:mysql://<host>:3306/<database>?useSSL=false&serverTimezone=UTC
  
  DB_USERNAME=<database_username>
  
  DB_PASSWORD=<database_password>

  BREVO_API_KEY=<your Brevo API key> #or any other suitable email service API key

  MAIL_FROM=<sender email address>
  
  APP_BASE_URL = <http://localhost:8082(for local testing)>

```
Mailtrap was used here for testing email functionality in development.