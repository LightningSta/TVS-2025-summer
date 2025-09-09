import style from './Stroke.module.css';
import React, { useState, useEffect } from 'react';
import js from "@eslint/js";

export default function Stroke({ onClick }) {
    const [elements, setElements] = useState([]); // Состояние для хранения элементов


    const export_Docx = (latex,name,mode)=>{

        console.log(latex, name, mode)
        const data = {
            latex: latex,
            mode: mode
        }
        console.log(data)
        console.log(JSON.stringify(data))
        fetch('/api/formulas/create',{
            method: 'POST',
            body: JSON.stringify(data)
        }).then(resp=>{
            const filename = name+'.'+mode;
            resp.blob().then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = decodeURIComponent(filename)
                document.body.appendChild(a);
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            });
        })
    }


    // Функция загрузки формул
    const loadFormulas = async () => {
        const response = await fetch('/api/db/formulas/people', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + sessionStorage.getItem('token'),
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                login: sessionStorage.getItem('login')
            })
        });

        const json = await response.json();
        console.log(json)
        const loadedElements = json.map((item, index) => (
            <section key={index} className={style.Stroke}>
                <div className={style.NameFormul}>{item.name}</div>
                <div className={style.ExportFile} onClick={event => {
                    export_Docx(item.latex, item.name,'docx')
                }}>{'Docx'}</div>
                <div className={style.ExportFile} onClick={event => {
                    export_Docx(item.latex, item.name,'pdf')
                }}>{'PDF'}</div>
                <div className={style.Analizator} onClick={event => {
                    onClick(item.latex);
                }}>
                    Click
                </div>
            </section>
        ));
        return loadedElements;
    };

    useEffect(() => {
        loadFormulas().then(loadedElements => {
            setElements(loadedElements);
        });
    }, []);

    return (
        <>
            <div>
                {elements.length > 0 ? elements : <p>Загрузка...</p>}
            </div>
        </>
    );
}
