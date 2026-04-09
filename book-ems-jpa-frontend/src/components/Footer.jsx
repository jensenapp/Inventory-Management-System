
import React from 'react';

export default function Footer() {
  return (
    
    <footer className="bg-dark text-white text-center py-3 mt-auto">
      <div className="container">
        <p className="mb-0">
          © {new Date().getFullYear()} Book EMS 書本管理系統. All rights reserved.
        </p>
      </div>
    </footer>
  );
}