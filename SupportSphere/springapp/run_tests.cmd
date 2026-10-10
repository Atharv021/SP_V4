@echo off
echo =====================================
echo    Running SupportSphere Tests
echo =====================================

call mvn test | findstr /R "Tests run: Failure: Error: Skipped:"

if %errorlevel% neq 0 (
    echo =====================================
    echo   SOME TESTS FAILED! X
    echo   Check the detailed output above.
    echo =====================================
) else (
    echo =====================================
    echo   ALL TESTS PASSED SUCCESSFULLY! 
    echo =====================================
)
