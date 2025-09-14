import style from './Service.module.css';
import { Routes, Route, Link, useLocation } from 'react-router-dom';

export default function Service() {
  const location = useLocation();
  
  const isLatex = location.pathname === '/service/latex';

  return (
    <section className={style.Service}>
        <h1>На данной странице отображаются все созданные нами сервисы</h1>

        <section className={style.Latex}>
          <Link style={{display: isLatex ? 'none' : 'block',}} to="/service/latex">Редактор формул LaTeX</Link>
        </section>
    </section>
  );
}
