export const mockUsers = {
  'candidate@example.com': { password: 'pass123', role: 'candidate' },
  'admin@example.com': { password: 'admin123', role: 'admin' },
  'manager@example.com': { password: 'man123', role: 'manager' },
};

export function login(email, password) {
  const user = mockUsers[email];
  if (user && user.password === password) {
    localStorage.setItem('role', user.role);
    localStorage.setItem('email', email);
    return user.role;
  }
  throw new Error('Invalid credentials');
}

export function logout() {
  localStorage.removeItem('role');
  localStorage.removeItem('email');
}

export function getRole() {
  return localStorage.getItem('role');
}
export function getUserName() {
  return localStorage.getItem('email');
}
export function getEmail() {
  return localStorage.getItem('email');
}
