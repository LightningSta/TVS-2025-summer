import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import style from './Main.module.css';
import config from '../Config.json';
import One from './One/One';
import Two from './Two/Two';
import Three from './Three/Three';
import Four from './Four/Four';
import Footer from './Footer/Footer';

export default function Main(){
    const info = config.One_info[0];

    return(
        <>
            <One className={style.AppOne} developers={info.developers} projects={info.projects} />
            <Two className={style.AppTwo} />
            <Three />
            <Four />
            <Footer />
        </>
    )
}