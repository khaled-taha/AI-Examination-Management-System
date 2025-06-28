# Code Evaluation Service

A Spring Boot microservice for evaluating code submissions in multiple programming languages with comprehensive testing and resource management.

## 🚀 Features

- **Multi-language Support**: Java, Python, C, C++, and SQL (MySQL)
- **Comprehensive Testing**: Compilation, runtime, time limit, and memory limit checks
- **Resource Management**: Configurable timeouts and memory limits
- **Detailed Feedback**: Categorized output types with specific error messages
- **Mark Calculation**: Automatic scoring based on passed test cases
- **Docker Support**: Complete containerized environment with all dependencies
- **Health Monitoring**: Built-in health checks and metrics

## 🐳 Docker Setup (Recommended)

The easiest way to run the service is using Docker, which includes all necessary compilers and interpreters.

### Prerequisites

- Docker and Docker Compose installed
- At least 2GB of available RAM
- Port 8080 available

### Quick Start

1. **Clone and navigate to the service directory:**
   ```bash
   cd code-evaluation-service
   ```

2. **Build and start the containers:**
   ```bash
   docker-compose up -d --build
   ```

3. **Wait for services to be ready (about 30-60 seconds):**
   ```bash
   # Check health
   curl http://localhost:8080/actuator/health
   ```

4. **Run comprehensive tests:**
   ```bash
   # Linux/Mac
   chmod +x test-docker.sh
   ./test-docker.sh
   
   # Windows
   test-docker.bat
   ```

### Docker Architecture

The Docker setup includes:

- **Code Evaluation Service**: Spring Boot application with all language evaluators
- **MySQL Database**: For SQL evaluation with sample data
- **Network**: Isolated network for service communication
- **Volumes**: Persistent MySQL data and temporary code execution directories

### Environment Variables

The service uses these environment variables (configured in docker-compose.yml):

```yaml
MYSQL_HOST: mysql
MYSQL_PORT: 3306
MYSQL_DATABASE: testdb
MYSQL_USER: root
MYSQL_PASSWORD: password
```

## 📋 API Endpoints

### Code Evaluation
- `POST /api/v1/code-evaluation/evaluate` - Evaluate code submission
- `GET /api/v1/code-evaluation/languages` - Get supported languages
- `GET /api/v1/code-evaluation/status` - Get evaluator availability status

### Health & Monitoring
- `GET /actuator/health` - Service health check
- `GET /actuator/info` - Service information
- `GET /actuator/metrics` - Service metrics

## 🔧 Supported Languages

### Java
- **Compiler**: OpenJDK 17
- **File Extension**: `.java`
- **Requirements**: Public class named `Solution` with `main` method

### Python
- **Interpreter**: Python 3.10
- **File Extension**: `.py`
- **Requirements**: Standard input/output

### C
- **Compiler**: GCC
- **File Extension**: `.c`
- **Requirements**: Standard input/output

### C++
- **Compiler**: G++
- **File Extension**: `.cpp`
- **Requirements**: Standard input/output

### SQL (MySQL)
- **Database**: MySQL 8.0
- **File Extension**: `.sql`
- **Requirements**: Valid SQL syntax, sample data provided

## 📊 Output Types

The service categorizes execution results into:

- **ALL_PASSED**: All test cases passed successfully
- **COMPILATION_ERROR**: Code failed to compile
- **RUNTIME_ERROR**: Code compiled but failed during execution
- **TIME_LIMIT_EXCEEDED**: Execution exceeded time limit (10 seconds)
- **MEMORY_LIMIT_EXCEEDED**: Execution exceeded memory limit (512MB)
- **PARTIAL_SUCCESS**: Some test cases passed, some failed

## 🎯 Mark Calculation

- **Total Score**: Sum of marks from passed test cases only
- **Failed Cases**: Get 0 marks regardless of test case mark value
- **Partial Success**: Only passed cases contribute to total score

## 🔍 Testing

### Manual Testing

Test individual languages:

```bash
# Java
curl -X POST http://localhost:8080/api/v1/code-evaluation/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Solution { public static void main(String[] args) { System.out.println(\"Hello World\"); } }",
    "language": "java",
    "testCases": [{"input": "", "expectedOutput": "Hello World", "mark": 1.0, "isSample": false}]
  }'

# Python
curl -X POST http://localhost:8080/api/v1/code-evaluation/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "code": "print(\"Hello World\")",
    "language": "python",
    "testCases": [{"input": "", "expectedOutput": "Hello World", "mark": 1.0, "isSample": false}]
  }'

# SQL
curl -X POST http://localhost:8080/api/v1/code-evaluation/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "code": "SELECT COUNT(*) as count FROM employees WHERE department = \"Engineering\";",
    "language": "sql",
    "testCases": [{"input": "", "expectedOutput": "count\n3", "mark": 1.0, "isSample": false}]
  }'
```

