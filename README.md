# 📚 Digital Library Management System (Java)

A desktop-based **Digital Library Management System** developed using **Java (Swing GUI)**.
This project allows users to browse, borrow, and return books, while administrators can manage library data efficiently.

---

## 🚀 Features

### 👤 User (Student)

* 🔐 Login & Registration system
* 📖 View available books
* 🔍 Search books by title/author
* 📥 Borrow books
* 🔁 Return books
* 📚 View "My Books"

### 👨‍💼 Admin

* 🔐 Admin login
* ➕ Add new books with quantity
* ❌ Remove books
* 📊 Manage library inventory

---

## 🧠 Concepts Used

This project demonstrates core Java concepts:

* ✅ Object-Oriented Programming (OOP)
* ✅ Inheritance & Encapsulation
* ✅ File Handling (BufferedReader / BufferedWriter)
* ✅ Exception Handling
* ✅ Java Swing (GUI Development)
* ✅ Event Handling
* ✅ Collections (ArrayList, HashMap)

---

## 🏗️ Project Structure

```
DigitalLibrary/
│
├── src/
│   ├── main/
│   │   └── Main.java
│   │
│   ├── model/
│   │   ├── Book.java
│   │   ├── User.java
│   │
│   ├── service/
│   │   └── LibraryService.java
│   │
│   ├── utils/
│   │   └── FileHandler.java
│   │
│   ├── ui/
│   │   ├── LoginUI.java
│   │   ├── RegisterUI.java
│   │   ├── DashboardUI.java
│   │   ├── BookUI.java
│   │   ├── AdminUI.java
│   │
│   ├── exceptions/
│   │   └── BookNotAvailableException.java
│   │
│   └── data/
│       ├── books.txt
│       └── users.txt
│
└── README.md
```

---

## 💾 Data Storage

* 📄 `books.txt` → Stores book details
* 📄 `users.txt` → Stores user credentials

Example:

```
Java Basics,James Gosling,5
OOP Concepts,John Doe,3
```

```
student,123
admin,admin
```

---

## ▶️ How to Run

### 1️⃣ Navigate to src folder

```
cd src
```

### 2️⃣ Compile

```
javac main/Main.java
```

### 3️⃣ Run

```
java main.Main
```

---

## 🔑 Default Credentials

| Role  | Username | Password |
| ----- | -------- | -------- |
| Admin | admin    | admin    |
| User  | student  | 123      |

---

## ⚠️ Notes

* Ensure `data/` folder is inside `src/`
* Each user must be stored in a new line in `users.txt`
* Borrowed book tracking is currently stored in memory

---

## 🔮 Future Enhancements

* 📅 Due date & fine calculation
* 💾 Persistent borrowed records (file/database)
* 🎨 Improved UI (modern layout)
* 🔐 Password encryption
* 📊 Admin analytics dashboard

---

## 👨‍💻 Author

**Sanketh Thatikonda**
B.Tech Computer Science

---

## 📌 License

This project is for educational purposes only.
