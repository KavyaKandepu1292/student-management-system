function renderNav(active) {
  const nav = document.getElementById("navLinks");
  if (!nav) return;

  const links = [];
  if (Auth.isAdmin()) {
    links.push(["dashboard", "dashboard.html", "Dashboard"]);
  }
  links.push(["students", "students.html", "Students"]);
  links.push(["courses", "courses.html", "Courses"]);
  links.push(["attendance", "attendance.html", "Attendance"]);

  nav.innerHTML = links.map(([key, href, label]) =>
    `<a href="${href}" class="${key === active ? 'active' : ''}">${label}</a>`
  ).join("") + `<a href="#" onclick="Auth.logout(); return false;">Logout (${Auth.getUsername() || ''})</a>`;
}
