import style from './Register.module.css'
import { useState } from 'react'

export default function Login(){
    const [nickname,setNickname] = useState('');
    const [login, setLogin] = useState('');
    const [password, setPassword] = useState('');
    const [repeatPassword, setRepeatPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!login || !password || !nickname || !repeatPassword) {
            setError('Пожалуйста, заполните все поля.');
            return;
        }

        if (password !== repeatPassword) {
            setError('Пароли не совпадают!');
            return;
        }

        const data = {
            login: login,
            password: password,
            nickname: nickname
        }
        const res = await fetch('/api/auth/registration', {
            headers: {
                'Content-type': 'Application/json'
            },
            method: 'POST',
            body: JSON.stringify(data)
        })
        if(res.ok){
            window.location.href='/login'
        }

    }

    const handleCheckboxChange = () => {
        setShowPassword(prevState => !prevState);
    };

    return(
        <>
            <section className={style.Login}>
                <div className={style.FormLogin}>
                    <h2 className={style.LoginH2}>Регистрация</h2>
                    <a className={style.RegisterA} href="/login">Авторизация</a>
                    <form className={style.LoginForm} onSubmit={handleSubmit} action="/registration" method="post">
                        <div className={style.LabelLoginForm}>
                            <div className={style.FormGroup}>
                                <label className={style.Label} htmlFor="nickname">Никнейм</label>
                                <input className={style.Input}
                                    type="text"
                                    id="nickname"
                                    value={nickname}
                                    onChange={(e) => setNickname(e.target.value)}
                                />
                            </div>
                            <div className={style.FormGroup}>
                                <label className={style.Label} htmlFor="login">Логин</label>
                                <input className={style.Input}
                                    type="text"
                                    id="login"
                                    value={login}
                                    onChange={(e) => setLogin(e.target.value)}
                                />
                            </div>
                            <div className={style.FormGroup}>
                                <label className={style.Label} htmlFor="password">Придумайте пароль</label>
                                <input className={style.Input}
                                    type = {showPassword ? 'text' : 'password'}
                                    id="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                />
                            </div>
                            <div className={style.FormGroup}>
                                <label className={style.Label} htmlFor="repeatPassword">Повторите пароль</label>
                                <input className={style.Input}
                                    type = {showPassword ? 'text' : 'password'}
                                    id="repeatPassword"
                                    value={repeatPassword}
                                    onChange={(e) => setRepeatPassword(e.target.value)}
                                />
                            </div>
                        </div>
                        <label className={style.OpenPassword}><input type="checkbox" checked={showPassword} onChange={handleCheckboxChange}/><p>Показать пароль</p></label>
                        <div className={style.FooterLogin}>
                            <button type="submit" className={style.LoginBtn}>Зарегистрироваться</button>
                        </div>
                    </form>
                </div>
            </section>
        </>
    )
}