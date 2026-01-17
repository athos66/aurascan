@echo off
echo Configuring git remote for aurascan...
:: Try to add the remote (will fail if it already exists, which is fine)
git remote add origin https://github.com/athos66/aurascan.git

:: Ensure the remote url is correct (updates it if it already existed)
git remote set-url origin https://github.com/athos66/aurascan.git

echo.
echo Current Remotes:
git remote -v
echo.
echo Done.
pause
