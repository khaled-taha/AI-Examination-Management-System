# 🚀 Postman Testing Guide for Code Evaluation Service

This guide will help you test the Code Evaluation Service using Postman with comprehensive examples for all supported programming languages.

## 📋 Prerequisites

1. **Docker containers running:**
   ```bash
   # Start the service
   docker-compose up -d --build
   
   # Wait for service to be ready (check health)
   curl http://localhost:8080/actuator/health
   ```

2. **Postman installed** (Desktop or Web version)

## 🔧 Setup Postman

### Step 1: Import the Collection

1. **Download the collection file:**
   - File: `Code-Evaluation-Service.postman_collection.json`
   - Located in the `code-evaluation-service` directory

2. **Import into Postman:**
   - Open Postman
   - Click "Import" button
   - Select the downloaded JSON file
   - The collection will be imported with all test cases

### Step 2: Configure Environment Variables

1. **Set the base URL:**
   - The collection uses `{{baseUrl}}` variable
   - Default value: `http://localhost:8080`
   - If your service runs on a different port, update this variable

## 🧪 Testing Strategy

### 1. **Health & Status Tests** (Start Here)

Begin with these basic tests to ensure the service is running:

- **Health Check**: `GET /actuator/health`
- **Get Supported Languages**: `GET /api/v1/code-evaluation/languages`
- **Get Evaluator Status**: `GET /api/v1/code-evaluation/status`

**Expected Results:**
- Health Check: `{"status":"UP"}`
- Languages: List of supported languages (java, python, c, c++, sql)
- Status: Availability status of each evaluator

### 2. **Language-Specific Tests**

Test each programming language with various scenarios:

#### **Java Tests**
- ✅ **Hello World (Success)**: Basic successful execution
- ❌ **Compilation Error**: Missing semicolon
- ❌ **Runtime Error**: Division by zero
- ⚠️ **Multiple Test Cases**: Partial success scenario

#### **Python Tests**
- ✅ **Hello World (Success)**: Basic successful execution
- ❌ **Runtime Error**: Division by zero
- ⏱️ **Time Limit Exceeded**: Infinite loop
- ✅ **Input Processing**: Reading from stdin

#### **C Tests**
- ✅ **Hello World (Success)**: Basic successful execution
- ❌ **Compilation Error**: Missing semicolon
- ✅ **Input Processing**: Using scanf

#### **C++ Tests**
- ✅ **Hello World (Success)**: Basic successful execution
- ✅ **Input Processing**: Using cin/cout

#### **SQL Tests**
- ✅ **Simple Query**: Basic SELECT with WHERE
- ✅ **Complex Query**: GROUP BY with aggregation
- ❌ **Syntax Error**: Missing quotes
- ✅ **Student Data Query**: Different table

### 3. **Error Cases**

Test error handling and validation:

- **Invalid Language**: Unsupported language (javascript)
- **Empty Code**: Empty code submission
- **Missing Test Cases**: No test cases provided

## 📊 Understanding Response Format

### Successful Response Example:
```json
{
  "success": true,
  "message": "All test cases passed successfully",
  "totalScore": 2.0,
  "resultSummary": "ALL PASSED: Passed 2/2 test cases with total score 2.00 (Memory: 45 KB)",
  "testCaseResults": [
    {
      "input": "5",
      "expectedOutput": "10",
      "actualOutput": "10",
      "passed": true,
      "markObtained": 1.0,
      "executionTimeMs": 150,
      "memoryUsedKb": 45,
      "feedback": "Test case passed",
      "isSample": false,
      "errorType": "PASSED"
    }
  ],
  "executionTimeMs": 200,
  "memoryUsedKb": 45,
  "outputType": "ALL_PASSED",
  "passedCases": 2,
  "failedCases": 0,
  "totalCases": 2
}
```

### Error Response Example:
```json
{
  "success": false,
  "message": "Compilation error occurred",
  "totalScore": 0.0,
  "resultSummary": "COMPILATION ERROR: Passed 0/1 test cases with total score 0.00 (Memory: 12 KB)",
  "testCaseResults": [
    {
      "input": "",
      "expectedOutput": "Hello World",
      "actualOutput": "ERROR: COMPILATION_ERROR: Solution.java:3: error: ';' expected",
      "passed": false,
      "markObtained": 0.0,
      "executionTimeMs": 50,
      "memoryUsedKb": 12,
      "feedback": "Compilation error: Solution.java:3: error: ';' expected",
      "isSample": false,
      "errorType": "COMPILATION_ERROR",
      "errorMessage": "Solution.java:3: error: ';' expected"
    }
  ],
  "executionTimeMs": 50,
  "memoryUsedKb": 12,
  "outputType": "COMPILATION_ERROR",
  "passedCases": 0,
  "failedCases": 1,
  "totalCases": 1
}
```

