import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
/*
proxy:{
      '/api': 'http://localhost:5000',
    }
 */
export default defineConfig({
  plugins: [react()],
  server:{
    host: true,
    strictPort: true,
    port: 5173
  }
})
