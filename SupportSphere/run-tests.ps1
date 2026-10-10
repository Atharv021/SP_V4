$ErrorActionPreference = "Stop"

Write-Host "====================================="
Write-Host "   Running SupportSphere Tests"
Write-Host "====================================="

# Navigate to the backend directory relative to the script
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$BackendDir = Join-Path $ScriptDir "springapp"

Write-Host "Changing directory to $BackendDir..."
Set-Location $BackendDir

# Determine if Maven is globally installed
$mvnCommand = "mvn"
if (-not (Get-Command $mvnCommand -ErrorAction SilentlyContinue)) {
    # If not, attempt to fallback to IntelliJ's bundled Maven (common on Windows without global mvn)
    $intellijMaven = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition*\plugins\maven\lib\maven3\bin\mvn.cmd"
    $found = Resolve-Path $intellijMaven -ErrorAction SilentlyContinue
    if ($found) {
        $mvnCommand = $found[-1].Path
    } else {
        Write-Error "ERROR: Maven ('mvn') is not installed or not in PATH."
        exit 1
    }
}

Write-Host "Executing 'mvn clean test'..."

# Execute maven
try {
    & $mvnCommand clean test
    $exitCode = $LASTEXITCODE
} catch {
    Write-Error "Failed to execute maven command."
    exit 1
}

if ($exitCode -eq 0) {
    Write-Host "====================================="
    Write-Host "  ALL TESTS PASSED SUCCESSFULLY! ✅"
    Write-Host "====================================="
    exit 0
} else {
    Write-Host "====================================="
    Write-Host "  SOME TESTS FAILED! ❌"
    Write-Host "  Check the detailed output above."
    Write-Host "  Detailed reports: $BackendDir\target\surefire-reports"
    Write-Host "====================================="
    exit $exitCode
}
