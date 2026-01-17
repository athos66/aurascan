@echo off
echo Syncing local code with remote...
echo.

:: Fetch latest info from remote
git fetch origin

:: Try pulling 'main' with unrelated histories allowed (useful if repo was init'd locally)
echo Attempting to pull 'main'...
git pull origin main --allow-unrelated-histories

:: If 'main' failed (e.g. branch not found), try 'master'
if %errorlevel% neq 0 (
    echo.
    echo 'main' pull failed or branch not found. Attempting 'master'...
    git pull origin master --allow-unrelated-histories
)

echo.
echo Sync operation completed. 
echo If you see 'CONFLICT' messages above, you may need to resolve them manually in your files.
pause
