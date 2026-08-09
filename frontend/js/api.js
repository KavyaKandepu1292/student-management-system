// Central place to configure and call the backend REST API.
const API_BASE = "http://localhost:8081/api";

const Auth = {
  getToken() { return localStorage.getItem("sms_token"); },
  getRole() { return localStorage.getItem("sms_role"); },
  getUsername() { return localStorage.getItem("sms_username"); },
  getStudentId() { return localStorage.getItem("sms_student_id"); },

  setSession({ token, role, username, studentId }) {
    localStorage.setItem("sms_token", token);
    localStorage.setItem("sms_role", role);
    localStorage.setItem("sms_username", username);
    if (studentId !== null && studentId !== undefined) {
      localStorage.setItem("sms_student_id", studentId);
    } else {
      localStorage.removeItem("sms_student_id");
    }
  },

  clearSession() {
    localStorage.removeItem("sms_token");
    localStorage.removeItem("sms_role");
    localStorage.removeItem("sms_username");
    localStorage.removeItem("sms_student_id");
  },

  isLoggedIn() { return !!this.getToken(); },
  isAdmin() { return this.getRole() === "ADMIN"; },

  logout() {
    this.clearSession();
    window.location.href = "index.html";
  },

  // Redirect to login if not authenticated; call at top of protected pages.
  requireLogin() {
    if (!this.isLoggedIn()) window.location.href = "index.html";
  },

  requireAdmin() {
    this.requireLogin();
    if (!this.isAdmin()) window.location.href = "students.html";
  }
};

async function apiRequest(path, { method = "GET", body = null } = {}) {
  const headers = { "Content-Type": "application/json" };
  const token = Auth.getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });

  if (res.status === 401) {
    Auth.clearSession();
    window.location.href = "index.html";
    throw new Error("Session expired. Please log in again.");
  }

  let data = null;
  const text = await res.text();
  if (text) {
    try { data = JSON.parse(text); } catch { data = text; }
  }

  if (!res.ok) {
    const message = (data && data.message) ? data.message : `Request failed (${res.status})`;
    const err = new Error(message);
    err.fieldErrors = data && data.fieldErrors ? data.fieldErrors : null;
    err.status = res.status;
    throw err;
  }

  return data;
}
