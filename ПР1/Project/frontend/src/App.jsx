import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { useEffect } from 'react';
import style from './App.module.css';
import './fonts.css';
import Navigate from './Navigate/Navigate';
import Main from './Main/Main';
import Login from './Form/Login/Login';
import Register from './Form/Register/Register';
import Editor from './Form/EditorFormul/EditorFormul'
import Service  from "./service/Service";
import Profile from './Form/test/Profile.jsx'

function AppContent() {
  const location = useLocation();

  useEffect(() => {
    if (location.pathname === '/register') {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'auto';
    }
  }, [location.pathname]);

  return (
    <>
      <Navigate />
      <Routes>
        <Route path="/" element={<Main />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/editor" element={<Editor />} />
        <Route path={"/service"} element={<Service/>}></Route>
        <Route path={"/me"} element={<Profile/>}></Route>
      </Routes>
    </>
  );
}

export default function App() {
  return (
    <Router>
      <AppContent />
    </Router>
  );
}
