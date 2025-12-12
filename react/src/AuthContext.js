import React, { createContext, useState, useContext } from 'react';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [currentUser, setCurrentUser] = useState(() => {
    const email = localStorage.getItem('email');
    const role = localStorage.getItem('role');
    const id = localStorage.getItem('id');
    if (email && role) {
      return { email, role, id };
    }
    return null;
  });

  const login = (userData) => {
    localStorage.setItem('role', userData.roleCode);
    localStorage.setItem('email', userData.email); 
    localStorage.setItem('id', userData.userId);
    localStorage.setItem('userName', userData.firstName +  userData.lastName );
    setCurrentUser({
      role: userData.roleCode,
      email: userData.email,
      id: userData.userId,
    });
  };

  const logout = () => {
    localStorage.removeItem('role');
    localStorage.removeItem('email');
    localStorage.removeItem('id');
    localStorage.removeItem('userName');
    setCurrentUser(null);
  };

  const value = { currentUser, login, logout };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};