# Backend API (Servicio Externo)

Esta carpeta contiene un backend Flask independiente del proyecto Android.
Se puede mover y ejecutar en cualquier entorno como servicio externo.

## Requisitos

- Python 3.10 o superior
- Recomendado: Python 3.12+ (probado en Python 3.14.4)
- `pip` y `venv` disponibles

## Estructura mínima

- `app.py`: API REST
- `requirements.txt`: dependencias Python
- `reports.db`: base SQLite (se crea automáticamente al iniciar)

## 1) Clonar o copiar esta carpeta en otro entorno

Ejemplo:

```bash
cp -r backend /ruta/nueva/mi-backend
cd /ruta/nueva/mi-backend
```

## 2) Crear entorno virtual

Linux/macOS:

```bash
python3 -m venv .venv
source .venv/bin/activate
```

Windows (PowerShell):

```powershell
py -m venv .venv
.venv\Scripts\Activate.ps1
```

Windows (CMD):

```bat
py -m venv .venv
.venv\Scripts\activate.bat
```

Si `py` no existe en tu instalación de Windows, usa:

```powershell
python -m venv .venv
```

Si PowerShell bloquea la activación por políticas de ejecución:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.venv\Scripts\Activate.ps1
```

## 3) Instalar dependencias

Linux/macOS:

```bash
pip install -r requirements.txt
```

Windows (PowerShell o CMD):

```bat
py -m pip install -r requirements.txt
```

## 4) Ejecutar la API

Linux/macOS:

```bash
python app.py
```

Windows (PowerShell o CMD):

```bat
py app.py
```

Windows (opcion rapida con doble clic):

```bat
run_backend.bat
```

Este script crea `.venv` si no existe, instala dependencias y levanta la API.

La API inicia en:

- `http://127.0.0.1:5000`
- `http://localhost:5000`

Para Android Emulator, usar:

- `http://10.0.2.2:5000/`

## 5) Verificación rápida

Linux/macOS:

```bash
curl http://127.0.0.1:5000/categories
curl http://127.0.0.1:5000/reports
```

Windows PowerShell:

```powershell
Invoke-RestMethod http://127.0.0.1:5000/categories
Invoke-RestMethod http://127.0.0.1:5000/reports
```

Windows CMD (si tienes curl disponible):

```bat
curl http://127.0.0.1:5000/categories
curl http://127.0.0.1:5000/reports
```

## Endpoints disponibles

- `GET /reports`
- `POST /reports`
- `GET /reports/{id}`
- `PUT /reports/{id}`
- `GET /categories`
- `POST /users/register`

## Payload esperado para reportes

`POST /reports` y `PUT /reports/{id}` (actualización completa) esperan:

```json
{
  "title": "string",
  "description": "string",
  "category": "string",
  "priority": "string",
  "status": "string",
  "timestamp": 1716959500000
}
```

## Notas para despliegue futuro (nube)

- Esta implementación usa servidor de desarrollo Flask (`debug=True`) para demo local.
- Antes de desplegar en nube, ejecutar con `debug=False` y detrás de un servidor WSGI/ASGI administrado.
- Si migran de SQLite a un motor externo (PostgreSQL/MySQL), mantener el mismo contrato REST para no romper Android.
