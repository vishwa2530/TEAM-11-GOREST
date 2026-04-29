# GoRest API Automation Framework 🚀

A robust and scalable API testing framework built using **Java**, **Cucumber**, and **RestAssured** for the [GoRest](https://gorest.co.in/) API.

## 🛠️ Technology Stack
- **Language:** Java 17+
- **Test Framework:** Cucumber-JVM (v7.x)
- **API Testing:** RestAssured
- **Assertions:** TestNG
- **Build Tool:** Maven
- **Data Driven:** Apache POI (Excel)
- **Reporting:** Cucumber HTML Reports

## 📂 Project Structure
```text
D:/sprint/
├── src/test/java/
│   ├── base/               # Global setup and BaseClass
│   ├── config/             # Configuration management
│   ├── endpoints/          # API endpoint constants
│   ├── hooks/              # Cucumber hooks
│   ├── runner/             # Test execution runner
│   ├── stepDefinitions/    # Glue code for features
│   └── utils/              # Data and Excel utilities
├── src/test/resources/
│   ├── features/           # Cucumber feature files
│   ├── schemas/            # JSON Schema files
│   └── testData/           # Excel data files
└── pom.xml                 # Project dependencies
```
## 🧪 Testing Coverage

### 👤 Users Module
- POST: Create user with valid/invalid data
- GET: Fetch user by ID and list users
- PUT: Update user details
- DELETE: Delete user and verify deletion
- AUTH: Validate unauthorized access

### 📝 Posts Module
- POST: Create post with dynamic user ID
- GET: Fetch post by ID and list posts
- PUT: Update post details
- DELETE: Delete post
- NEGATIVE: Invalid data scenarios

### 💬 Comments Module
- POST: Create comment with valid/invalid/empty data
- GET: Fetch single comment and all comments
- PUT: Update comment via Excel/DataTables
- DELETE: Delete comment and validate response
- AUTH: Invalid token validation

### ✅ Todos Module
- POST: Create todo
- GET: Fetch todos
- PUT: Update todo status/details
- DELETE: Delete todo
- VALIDATION: Status and response checks

## 🚀 How to Run Tests

### Run all tests
```bash
mvn test
```

### Run Users module
```bash
mvn test -Dcucumber.options="src/test/resources/features/User_Module.feature"
```

### Run Posts module
```bash
mvn test -Dcucumber.options="src/test/resources/features/Post_Module.feature"
```

### Run Comments module
```bash
mvn test -Dcucumber.options="src/test/resources/features/Comments_Module.feature"
```

### Run Todos module
```bash
mvn test -Dcucumber.options="src/test/resources/features/Todo_Module.feature"
```

---

## 📊 Report

After execution, open:

```
target/cucumber-reports/report.html
```

---

## 👨‍💻 Authors

- Rajmohan  
- Kamesh  
- Jishwa Rohith  
- Vishwa  

---

> NOTE: This framework supports API testing for Users, Posts, Comments, and Todos modules.
