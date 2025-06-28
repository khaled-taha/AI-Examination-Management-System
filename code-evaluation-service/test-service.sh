#!/bin/bash

# Test script for Code Evaluation Service
# Make sure the service is running on port 8081

BASE_URL="http://localhost:8081/code-evaluation/api/v1/code-evaluation"

echo "Testing Code Evaluation Service..."
echo "=================================="

# Test 1: Health Check
echo "1. Testing Health Check..."
curl -s "$BASE_URL/health" | jq .
echo ""

# Test 2: Get Supported Languages
echo "2. Testing Get Supported Languages..."
curl -s "$BASE_URL/languages" | jq .
echo ""

# Test 3: Get Evaluator Status
echo "3. Testing Get Evaluator Status..."
curl -s "$BASE_URL/status" | jq .
echo ""

# Test 4: Evaluate Java Code
echo "4. Testing Java Code Evaluation..."
curl -s -X POST "$BASE_URL/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "public class Solution {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World\");\n    }\n}",
    "language": "java",
    "testCases": [
      {
        "input": "",
        "expectedOutput": "Hello World",
        "mark": 1.0,
        "isSample": false
      }
    ]
  }' | jq .
echo ""

# Test 5: Evaluate Python Code
echo "5. Testing Python Code Evaluation..."
curl -s -X POST "$BASE_URL/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "n = int(input())\nprint(n * 2)",
    "language": "python",
    "testCases": [
      {
        "input": "5",
        "expectedOutput": "10",
        "mark": 1.0,
        "isSample": false
      },
      {
        "input": "10",
        "expectedOutput": "20",
        "mark": 1.0,
        "isSample": false
      }
    ]
  }' | jq .
echo ""

# Test 6: Evaluate C Code
echo "6. Testing C Code Evaluation..."
curl -s -X POST "$BASE_URL/evaluate" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "#include <stdio.h>\n\nint main() {\n    int n;\n    scanf(\"%d\", &n);\n    printf(\"%d\\n\", n * 2);\n    return 0;\n}",
    "language": "c",
    "testCases": [
      {
        "input": "5",
        "expectedOutput": "10",
        "mark": 1.0,
        "isSample": false
      }
    ]
  }' | jq .
echo ""

echo "Testing completed!" 