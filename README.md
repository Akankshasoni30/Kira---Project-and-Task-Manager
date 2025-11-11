# 🧭 Kira - Smart Project & Task Management System

![Built With Love](https://img.shields.io/badge/built%20with-love-red)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Vue](https://img.shields.io/badge/frontend-Vue.js-green)
![SpringBoot](https://img.shields.io/badge/backend-SpringBoot-orange)
![MySQL](https://img.shields.io/badge/database-MySQL-blue)

---

## ✨ Tagline

> **Visualize. Manage. Deliver.**  
> Kira empowers teams to streamline project planning, track progress visually, and boost collaboration — all in one elegant dashboard.

---

## 🧩 About

**Kira** is an intuitive full-stack **Project & Task Management System** built with **Vue.js (Vuetify)** and **Spring Boot**.  
It helps organizations manage multiple projects, assign tasks to users, and track real-time progress using **interactive charts** and **Kanban boards**.  

It includes **JWT-based authentication**, **role-based access**, and **email notifications** when new tasks are assigned — ensuring transparency and smooth coordination between Admins and Users.

[🎯 Report Bug](https://github.com/yourusername/Kira/issues) · [💡 Request Feature](https://github.com/yourusername/Kira/issues)

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
  ▶️ Watch the full demo on <a href="https://youtu.be/your-demo-link">YouTube 🎥</a>
</p>

---

## 📸 Screenshots

<h3 align="center">📊 Admin Dashboard</h3>
<div align="center">
  <img src="https://github.com/user-attachments/assets/your-screenshot-1" width="600" alt="Admin Dashboard"/>
</div>

<h3 align="center">📋 Project Management</h3>
<div align="center">
  <img src="https://github.com/user-attachments/assets/your-screenshot-2" width="600" alt="Project Management"/>
</div>

<h3 align="center">🧩 Task Kanban Board</h3>
<div align="center">
  <img src="https://github.com/user-attachments/assets/your-screenshot-3" width="600" alt="Task Board"/>
</div>

<h3 align="center">🌗 Dark & Light Theme</h3>
<div align="center">
  <img src="https://github.com/user-attachments/assets/your-screenshot-4" width="600" alt="Dark & Light Mode"/>
</div>

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
git clone https://github.com/yourusername/Kira.git

# Go to frontend folder
cd Kira/frontend

# Install dependencies
npm install

# Run development server
npm run serve
