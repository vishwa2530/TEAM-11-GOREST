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

## 🧪 Testing Coverage: Comments Module
The current implementation covers 15 critical scenarios for the **Comments Module**:
- ✅ **POST:** Create comment with valid/invalid/empty data.
- ✅ **GET:** Fetch single comment by ID, fetch all comments.
- ✅ **PUT:** Update comment details via Excel and DataTables.
- ✅ **DELETE:** Delete existing comments and verify deletion.
- ✅ **AUTH:** Negative testing for invalid tokens and unauthorized access.

## 🚀 How to Run Tests

### Run all tests
```bash
mvn test
```

### Run specific module (Comments)
```bash
mvn test -Dcucumber.options="src/test/resources/features/Comments_Module.feature"
```

### Generate Report
After execution, the HTML report can be found at:
`target/cucumber-reports/index.html`

## 👨‍💻 Author
**Jishwa**
*Senior Automation Engineer*

---
> [!NOTE]
> This framework is designed for standardized API testing across all GoRest modules (Users, Posts, Todos, Comments).
