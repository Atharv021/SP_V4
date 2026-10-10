#!/bin/bash

echo "====================================="
echo "   Running SupportSphere Tests"
echo "====================================="

# Run tests and pipe output to terminal while filtering summary lines
mvn test | grep -E "Tests run: |Failure: |Error: |Skipped: "

# Check if the build succeeded or failed
if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo "====================================="
    echo "  ALL TESTS PASSED SUCCESSFULLY! ✅"
    echo "====================================="
else
    echo "====================================="
    echo "  SOME TESTS FAILED! ❌"
    echo "  Check the detailed output above."
    echo "====================================="
fi
