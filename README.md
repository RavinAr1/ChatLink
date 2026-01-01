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
- Replying to messages and chat deletion
- Mobile responsive design
- MySQL database for persistence
- Dockerized for easy deployment

---

## Environment Variables
Create a .env file or provide these variables when running locally in application.properties file or in Docker

```yaml
  DB_URL=<db_url> #jdbc:mysql://<host>:3306/<database>?useSSL=false&serverTimezone=UTC
  
  DB_USERNAME=<database_username>
  
  DB_PASSWORD=<database_password>

  MAIL_USERNAME=<gmail_username if using gmail smtp>

  MAIL_PASSWORD=<google_app_password if using gmail smtp>

  MAIL_FROM = <same as MAIL_USERNAME>

  APP_BASE_URL = <http://localhost:8082(for local testing)>

  UPLOAD_DIR = ${UPLOAD_DIR:/tmp/uploads}
#  Default file upload directory is /tmp/uploads(whichever drive the project is saved on - change to have a diffrent folder),
#  can be overridden by setting the UPLOAD_DIR environment variable when deploying the 
#  application. - change "/tmp/uploads" to your desired path keep the ${UPLOAD_DIR: part as is.
  

```
Mailtrap was used here for testing email functionality in development.