## 🎯 Output Types

The service categorizes results into these types:

- **`ALL_PASSED`**: All test cases passed successfully
- **`COMPILATION_ERROR`**: Code failed to compile (Java, C, C++)
- **`RUNTIME_ERROR`**: Code compiled but failed during execution
- **`TIME_LIMIT_EXCEEDED`**: Execution exceeded 10-second timeout
- **`MEMORY_LIMIT_EXCEEDED`**: Execution exceeded 512MB memory limit
- **`PARTIAL_SUCCESS`**: Some test cases passed, some failed

## 🔍 Key Response Fields

- **`success`**: Boolean indicating overall success
- **`totalScore`**: Sum of marks from passed test cases only
- **`outputType`**: Categorized result type
- **`passedCases`/`failedCases`**: Count of passed/failed test cases
- **`testCaseResults`**: Detailed results for each test case
- **`executionTimeMs`**: Total execution time in milliseconds
- **`memoryUsedKb`**: Memory usage in kilobytes

## 🚨 Common Issues & Solutions

### 1. **Service Not Responding**
- Check if Docker containers are running: `docker-compose ps`
- Verify health endpoint: `GET /actuator/health`
- Check logs: `docker-compose logs code-evaluation-service`

### 2. **SQL Tests Failing**
- Ensure MySQL container is running
- Check database connection in logs
- Verify sample data is loaded

### 3. **Language Evaluators Not Available**
- Check evaluator status: `GET /api/v1/code-evaluation/status`
- Verify all compilers/interpreters are installed in container
- Check container logs for installation errors

### 4. **Timeout Issues**
- Default timeout is 10 seconds
- For longer-running code, adjust timeout in application properties
- Check if code has infinite loops

## 🎯 Testing Tips

### 1. **Start Simple**
- Begin with "Hello World" tests for each language
- Verify basic functionality before testing complex scenarios

### 2. **Test Error Cases**
- Don't just test success cases
- Test compilation errors, runtime errors, and timeouts
- Verify proper error categorization

### 3. **Check Response Details**
- Examine `testCaseResults` for detailed feedback
- Verify `outputType` categorization
- Check execution time and memory usage

### 4. **Test Multiple Scenarios**
- Test with different input types
- Test with multiple test cases
- Test edge cases and boundary conditions

## 📈 Performance Testing

### Monitor These Metrics:
- **Execution Time**: Should be under 10 seconds
- **Memory Usage**: Should be under 512MB
- **Response Time**: API response time
- **Concurrent Requests**: Test multiple simultaneous evaluations

### Example Performance Test:
```bash
# Test concurrent requests
for i in {1..5}; do
  curl -X POST http://localhost:8080/api/v1/code-evaluation/evaluate \
    -H "Content-Type: application/json" \
    -d '{"code":"print(\"Hello World\")","language":"python","testCases":[{"input":"","expectedOutput":"Hello World","mark":1.0,"isSample":false}]}' &
done
wait
```

## 🔧 Custom Test Cases

### Creating Your Own Tests:

1. **Copy an existing request**
2. **Modify the code and test cases**
3. **Update expected outputs**
4. **Test different scenarios**

### Example Custom Test:
```json
{
  "code": "def fibonacci(n):\n    if n <= 1:\n        return n\n    return fibonacci(n-1) + fibonacci(n-2)\n\nn = int(input())\nprint(fibonacci(n))",
  "language": "python",
  "testCases": [
    {
      "input": "5",
      "expectedOutput": "5",
      "mark": 2.0,
      "isSample": true
    },
    {
      "input": "10",
      "expectedOutput": "55",
      "mark": 2.0,
      "isSample": false
    }
  ]
}
```

## 🎉 Success Criteria

Your testing is successful when:

1. ✅ **Health endpoint returns UP status**
2. ✅ **All language evaluators are available**
3. ✅ **Basic "Hello World" tests pass for all languages**
4. ✅ **Error cases are properly categorized**
5. ✅ **SQL queries work with sample database**
6. ✅ **Response format matches expected structure**
7. ✅ **Performance is within acceptable limits**

## 📞 Troubleshooting

If you encounter issues:

1. **Check Docker logs**: `docker-compose logs -f`
2. **Verify service health**: `GET /actuator/health`
3. **Check evaluator status**: `GET /api/v1/code-evaluation/status`
4. **Review response details** for specific error messages
5. **Test with simpler code** to isolate issues

Happy Testing! 🚀 