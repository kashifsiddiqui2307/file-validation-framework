# File Validation Framework

A scalable, configuration-driven **Core Java Maven framework** for validating system extract files with support for multiple file formats (CSV, ZIP, Excel, XML, JSON).

## 🎯 Features

### Pluggable Architecture
- Extensible validator framework for easy addition of new file types
- Factory pattern for validator instantiation
- Configuration-driven approach (no code changes needed)

### Comprehensive Validations
1. **Delimiter Validation** - Validates all record rows for correct delimiter usage with detailed error reporting
2. **File Validation** - Extracts .meta.zip files and verifies presence of all expected files
3. **Trailer Validation** - Validates TRL (trailer) record count matches data rows
4. **Date Format Validation** - Validates YYYYMMDDHHMMSS format in ZIP file names
5. **Header Validation** - Validates CSV headers match expected headers from JSON configuration
6. **Not-Null Validation** - Ensures critical columns have values

### Advanced Reporting
- Attractive Extent Reports with custom styling
- Tabular format for failure details
- Execution time tracking per validation
- Detailed console logging with progress updates

### Configuration-Driven Architecture
- JSON-based configuration for headers and validation rules
- Easy extensibility without code changes
- Support for multiple file types
- Skip specific validations per file

## 🛠️ Technology Stack

| Component | Version |
|-----------|----------|
| **Java** | 11+ |
| **Maven** | 3.6+ |
| **TestNG** | 7.8.1 |
| **Extent Reports** | 5.1.1 |
| **Log4j2** | 2.20.0 |
| **Jackson** | 2.15.2 |
| **Apache Commons CSV** | 1.10.0 |
| **Apache POI** | 5.2.3 |

## 📁 Project Structure

```
file-validation-framework/
├── src/
│   ├── main/
│   │   └── java/com/filevalidation/
│   │       ├── config/              # Configuration management
│   │       ├── core/                # Core framework engine
│   │       ├── validators/          # Pluggable validators
│   │       ├── models/              # Data models
│   │       ├── utils/               # Utility classes
│   │       └── exceptions/          # Custom exceptions
│   └── test/
│       ├── java/com/filevalidation/
│       │   └── tests/
│       │       └── FileValidationTest.java
│       └── resources/
│           ├── testng.xml
│           ├── log4j2.xml
│           ├── config/
│           └── data/
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- **Java 11 or higher** installed
- **Maven 3.6+** installed
- **Git** for cloning the repository
- **Eclipse IDE** or **IntelliJ IDEA** (optional)

### Installation

```bash
# Clone the repository
git clone https://github.com/kashifsiddiqui2307/file-validation-framework.git

# Navigate to project directory
cd file-validation-framework

# Build the project
mvn clean install
```

## 📋 How to Run Tests

### 1. **Via Maven Command Line**

```bash
# Run all tests
mvn test

# Run with specific test class
mvn test -Dtest=FileValidationTest

# Run with detailed logging
mvn test -X
```

### 2. **Via Eclipse IDE**

#### Setup:
1. **File → Import → Existing Maven Projects**
2. Select project directory
3. Click **Finish**
4. Right-click project → **Maven → Update Project**

#### Run Tests:
1. **Right-click project → Run As → Maven test**
2. **Or right-click FileValidationTest.java → Run As → TestNG Test**
3. **Shortcut**: `Alt + Shift + X, T`

#### View Results:
- Results in **Console** tab
- **TestNG Results** tab shows test hierarchy
- Failed tests highlighted with details

### 3. **Via IntelliJ IDEA**

#### Setup:
1. **File → Open**
2. Select project root directory
3. **Open as Project**

#### Run Tests:
1. Navigate to `FileValidationTest.java`
2. Click **green play button** next to class name
3. **Shortcut**: `Ctrl + Shift + F10` (Windows/Linux) or `Ctrl + Shift + R` (Mac)
4. **Right-click → Run FileValidationTest**

#### View Results:
- Results in **Run Tool Window** (bottom panel)
- Shows test status, execution time, failure details
- Double-click failed test to jump to error

#### Run Specific Test Method:
1. Click on method name
2. Press `Ctrl + Shift + F10`
3. Or click the green arrow next to method

#### Coverage Report:
1. **Run → Run FileValidationTest with Coverage**
2. Coverage appears in **Coverage Tool Window**

#### Debug:
1. Set breakpoints (click left margin)
2. Right-click test → **Debug 'FileValidationTest'**
3. Step through code using debug panel
4. View variables in **Variables** panel

### 4. **IDE Shortcuts Summary**

| Action | Eclipse | IntelliJ |
|--------|---------|----------|
| Run tests | `Alt+Shift+X, T` | `Ctrl+Shift+F10` |
| Debug tests | `Alt+Shift+D, T` | `Ctrl+Shift+D` |
| Run single test | Right-click → TestNG | Click green arrow |
| View results | TestNG Results tab | Run Tool Window |

## ⚙️ Configuration

### Sample Configuration (src/test/resources/config/validation-config.json)

```json
{
  "files": [
    {
      "fileName": "BMOTOFNZ_20230101120000.zip",
      "delimiter": "|",
      "hasHeader": true,
      "fileType": "CSV",
      "expectedHeaders": [
        "ID",
        "Name",
        "Date",
        "Amount"
      ],
      "notNullColumns": [
        "ID",
        "Name"
      ],
      "dateFormat": "yyyy-MM-dd",
      "skipValidations": []
    }
  ]
}
```

## 🔍 How It Works

### Validation Flow:

```
1. Load Configuration (JSON)
   ↓
