@echo off
echo Pushing code to origin...
:: Try pushing 'main' first
git push -u origin main

:: If 'main' fails, try 'master' as a fallback
if %errorlevel% neq 0 (
    echo.
    echo 'main' branch push failed or does not exist. Attempting 'master'...
    git push -u origin master
)

echo.
echo Push operation completed.
pause
