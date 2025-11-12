# 🧭 Kira - Smart Project & Task Management System

![Built With Love](https://img.shields.io/badge/built%20with-love-red)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/frontend-Vue.js-green)
![SpringBoot](https://img.shields.io/badge/backend-SpringBoot-orange)
![MySQL](https://img.shields.io/badge/database-MySQL-blue)

---

## 🧩 About

**Kira** is an intuitive full-stack **Project & Task Management System** built with **Vue.js (Vuetify)** and **Spring Boot**.  
It helps organizations manage multiple projects, assign tasks to users, and track real-time progress using **interactive charts** and **Kanban boards**.  

It includes **JWT-based authentication**, **role-based access**, and **email notifications** when new tasks are assigned — ensuring transparency and smooth coordination between Admins and Users.

[🎯 Report Bug](https://github.com/Akankshasoni30/Kira---Project-and-Task-Manager/issues) · [💡 Request Feature](https://github.com/Akankshasoni30/Kira---Project-and-Task-Manager/issues)

---

## 🚀 Features

### 🔐 Authentication & Authorization
- JWT-based Authentication (Login, Signup, Forgot Password)
- Role-based Access (Admin & User)
- Secure APIs with Bearer Token Auth

### 📊 Dashboards
- **Admin Dashboard**
  - Project-wide analytics with Bar & Pie Charts
  - Task stats by Status (New, In Progress, Completed)
- **User Dashboard**
  - Personal task view by Priority & Status
  - Real-time charts powered by ApexCharts

### 🧱 Project Management
- Create, Edit, View, Delete projects
- View associated tasks per project
- Filter projects by name or status

### ✅ Task Management
- Full CRUD (Create, Read, Update, Delete) for tasks
- Assign tasks to users
- **Kanban Board** for drag-and-drop task status changes
- Task filtering & export (Excel/CSV)
- 📧 **Automatic Email Notification** to user on task assignment

### 💬 Collaboration
- Add comments & attachments to tasks
- Update task status dynamically

### 🌗 Interface & UX
- Dark/Light theme toggle
- Expandable sidebar
- Responsive layout built with Vuetify

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-------------|
| **Frontend** | Vue.js, Vuetify, ApexCharts |
| **Backend** | Spring Boot (Java), Spring Data JPA |
| **Database** | MySQL |
| **Auth** | JWT (JSON Web Token) |
| **Mail Service** | JavaMailSender |
| **Others** | Axios, REST APIs |

---

## 🎬 Demonstration

<p align="center">
  ▶️ Watch the full demo on <a href="https://www.youtube.com/watch?v=iDJpNhDp6xk">YouTube 🎥</a>
</p>

---

## 📸 Screenshots

### 📊 Admin vs User Dashboard
<div align="center">
 <img src="https://github.com/user-attachments/assets/44bc8975-2fda-4eab-8fc2-8839c441fb75" width="45%"  alt="Admin Dashboard"/>
 <img src="https://github.com/user-attachments/assets/d0e31d35-1c05-4e8c-aa2b-c2679ed1e456" width="45%"  alt="User Dashboard"/>
</div>
<p align="center"><em>Left: Admin Dashboard | Right: User Dashboard</em></p>



### 📋 Project Management
<div align="center">
  <img src="https://github.com/user-attachments/assets/1e72efc3-cdeb-41a6-8a2b-9e36ba6c22c5" width="45%"  alt="Admin Project Management"/>
  <img src="https://github.com/user-attachments/assets/cbc727c3-8d47-4aba-9889-ed4b208d97be" width="45%" alt="User Project View"/>
</div>
<p align="center"><em>Left: Admin Project Management | Right: User Project View</em></p>



### 🧩 Task Board (Kanban)
<div align="center">
  <img src="https://github.com/user-attachments/assets/20f6ac17-dffd-4a7a-864d-e2fc4e48b9b5" width="45%" alt="Admin Task Board"/>
  <img src="https://github.com/user-attachments/assets/86cfa08e-e165-49b1-8cda-fdac81a8838d" width="45%"  alt="User Task Board"/>
</div>
<p align="center"><em>Left: Admin Task Board | Right: User Task Board</em></p>



### 🌗 Dark Mode
<div align="center">
  <img src="https://github.com/user-attachments/assets/53edd6c2-abf7-4c5c-94e2-bfd557d7bc1d" width="45%"  alt="Dark Theme"/>
  
---


## ⚙️ Project Setup

This project includes two modules:  
- **Frontend:** Vue.js + Vuetify  
- **Backend:** Spring Boot + MySQL  

---

### 🖥️ Frontend Setup

#### Prerequisites
- [Node.js](https://nodejs.org/) >= 14  
- npm (comes with Node.js)

#### Steps

```bash
# Clone the repository
git clone https://github.com/Akankshasoni30/Kira---Project-and-Task-Manager

# Go to frontend folder
cd Kira/frontend

# Install dependencies
npm install

# Run development server
npm run serve

Frontend runs by default on:
👉 http://localhost:8081

```

🔧 Backend Setup

Prerequisites
Java 17+
Maven
MySQL Server
Steps
# Navigate to backend folder
cd ../backend

📄 Configure Database & Mail
```bash
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kira_db
    username: your_db_username
    password: your_db_password

  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com          # Replace with your Gmail address
    password: your_app_password            # Replace with Gmail App Password


💡 Tip: Use a Gmail App Password
 instead of your main Gmail password for security.

# Build and run the application
mvn spring-boot:run

Backend runs by default on:
👉 http://localhost:8080
```

🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to fork this repository and submit a pull request.

Developed with ❤️ by Akanksha Soni


