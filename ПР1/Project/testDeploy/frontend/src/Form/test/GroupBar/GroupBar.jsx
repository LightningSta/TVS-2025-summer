import style from './GroupBar.module.css';
import Line from './Line/Line'
import {useState} from "react";

export default function GroupBar({ config, groupName, sharedState, setSharedState, setActiveTab }) {
    const [exit,setExit]= useState(false);
    const handleClick = (index) => {
        const clickedItem = config[index];
        setActiveTab(clickedItem.name);
        setSharedState((prev) => {
            if (groupName === 'GroupBar1') {
                return { GroupBar1: index === prev[groupName] ? null : index, GroupBar2: null, GroupBar3: null };
            }
            if (groupName === 'GroupBar2') {
                return { ...prev, GroupBar1: null, GroupBar2: index === prev[groupName] ? null : index, GroupBar3: null };
            }
            if (groupName === 'GroupBar3') {
                return { ...prev, GroupBar2: null, GroupBar3: index === prev[groupName] ? null : index, GroupBar1: null };
            }

            return prev;
        });
        if(clickedItem.name=='Выйти из профиля'){
            if(exit){
                sessionStorage.clear();
                window.location.href='/'
            }else{
                alert('Если вы уверены, что хотите выйти, то нажмите ещё раз выйти.')
            }
            setExit(!exit)
        }
    };

    return (
        <div className={style.GroupBar}>
            {config.map((item, index) => (
                <div
                    key={index}
                    className={`${style.GroupItem} ${
                        sharedState[groupName] === index ? style.active : ''
                    }`}
                    onClick={() => handleClick(index)}
                >
                    {item.img && <img src={item.img} alt={item.name} />}
                    <p className={`${sharedState[groupName] === index ? style.activeText : ''}`}>
                        {item.name}
                    </p>
                </div>
            ))}
            <Line />
        </div>
    );
}

