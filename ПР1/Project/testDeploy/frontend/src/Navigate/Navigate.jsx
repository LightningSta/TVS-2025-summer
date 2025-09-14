import style from './Navigate.module.css'
import Logo from '/logo.jpg'
import LoginBg from '/LoginBg.svg'
import Chervak from '/Chervak.svg'
import { useNavigate, useLocation } from 'react-router-dom'
import BackLatex from '/BackLatex.svg'
import Profile from '/Profile.svg'

export default function Navigate(){

    const navigate = useNavigate();
    const location = useLocation();

    const handleLoginClick = () => {
        navigate('/login');
    };
    const handleMeClick = () => {
        navigate('/me');
    };

    const handleRegisterClick = () => {
        navigate('/register');
    };

    const isLoginOrRegister = location.pathname === '/login' || location.pathname === '/register' || location.pathname === '/recover';

    const isEditor = location.pathname === '/editor';

    const isProfile = location.pathname === '/me';

    const isAuth = sessionStorage.getItem('login')

    return(
        <>
            {isLoginOrRegister && (
                <>
                    <img className={style.LoginBg} src={LoginBg} />
                    <img className={style.Chervak} src={Chervak} />
                </>
            )}
            {isEditor && (
                <>
                    <img className={style.BackLatex} src={BackLatex} />
                </>
            )}
            {isProfile && (
                <>
                    <img className={style.Profile} src={Profile} />
                </>
            )}
            <section 
                className={style.Navigate} 
                style={{
                    boxShadow: isProfile||isEditor||isLoginOrRegister ? '0 0.21vw 0.21vw 0 rgba(0, 0, 0, 0.25)' : 'none',
                    position: isProfile||isEditor||isLoginOrRegister ? 'absolute' : 'none',
                    left: isProfile||isEditor||isLoginOrRegister ? '0' : 'none',
                    top: isProfile||isEditor||isLoginOrRegister ? '0' : 'none',
                }}
            >
                <a href="/">
                    <div className={style.Logo} style={{marginLeft: isEditor||isLoginOrRegister ? '5.3vw': 'none'}}>
                        <img className={style.LogoImg} src={Logo} />
                        <p className={style.LogoP}>MathNotes</p>
                    </div>
                </a>
                <div className={style.Navigation}>
                    <a href="/">Главная</a>
                    <a href="">О нас</a>
                    <a href="">Отзывы</a>
                    <a href="">Обратная связь</a>
                </div>
                {isAuth &&
                    <div className={style.NavigateButton}>
                        <button className={style.ButtonAuth} onClick={handleMeClick}>{sessionStorage.getItem('login')}</button>
                    </div>
                }
                {!isAuth &&
                    <div className={style.NavigateButton}>
                        <button className={style.ButtonAuth} onClick={handleLoginClick}>Войти</button>
                        <button className={style.ButtonReg} onClick={handleRegisterClick}>Регистрация</button>
                    </div>
                }
            </section>
        </>
    )
}
