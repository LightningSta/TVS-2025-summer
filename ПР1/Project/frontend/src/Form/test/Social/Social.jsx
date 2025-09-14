import style from './Social.module.css'
import YouTube from '/YouTube.svg'

export default function Social(){
    return(
        <>
            <div className={style.Social}><img src={YouTube} /></div>
        </>
    )
}