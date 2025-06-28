#!/bin/bash

# Test script for Code Evaluation Service in Docker
# This script tests all supported programming languages

set -e

echo "🚀 Starting Code Evaluation Service Docker Test"
echo "================================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to wait for service to be ready
wait_for_service() {
    print_status "Waiting for service to be ready..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            print_success "Service is ready!"
            return 0
        fi
        print_status "Attempt $attempt/$max_attempts - Service not ready yet..."
        sleep 2
        attempt=$((attempt + 1))
    done
    
    print_error "Service failed to start within expected time"
    return 1
}

# Function to test a language
test_language() {
    local language=$1
    local test_name=$2
    local code=$3
    local expected_output=$4
    
    print_status "Testing $language: $test_name"
    
    # Create test request
    local request_data=$(cat <<EOF
{
    "code": "$code",
    "language": "$language",
    "testCases": [
        {
            "input": "",
            "expectedOutput": "$expected_output",
            "mark": 1.0,
            "isSample": false
        }
    ]
}
EOF
)
    
    # Send request
    local response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "$request_data" \
        http://localhost:8080/api/v1/code-evaluation/evaluate)
    
    # Check if request was successful
    if echo "$response" | grep -q '"success":true'; then
        print_success "$language test passed"
        echo "$response" | jq '.resultSummary' 2>/dev/null || echo "$response"
    else
        print_error "$language test failed"
        echo "$response" | jq '.message' 2>/dev/null || echo "$response"
    fi
    
    echo ""
}

# Function to test SQL with database
test_sql() {
    print_status "Testing SQL with database connection"
    
    local request_data='{
        "code": "SELECT COUNT(*) as count FROM employees WHERE department = \"Engineering\";",
        "language": "sql",
        "testCases": [
            {
                "input": "",
                "expectedOutput": "count\n3",
                "mark": 1.0,
                "isSample": false
            }
        ]
    }'
    
    local response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "$request_data" \
        http://localhost:8080/api/v1/code-evaluation/evaluate)
    
    if echo "$response" | grep -q '"success":true'; then
        print_success "SQL test passed"
        echo "$response" | jq '.resultSummary' 2>/dev/null || echo "$response"
    else
        print_error "SQL test failed"
        echo "$response" | jq '.message' 2>/dev/null || echo "$response"
    fi
    
    echo ""
}

# Function to test error cases
test_error_cases() {
    print_status "Testing error cases..."
    
    # Test compilation error (Java)
    local java_error_code='public class Solution {
    public static void main(String[] args) {
        System.out.println("Hello World"
    }
}'
    
    local request_data=$(cat <<EOF
{
    "code": "$java_error_code",
    "language": "java",
    "testCases": [
        {
            "input": "",
            "expectedOutput": "Hello World",
            "mark": 1.0,
            "isSample": false
        }
    ]
}
EOF
)
    
    local response=$(curl -s -X POST \
        -H "Content-Type: application/json" \
        -d "$request_data" \
        http://localhost:8080/api/v1/code-evaluation/evaluate)
    
    if echo "$response" | grep -q '"outputType":"COMPILATION_ERROR"'; then
        print_success "Compilation error test passed"
    else
        print_error "Compilation error test failed"
    fi
    
    echo ""
}

# Main test execution
main() {
    print_status "Starting Docker containers..."
    
    # Build and start containers
    docker-compose down -v 2>/dev/null || true
    docker-compose up -d --build
    
    # Wait for service to be ready
    wait_for_service
    
    print_status "Testing supported languages..."
    echo ""
    
    # Test Java
    test_language "java" "Hello World" \
        'public class Solution {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}' "Hello World"
    
    # Test Python
    test_language "python" "Simple Print" \
        'print("Hello World")' "Hello World"
    
    # Test C
    test_language "c" "Hello World" \
        '#include <stdio.h>
int main() {
    printf("Hello World");
    return 0;
}' "Hello World"
    
    # Test C++
    test_language "c++" "Hello World" \
        '#include <iostream>
int main() {
    std::cout << "Hello World";
    return 0;
}' "Hello World"
    
    # Test SQL
    test_sql
    
    # Test error cases
    test_error_cases
    
    print_status "Testing service endpoints..."
    
    # Test health endpoint
    if curl -s http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
        print_success "Health endpoint working"
    else
        print_error "Health endpoint failed"
    fi
    
    # Test supported languages endpoint
    local languages_response=$(curl -s http://localhost:8080/api/v1/code-evaluation/languages)
    if echo "$languages_response" | grep -q "java"; then
        print_success "Languages endpoint working"
        echo "$languages_response" | jq '.languages' 2>/dev/null || echo "$languages_response"
    else
        print_error "Languages endpoint failed"
    fi
    
    # Test evaluator status endpoint
    local status_response=$(curl -s http://localhost:8080/api/v1/code-evaluation/status)
    if echo "$status_response" | grep -q "java"; then
        print_success "Status endpoint working"
        echo "$status_response" | jq '.evaluators' 2>/dev/null || echo "$status_response"
    else
        print_error "Status endpoint failed"
    fi
    
    echo ""
    print_success "All tests completed!"
    print_status "Service is running at http://localhost:8080"
    print_status "Health check: http://localhost:8080/actuator/health"
    print_status "API documentation: http://localhost:8080/api/v1/code-evaluation"
}

# Run main function
main "$@" 