### Automated Testing

Run the comprehensive test suite:

```bash
# Linux/Mac
./test-docker.sh

# Windows
test-docker.bat
```

## 🛠️ Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Python 3.10+
- GCC/G++ (for C/C++)
- MySQL 8.0+ (for SQL evaluation)

### Local Development

1. **Install dependencies:**
   ```bash
   # Ubuntu/Debian
   sudo apt-get update
   sudo apt-get install openjdk-17-jdk python3.10 gcc g++ mysql-client
   
   # macOS
   brew install openjdk@17 python@3.10 gcc mysql
   
   # Windows
   # Install Java, Python, MinGW, and MySQL separately
   ```

2. **Set up MySQL:**
   ```bash
   # Start MySQL service
   sudo systemctl start mysql
   
   # Create database and user
   mysql -u root -p < sql/init.sql
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## 📁 Project Structure

```
code-evaluation-service/
├── src/
│   ├── main/
│   │   ├── java/com/university/codeevaluation/
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── controllers/      # REST controllers
│   │   │   ├── models/           # DTOs and response models
│   │   │   └── services/         # Language evaluators
│   │   └── resources/
│   │       ├── application.yml   # Main configuration
│   │       └── application-docker.yml # Docker configuration
├── sql/
│   └── init.sql                  # Database initialization
├── Dockerfile                    # Docker image definition
├── docker-compose.yml           # Multi-container setup
├── test-docker.sh               # Linux/Mac test script
├── test-docker.bat              # Windows test script
└── README.md                    # This file
```

## 🔧 Configuration

### Application Properties

Key configuration options in `application.yml`:

```yaml
code-evaluation:
  timeout:
    seconds: 10          # Execution timeout
  memory:
    limit-mb: 512        # Memory limit per execution
  temp:
    directory: /tmp      # Temporary directory for code files
```

### Docker Configuration

Docker-specific settings in `docker-compose.yml`:

```yaml
services:
  code-evaluation-service:
    ports:
      - "8080:8080"      # Service port
    environment:
      - MYSQL_HOST=mysql # Database host
    volumes:
      - /tmp:/tmp        # Temp directory mount
```

## 🚨 Troubleshooting

### Common Issues

1. **Service not starting:**
   ```bash
   # Check logs
   docker-compose logs code-evaluation-service
   
   # Check health
   curl http://localhost:8080/actuator/health
   ```

2. **MySQL connection issues:**
   ```bash
   # Check MySQL logs
   docker-compose logs mysql
   
   # Test connection
   docker exec -it code-evaluation-service_mysql_1 mysql -u root -p
   ```

3. **Language evaluators not available:**
   ```bash
   # Check evaluator status
   curl http://localhost:8080/api/v1/code-evaluation/status
   ```

### Performance Tuning

- **Memory**: Increase `MAX_MEMORY_MB` in evaluators for memory-intensive code
- **Timeout**: Adjust `TIMEOUT_SECONDS` for longer-running programs
- **Concurrency**: Configure thread pool size in application properties

## 📈 Monitoring

### Health Checks

- **Service Health**: `GET /actuator/health`
- **Database Health**: Included in service health check
- **Evaluator Status**: `GET /api/v1/code-evaluation/status`

### Metrics

- **Execution Count**: Number of code evaluations
- **Success Rate**: Percentage of successful evaluations
- **Average Execution Time**: Mean execution time per language
- **Memory Usage**: Memory consumption per evaluation

## 🔒 Security Considerations

- **Code Isolation**: Each execution runs in a separate temporary directory
- **Resource Limits**: Time and memory limits prevent resource exhaustion
- **Input Validation**: All inputs are validated before processing
- **Container Security**: Non-root user execution in Docker

## 🤝 Integration

### Main Exam System Integration

```java
@Service
public class CodeEvaluationIntegrationService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public CodeExecutionResponse evaluateCode(CodeExecutionRequest request) {
        String url = "http://localhost:8080/api/v1/code-evaluation/evaluate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<CodeExecutionRequest> entity = new HttpEntity<>(request, headers);
        
        ResponseEntity<CodeExecutionResponse> response = restTemplate.postForEntity(
            url, entity, CodeExecutionResponse.class);
        
        return response.getBody();
    }
}
```

## 📄 License

This project is part of the AI Examination Management System.

## 🆘 Support

For issues and questions:
1. Check the troubleshooting section
2. Review the logs: `docker-compose logs`
3. Test individual components using the provided test scripts 