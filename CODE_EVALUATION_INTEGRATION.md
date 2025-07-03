# Code Evaluation Integration

This document describes the integration between the AI Examination Management System and the Code Evaluation Service for automated code assessment.

## Overview

The integration allows students to submit code answers for coding questions, which are then automatically evaluated against predefined test cases using the external Code Evaluation Service. The evaluation process is triggered at the end of the exam, ensuring that students can make multiple submissions during the exam and only the final evaluation is performed.

## Architecture

### Components

1. **CodeEvaluationConfig** - Configuration for RestTemplate and service URLs
2. **CodeEvaluationIntegrationService** - Service layer for integration logic
3. **CodeEvaluationController** - REST endpoints for health checks and status
4. **Enhanced ExamServiceImpl** - Modified to trigger async code evaluation at exam end

### Flow

1. Student submits code answer via `submitCodeAnswer` endpoint
2. System validates the request and saves the answer with "evaluation pending" status
3. Student can make multiple submissions during the exam (latest submission overrides previous)
4. When student calls `endExam` endpoint, async code evaluation is triggered for all coding answers
5. Code Evaluation Service processes the code against test cases
6. Results are saved back to the database, and exam attempt score is updated
7. Student can check results via `getCodingTestResults` endpoint

## Configuration

### Application Properties

```yaml
code:
  evaluation:
    service:
      url: http://localhost:8084  # Code Evaluation Service URL
      timeout: 30000              # Request timeout in milliseconds
```

### Service Dependencies

The integration requires:
- Spring Boot with `@EnableAsync`
- RestTemplate for HTTP communication
- JPA repositories for data persistence

## API Endpoints

### Code Submission
- **POST** `/api/v1/student-exam/submit-code-answer/{attemptId}`
  - Submits code answers with "evaluation pending" status
  - Returns immediate response - evaluation will be done at exam end

### Exam End
- **POST** `/api/v1/student-exam/end-exam/{attemptId}`
  - Ends the exam and triggers async evaluation for all coding answers
  - Returns exam attempt with initial score (will be updated after evaluation)

### Result Retrieval
- **GET** `/api/v1/student-exam/coding-test-results/{codeAnswerId}`
  - Retrieves detailed test case results for a code answer

### Health Monitoring
- **GET** `/api/v1/code-evaluation/health`
  - Checks if the Code Evaluation Service is available
- **GET** `/api/v1/code-evaluation/status`
  - Returns detailed integration status

## Data Models

### CodeEvaluationRequestDTO
```java
{
  "code": "String - Student's submitted code",
  "language": "String - Programming language (e.g., java, python, cpp)",
  "testCases": [
    {
      "input": "String - Test input",
      "expectedOutput": "String - Expected output",
      "mark": "double - Points for this test case",
      "isSample": "boolean - Whether this is a sample test case",
      "testCaseId": "UUID - Original test case ID"
    }
  ]
}
```

### CodeEvaluationResponseDTO
```java
{
  "success": "boolean - Whether evaluation was successful",
  "message": "String - Status message",
  "totalScore": "double - Total score achieved",
  "resultSummary": "String - Summary of results",
  "testCaseResults": [
    {
      "input": "String - Test input used",
      "expectedOutput": "String - Expected output",
      "actualOutput": "String - Actual output from code",
      "passed": "boolean - Whether test case passed",
      "markObtained": "double - Points obtained",
      "executionTimeMs": "long - Execution time in milliseconds",
      "memoryUsedKb": "long - Memory used in KB",
      "feedback": "String - Detailed feedback",
      "testCaseId": "UUID - Original test case ID"
    }
  ],
  "outputType": "ENUM - Type of output (COMPILATION_ERROR, RUNTIME_ERROR, etc.)",
  "passedCases": "int - Number of passed test cases",
  "failedCases": "int - Number of failed test cases",
  "totalCases": "int - Total number of test cases"
}
```

## Database Schema

### StudentAnswerCode
- Enhanced with `totalScore` and `resultSummary` fields
- Stores the submitted code and evaluation results

### StudentCodingTestResult
- Stores individual test case results
- Links to both `StudentAnswerCode` and `CodingTestCase`
- Includes execution metrics and feedback

## Async Processing

### Benefits
- Non-blocking exam end process
- Scalable evaluation processing
- Better user experience during exam

### Implementation
- Uses Spring's `@Async` annotation
- Separate thread pool for evaluation tasks
- Automatic error handling and logging

### Result Override
When a student resubmits code for the same question:
1. Previous results are automatically deleted
2. New evaluation is triggered at exam end
3. Updated results replace the old ones
4. Student gets the latest evaluation

## Error Handling

### Service Unavailable
- If Code Evaluation Service is down, students receive "Service unavailable" message
- No evaluation is performed, but code is still saved
- System continues to function normally

### Evaluation Failures
- Network timeouts are handled gracefully
- Evaluation errors are logged and stored
- Students receive error feedback

### Database Errors
- Transaction rollback on database failures
- Comprehensive error logging
- Graceful degradation

## Monitoring and Logging

### Health Checks
- Regular health checks to Code Evaluation Service
- Integration status monitoring
- Service availability tracking

### Logging
- Detailed logs for evaluation process
- Error tracking and debugging information
- Performance metrics logging

## Security Considerations

### Input Validation
- Code submission validation
- Language and test case verification
- Request size limits

### Service Communication
- HTTPS for production environments
- Request timeout configuration
- Error handling for security

## Testing

### Unit Tests
- Service layer testing
- DTO validation testing
- Error handling testing

### Integration Tests
- End-to-end code submission flow
- Async processing verification
- Database consistency checks

### Manual Testing
1. Start both services
2. Create a coding question with test cases
3. Submit code answer multiple times
4. End exam to trigger evaluation
5. Check results in database

## Deployment

### Prerequisites
- Code Evaluation Service running on configured URL
- Database with required schema
- Proper network connectivity

### Configuration
- Set appropriate service URLs for environment
- Configure timeouts based on expected load
- Enable async processing

### Monitoring
- Monitor service health endpoints
- Track evaluation success rates
- Monitor database performance

## Troubleshooting

### Common Issues

1. **Service Not Available**
   - Check Code Evaluation Service is running
   - Verify network connectivity
   - Check service URL configuration

2. **Evaluation Timeouts**
   - Increase timeout configuration
   - Check Code Evaluation Service performance
   - Monitor system resources

3. **Database Errors**
   - Check database connectivity
   - Verify schema consistency
   - Review transaction logs

### Debug Steps
1. Check health endpoint: `/api/v1/code-evaluation/health`
2. Review application logs
3. Verify database state
4. Test Code Evaluation Service directly

## Future Enhancements

### Planned Features
- Real-time evaluation status updates
- Batch evaluation processing
- Advanced code analysis
- Performance optimization

### Scalability
- Load balancing for Code Evaluation Service
- Database optimization
- Caching strategies
- Horizontal scaling support 