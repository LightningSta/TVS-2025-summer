import style from './Box.module.css';
import React, {useEffect, useState} from "react";
import ButtonConfig from "../../EditorFormul/ButtonConfig.json";

export default function Box({ config,json }) {
    const [sim,setSim]= useState('Процент совпадения')
    useEffect(() => {
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
    }, [json.part]);

    return (
        <div className={style.PodCard}>
            {config.map((item, index) => (
                <div key={index} className={style.Box}>
                    <div className={style.Grid} style={{background: item.background}}>
                        {item.img && <img src={item.img} alt={item.header} className={style.Image} />}
                    </div>
                    <div className={style.TextAll}>
                        <div className={style.Header}>{item.header}</div>
                        {sim == item.header &&
                            <div className={style.Text}>{json.similarity}</div>
                        }
                        {sim != item.header &&

                            <div
                                id="formulaPreview"
                                className={style.Text}
                            >
                                <span dangerouslySetInnerHTML={{__html: `\\(${json.part}\\)`}}/>
                            </div>

                        }
                    </div>
                </div>
            ))}
        </div>
    );
}
