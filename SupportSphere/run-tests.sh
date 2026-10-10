#!/bin/bash
set -euo pipefail

# Determine script location
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="${SCRIPT_DIR}/springapp"

echo "====================================="
echo "   Running SupportSphere Tests"
echo "====================================="

# Check backend directory
if [ ! -d "${BACKEND_DIR}" ] || [ ! -f "${BACKEND_DIR}/pom.xml" ]; then
    echo "ERROR: Backend directory 'springapp/' or pom.xml not found at ${BACKEND_DIR}."
    echo "Please ensure you run this script from the repository root."
    exit 1
fi

# Check Java 17
if ! command -v java >/dev/null 2>&1; then
    echo "ERROR: Java is not installed or not in PATH."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ ! "$JAVA_VERSION" == 17* ]]; then
    echo "WARNING: Expected Java 17, but found Java $JAVA_VERSION."
fi

# Check Maven
if ! command -v mvn >/dev/null 2>&1; then
    echo "ERROR: Maven ('mvn') is not installed or not in PATH."
    echo "Please install Maven and ensure it is available in your PATH to run the tests."
    exit 1
fi

# Run tests
echo "Changing directory to ${BACKEND_DIR}..."
cd "${BACKEND_DIR}"

echo "Executing 'mvn clean test'..."
if mvn clean test; then
    echo "====================================="
    echo "  ALL TESTS PASSED SUCCESSFULLY! ✅"
    echo "====================================="
    exit 0
else
    EXIT_CODE=$?
    echo "====================================="
    echo "  SOME TESTS FAILED! ❌"
    echo "  Check the detailed output above."
    echo "  Detailed reports: ${BACKEND_DIR}/target/surefire-reports"
    echo "====================================="
    exit $EXIT_CODE
fi
