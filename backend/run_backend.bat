@echo off
setlocal

cd /d %~dp0

echo [1/4] Verificando Python...
where py >nul 2>nul
if %errorlevel%==0 (
    set "PY_CMD=py"
) else (
    where python >nul 2>nul
    if %errorlevel%==0 (
        set "PY_CMD=python"
    ) else (
        echo No se encontro Python en PATH.
        echo Instala Python 3.10+ y vuelve a ejecutar este archivo.
        pause
        exit /b 1
    )
)

echo [2/4] Verificando entorno virtual...
if not exist ".venv\Scripts\python.exe" (
    %PY_CMD% -m venv .venv
    if errorlevel 1 (
        echo No se pudo crear el entorno virtual.
        pause
        exit /b 1
    )
)

call .venv\Scripts\activate.bat
if errorlevel 1 (
    echo No se pudo activar el entorno virtual.
    pause
    exit /b 1
)

echo [3/4] Instalando dependencias...
python -m pip install -r requirements.txt
if errorlevel 1 (
    echo Fallo la instalacion de dependencias.
    pause
    exit /b 1
)

echo [4/4] Iniciando API en http://127.0.0.1:5000
python app.py

endlocal