2. Initialize ValidationEngine
   ↓
3. Create FileValidationContext from file
   ↓
4. Run applicable validators
   ↓
5. Aggregate results
   ↓
6. Display summary
```

### Key Components:

- **ValidationEngine**: Orchestrates all validations
- **ValidatorFactory**: Creates validator instances
- **FileValidator**: Interface for all validators
- **ValidationResult**: Holds validation outcome details
- **ConfigurationManager**: Loads and manages config

## 📊 Validation Types

### 1. Delimiter Validation
- Ensures consistent delimiter usage across all records
- Reports columns with inconsistent field counts

### 2. File Validation
- Verifies ZIP file contents match manifest
- Reports missing/additional files

### 3. Trailer Validation
- Validates record count in trailer matches actual data
- Reports count mismatches

### 4. Date Format Validation
- Ensures filenames contain valid date format (YYYYMMDDHHMMSS)
- Reports invalid dates

### 5. Header Validation
- Verifies CSV headers match configuration
- Reports missing/additional headers

### 6. Not-Null Validation
- Ensures critical fields are not empty
- Reports rows with null/empty values

## 🐛 Debug Mode

### Enable Debug Logging:
1. Modify `src/test/resources/log4j2.xml`:
   ```xml
   <Logger name="com.filevalidation" level="DEBUG" ...>
   ```

2. Run tests:
   ```bash
   mvn test -X
   ```

### IntelliJ Debug:
1. Set breakpoints
2. Right-click test → **Debug**
3. Step through code using debug panel

### Eclipse Debug:
1. Set breakpoints
2. Right-click test → **Debug As → Debug**
3. Use Debug perspective

## 🔧 Adding a New Validator

### Step 1: Create Validator Class
```java
public class CustomValidator implements FileValidator {
    @Override
    public ValidationResult validate(FileValidationContext context) {
        // Implementation
    }
    
    @Override
    public String getName() {
        return "Custom Validation";
    }
    
    @Override
    public boolean supports(String fileType) {
        return true;
    }
}
```

### Step 2: Register in Factory
```java
// In ValidatorFactory.java
registerValidator("custom", CustomValidator.class);
```

### Step 3: Update Configuration
```json
{
  "skipValidations": []
}
```

## 📝 Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| No main method found | Use TestNG, run via Maven or IDE |
| Maven build fails | Run `mvn clean install` |
| Tests not found in IDE | Right-click project → Maven → Update |
| Log files not created | Check `logs/` directory permissions |
| Configuration not loaded | Verify path to config file |

## 🚀 Performance Features

- **Async Logging** for non-blocking performance
- **Efficient CSV Streaming** for large files
- **Factory Pattern** for validator creation
- **Batch Processing** support for multiple files
- **Execution Time Tracking** per validation

## 👨‍💻 Author

**Kashif Siddiqui**

## 📄 License

MIT License

## 📞 Support

For issues or questions:
1. Check existing GitHub issues
2. Create a new issue with detailed description
3. Include logs and configuration
4. Provide reproduction steps

---

**Happy Testing! 🎉**
