@echo off
chcp 65001 >nul
echo ========================================
echo   AI 代码评审工具启动脚本
echo ========================================
echo.

:: 加载 .env 环境变量
echo [1/3] 加载配置文件...
for /f "usebackq tokens=1,2 delims==" %%a in ("%~dp0.env") do (
    if not "%%b"=="" set "%%a=%%b"
)

:: 检查必要的环境变量
if "%DEEPSEEK_APIKEYSECRET%"=="" (
    echo [错误] 请先配置 .env 文件中的 DEEPSEEK_APIKEYSECRET
    pause
    exit /b 1
)

:: 启动后端
echo [2/3] 启动后端服务 (端口 8091)...
start "后端服务" cmd /k "cd /d %~dp0 && mvn spring-boot:run -pl openai-code-review-test"

:: 等待后端启动
echo 等待后端启动...
timeout /t 10 /nobreak >nul

:: 启动前端
echo [3/3] 启动前端服务 (端口 3000)...
cd /d "%~dp0frontend" && start "前端服务" cmd /k "npm run dev"

echo.
echo ========================================
echo   启动完成！
echo   前端地址: http://localhost:3000
echo   后端地址: http://localhost:8091
echo ========================================
echo.
echo 请关闭此窗口或按任意键退出...
pause >nul
