import { useState } from 'react';
import style from './Profile.module.css';
import Avatar from '/Avatar.svg';
import Button from '/Icons.svg';
import Social from './Social/Social';
import Config from './Profile.config.json';
import GroupBar from './GroupBar/GroupBar';
import Line from './GroupBar/Line/Line';
import Stroke from './Stroke/Stroke';
import Box from './Box/Box';
import Develop from '/develop.jpg'
import js from "@eslint/js";

export default function Profile() {
    if(sessionStorage.getItem('login')){
        const [sharedState, setSharedState] = useState({
            GroupBar1: 0,
            GroupBar2: null,
            GroupBar3: null,
        });

        const [activeTab, setActiveTab] = useState('История');
        const [showAnalizator, setShowAnalizator] = useState(false);

        const [formula,setFormula]= useState('')

        const [json,setJSON] = useState('')

        const handleShowAnalizator = (test) => {
            setFormula(test);
            console.log(test)
            analyz(test);
            setShowAnalizator(true);
        };

        const analyz = async (test_formula) => {
            console.log(test_formula)
            const response = await fetch('/api/formulas/analyz', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
                    'Content-type': 'application/json'
                },
                body: JSON.stringify({
                    login: sessionStorage.getItem('login'),
                    formula: test_formula
                })
            });

            const json = await response.json();
            setJSON(json)
        }

        return (
            <>
                <section className={style.Profile}>
                    <section className={style.Content}>
                        <section className={style.Header}>
                            <div className={style.Frame}>
                                <div className={style.Name}>
                                    <img className={style.Avatar} src={Avatar} />
                                    <button className={style.Button}><img src={Button} /></button>
                                    <p>{sessionStorage.getItem('login')}</p>
                                </div>
                                <div className={style.Social}>
                                    <Social />
                                </div>
                            </div>
                        </section>
                        <section className={style.Main}>
                            <div className={style.SideBar}>
                                <div className={style.GroupBar1}>
                                    <GroupBar
                                        config={Config.GroupBar1}
                                        groupName="GroupBar1"
                                        sharedState={sharedState}
                                        setSharedState={setSharedState}
                                        setActiveTab={setActiveTab}
                                    />
                                </div>
                                <div className={style.AppLine}>
                                    <Line />
                                    <Line />
                                    <Line />
                                    <Line />
                                </div>
                                <div className={`${style.GroupBar1} ${style.GroupBar2}`}>
                                    <GroupBar
                                        config={Config.GroupBar2}
                                        groupName="GroupBar2"
                                        sharedState={sharedState}
                                        setSharedState={setSharedState}
                                        setActiveTab={setActiveTab}
                                    />
                                </div>
                                <div className={style.Lines}></div>
                                <div className={style.AppLine}>
                                    <Line />
                                    <Line />
                                    <Line />
                                    <Line />
                                </div>
                                <div className={`${style.GroupBar1} ${style.GroupBar2}`}>
                                    <GroupBar
                                        config={Config.GroupBar3}
                                        groupName="GroupBar3"
                                        sharedState={sharedState}
                                        setSharedState={setSharedState}
                                        setActiveTab={setActiveTab}
                                    />
                                </div>
                                <div className={style.Lines}></div>
                            </div>
                            <div className={`${style.Divider} ${showAnalizator ? style.DividerExpanded : ''}`}></div>
                            {activeTab === 'История' ? (
                                <section className={style.MainInformation}>
                                    <div className={style.Listing}>
                                        <div className={style.TextList}>История</div>
                                        <div className={style.Table}>
                                            <div className={style.Heading}>
                                                <div className={style.NameFormul}>Имя формулы</div>
                                                <div className={style.ExportFile}>Экспортировать файл</div>
                                                <div className={style.Analizator}>Анализатор</div>
                                            </div>
                                            <Stroke onClick={handleShowAnalizator} />
                                        </div>
                                    </div>
                                    <div className={style.DividerGhor}></div>
                                    {showAnalizator && (
                                        <>
                                            <div className={style.AnalizatorBlock}>
                                                <div className={style.HeadingAnaliz}>Анализатор</div>
                                                <div className={style.Card}>
                                                    <Box json={json} config={Config.Box} />
                                                </div>
                                            </div>
                                            <div className={style.DividerGhor}></div>
                                        </>
                                    )}
                                </section>
                            ) : (
                                <section className={style.Develop}>В разработке
                                    <img className={style.DevelopImg} src={Develop} />
                                </section>
                            )}
                        </section>
                    </section>
                </section>
            </>
        );
    }else{
        window.location.href='/login'
    }
}
