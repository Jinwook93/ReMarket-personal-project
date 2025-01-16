// App.js
import React from "react";
import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Home from "./pages/Home";
import About from "./pages/Home copy";
import Header from "./components/Header";
import Navigation from "./components/Navigation";
import LoginForm from "./pages/LoginForm";


function App() {
  return (
    <Router>
      <Header/>
      <Navigation/>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About/>} />
        <Route path="/login" element={<LoginForm />} />
      </Routes>
    </Router>
  );
}

export default App;
