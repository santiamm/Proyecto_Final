from flask import Flask, request, jsonify
from flask_cors import CORS
import sqlite3

app = Flask(__name__)
CORS(app)

DB_FILE = "reports.db"


def get_conn():
    conn = sqlite3.connect(DB_FILE)
    conn.row_factory = sqlite3.Row
    return conn


def init_db():
    conn = get_conn()
    c = conn.cursor()
    c.execute(
        """CREATE TABLE IF NOT EXISTS reports
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  title TEXT NOT NULL,
                  description TEXT NOT NULL,
                  category TEXT NOT NULL,
                  priority TEXT NOT NULL,
                  status TEXT NOT NULL,
                  timestamp INTEGER NOT NULL)"""
    )
    conn.commit()
    conn.close()


def row_to_report(row):
    return {
        "id": row["id"],
        "title": row["title"],
        "description": row["description"],
        "category": row["category"],
        "priority": row["priority"],
        "status": row["status"],
        "timestamp": row["timestamp"],
    }


def validate_report_payload(data):
    required = ["title", "description", "category", "priority", "status", "timestamp"]
    missing = [field for field in required if field not in data]
    return missing


@app.route("/reports", methods=["GET"])
def get_reports():
    conn = get_conn()
    c = conn.cursor()
    c.execute(
        "SELECT id, title, description, category, priority, status, timestamp FROM reports ORDER BY timestamp DESC"
    )
    rows = c.fetchall()
    conn.close()
    return jsonify([row_to_report(row) for row in rows])


@app.route("/reports", methods=["POST"])
def create_report():
    data = request.get_json(silent=True) or {}
    missing = validate_report_payload(data)
    if missing:
        return jsonify({"error": "Missing fields", "fields": missing}), 400

    conn = get_conn()
    c = conn.cursor()
    c.execute(
        """INSERT INTO reports (title, description, category, priority, status, timestamp)
                 VALUES (?, ?, ?, ?, ?, ?)""",
        (
            data["title"],
            data["description"],
            data["category"],
            data["priority"],
            data["status"],
            data["timestamp"],
        ),
    )
    conn.commit()
    report_id = c.lastrowid
    c.execute(
        "SELECT id, title, description, category, priority, status, timestamp FROM reports WHERE id = ?",
        (report_id,),
    )
    row = c.fetchone()
    conn.close()
    return jsonify(row_to_report(row)), 201


@app.route("/reports/<int:report_id>", methods=["PUT"])
def update_report(report_id):
    data = request.get_json(silent=True) or {}
    conn = get_conn()
    c = conn.cursor()
    c.execute("SELECT id FROM reports WHERE id = ?", (report_id,))
    exists = c.fetchone()
    if not exists:
        conn.close()
        return jsonify({"error": "Not found"}), 404

    if "status" in data and len(data.keys()) == 1:
        c.execute("UPDATE reports SET status = ? WHERE id = ?", (data["status"], report_id))
    else:
        missing = validate_report_payload(data)
        if missing:
            conn.close()
            return jsonify({"error": "Missing fields", "fields": missing}), 400
        c.execute(
            """UPDATE reports
               SET title = ?, description = ?, category = ?, priority = ?, status = ?, timestamp = ?
               WHERE id = ?""",
            (
                data["title"],
                data["description"],
                data["category"],
                data["priority"],
                data["status"],
                data["timestamp"],
                report_id,
            ),
        )

    conn.commit()
    c.execute(
        "SELECT id, title, description, category, priority, status, timestamp FROM reports WHERE id = ?",
        (report_id,),
    )
    row = c.fetchone()
    conn.close()
    return jsonify(row_to_report(row))


@app.route("/reports/<int:report_id>", methods=["GET"])
def get_report(report_id):
    conn = get_conn()
    c = conn.cursor()
    c.execute(
        "SELECT id, title, description, category, priority, status, timestamp FROM reports WHERE id = ?",
        (report_id,),
    )
    row = c.fetchone()
    conn.close()
    if row:
        return jsonify(row_to_report(row))
    return jsonify({"error": "Not found"}), 404


@app.route("/categories", methods=["GET"])
def get_categories():
    categories = ["Alumbrado Público", "Vías e Infraestructura", "Seguridad Comunitaria", "Aseo Urbano"]
    return jsonify(categories)


@app.route("/users/register", methods=["POST"])
def register_user():
    data = request.get_json(silent=True) or {}
    email = data.get("email", "")
    if not email:
        return jsonify({"error": "email is required"}), 400
    return jsonify({"message": "User registered", "email": email}), 201


if __name__ == "__main__":
    init_db()
    app.run(host="0.0.0.0", port=5000, debug=True)
