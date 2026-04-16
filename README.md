# 📖 Bible Verse Generator

A full-stack application that delivers a random Bible verse to the user on the click of a button. The backend is built with Java (Maven) and uses ActiveMQ as a message queue to send verses to the frontend, which is built with HTML, CSS, and JavaScript.

---

## 🗂️ Project Structure

```
bible-verse-generator/
├── src/
│   └── main/
│       └── java/
│           └── ...         # Java source files (producer & consumer)
├── resources/
│   └── verses.json         # Bible verses data file
├── frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── pom.xml                 # Maven configuration
└── README.md


---

*Built by [Khanyisile Makhubu](https://github.com/Khanyi-Codes)*