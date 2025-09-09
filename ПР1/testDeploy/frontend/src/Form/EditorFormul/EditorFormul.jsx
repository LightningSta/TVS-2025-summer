import React, { useState, useEffect } from 'react';
import style from './LaTeX.module.css';
import ButtonConfig from './ButtonConfig.json';
import arrow from '/up-arrow.svg'
import js from "@eslint/js";

export default function LaTeX() {
    const [latex, setLatex] = useState('');
    const [savedFormula, setSavedFormula] = useState(null);
    const [buttons, setButtons] = useState([]);
    const [activeCategory, setActiveCategory] = useState(null);
    const [hiddenName, setHiddenNmae] =useState(false)
    const [error, setError]= useState('')
    const [name,setName] = useState('')
    var once= false;
    useEffect(() => {
        if(!sessionStorage.getItem('login')&&!once){
            alert('Если вы войдёте в аккаунт то у вас будет возможность сохранять формулы и их экспортировать в docx и pdf, а также и анализировать их.');
            once=true
        }
        setButtons(ButtonConfig);

        const script = document.createElement('script');
        script.type = 'text/javascript';
        script.src = 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.7/MathJax.js?config=TeX-MML-AM_CHTML';
        script.async = true;
        document.head.appendChild(script);

        return () => {
            document.head.removeChild(script);
        };
    }, []);

    useEffect(() => {
        if (window.MathJax) {
            window.MathJax.Hub.Queue(["Typeset", window.MathJax.Hub, "formulaPreview"]);
        }
    }, [latex]);

    const handleInputChange = (e) => {
        setLatex(e.target.value);
    };

    const insertText = (text) => {
        setLatex((prevLatex) => prevLatex + text);
    };

    const saveFormula = ()=>{
        setSavedFormula(latex);
        if(hiddenName){
            if(sessionStorage.getItem('login')){
                fetch('/api/db/formulas', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer '+sessionStorage.getItem('token'),
                        'Content-type': 'Application/json'
                    },
                    body: JSON.stringify({
                        login: sessionStorage.getItem('login'),
                        formula: {
                            name: name,
                            latex: latex
                        }
                    })
                }).then(reps => {
                    if(reps.ok){
                        setError('200')
                    }else{
                        setError('!200')
                    }
                    setTimeout(() => {
                        setError('');
                    }, 1000);
                })
            }else{
                fetch('/api/formulas/create',{
                    method: 'POST',
                    body: JSON.stringify({
                        latex: latex,
                        mode: 'docx'
                    })
                }).then(resp=>{
                    const filename = name+'.docx';
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
        }
    }
    const toggleCategory = (category) => {
        if (activeCategory === category) {
            setActiveCategory(null);
        } else {
            setActiveCategory(category);
        }
    };

    const importDocx = async (file) => {
        const formData = new FormData();
        formData.append('file', file);
        const response = await fetch('/api/formulas/import', {
            method: 'POST',
            body: formData
        })
        const json = await response.json()
        console.log(json)
        setLatex(json.latex)
    }


    const renderButtons = () => {
        return buttons.map((category, index) => (
            <div key={index} className={style.buttonCategory}>
                <div
                    className={`${style.categoryTitle} ${activeCategory === category.title ? style.activeCategory : ''}`}
                    onClick={() => toggleCategory(category.title)}
                >
                    {category.title}
                </div>

                {activeCategory === category.title && (
                    <div className={style.buttonContainer}>
                        {category.buttons.map((button, btnIndex) => (
                            <button
                                key={btnIndex}
                                className={style.button}
                                onClick={() => insertText(button.code)}
                            >
                                <div className={style.tooltip}>{button.label}
                                    <span className={style.tooltiptext}>{button.tooltip}</span>
                                </div>

                            </button>
                        ))}
                    </div>
                )}
            </div>
        ));
    };

    return (
        <>
            <section className={style.LaTeX}>
                <div className={style.LaTeXAll}>
                    <input type="file" id="fileInput" onChange={event => {
                        importDocx(event.target.files[0])
                    }} className={style.fileInput}  accept=".xlsx,.xls,.doc, .docx,.ppt, .pptx,.txt,.pdf"/>
                    <h1>LaTeX Formula Editor
                        <img onClick={event=>{
                            const fileInput = document.getElementById('fileInput');
                            fileInput.click();
                        }} className={style.LogoImg} src={arrow}/>
                    </h1>
                    <p>Введите LaTeX формулу в текстовом поле ниже или используйте кнопки для вставки математических
                        выражений:</p>

                    <div className={style.Forms}>
                        <label className={style.Label} htmlFor="formulaEditor">Редактор формул LaTeX</label>
                        <textarea
                            className={style.TextArea}
                            id="formulaEditor"
                            value={latex}
                            onChange={handleInputChange}
                            placeholder="Введите ваш LaTeX код здесь..."
                        ></textarea>

                        <label className={style.Label} htmlFor="formulaPreview">Визуализация формулы LaTeX</label>
                        <div id="formulaPreview" className={style.TextArea}>
                            <span dangerouslySetInnerHTML={{__html: `\\(${latex}\\)`}}/>
                        </div>
                    </div>
                    <div className={style.div_test}>
                        <button className={style.Save} onClick={
                            event => {
                                saveFormula();
                                setHiddenNmae(!hiddenName)
                            }
                        }>Сохранить формулу
                        </button>
                        {error == '200' &&
                            <p className={style.SuccessSave}>Формула была успешно сохранена</p>
                        }
                        {error == '!200' &&
                            <p className={style.failedSave}>При сохранении произошла ошибка</p>
                        }
                        {hiddenName && (
                            <input
                                className={style.Input}
                                id="name"
                                placeholder="Введите имя вашей формулы"
                                value={name}
                                onChange={(e) => {
                                    setName(e.target.value)
                                }}
                            />
                        )
                        }
                    </div>

                    <div className={style.Button}>{renderButtons()}</div>
                </div>
            </section>
        </>
    );
}
