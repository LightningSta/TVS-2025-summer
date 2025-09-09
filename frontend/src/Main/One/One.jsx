import style from './One.module.css'
import ImgPerson from '/person.png'
import BlockDev from '/people.svg'
import BlockProject from '/book.svg'

export default function One({className, developers, projects}){
    return(
        <>
            <section className={`${className} ${style.One}`}>
                <p className={style.OneHeadP}>Забудьте про проблемы с формулами <br /><span>Легко и Уверенно!</span></p>
                <p className={style.OneCenterP}>
                    MathNotes — универсальная платформа для работы с математическими формулами,
                    <br/>обеспечивающая полный спектр решений для научных и инженерных задач:
                    <br/> от
                    удобного редактора формул с поддержкой LaTeX до мощных инструментов
                    <br/>анализа и проверки оригинальности.
                </p>
                <button onClick={event => {
                    window.location.href='/editor'
                }} className={style.OneButton}>Начать</button>
                <img className={style.ImgPerson} src={ImgPerson} />
                <section className={style.BlockDeveloper}>
                    <div className={style.BlcDev}><img className={style.ImgBlockDev} src={BlockDev} /></div>
                    <p className={style.BlockDevP}><span className={style.BlockDevSpan}>Лёгкость использования</span></p>
                </section>
                <section className={`${style.BlockDeveloper} ${style.BlockProject}`}>
                    <div className={style.BlcProject}><img className={style.ImgBlockDev} src={BlockProject} /></div>
                    <p className={style.BlockProjP}>Качество<br /></p>
                </section>
            </section>
        </>
    )
}