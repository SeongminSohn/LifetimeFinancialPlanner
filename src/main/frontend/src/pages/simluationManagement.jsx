import React, {useEffect, useState} from 'react';
import './common.css';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"
import axios from "axios";

function simulationManagement(){

    function simulationManage(){

    }

    return (<div className="total">
        <nav className="navBarTop">
            <img src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
            <p className="logoLetter">Life Time Financial Planner</p>
            <div></div>
        </nav>
        <nav className="navBarSub">
            {/*{loggedIn === true && <button className="commonButton" onClick={popupMenu}>Menu</button>}*/}
            {/*{sideElements()}*/}
            {/*{loggedIn === true && (<button className="commonButton" onClick={toProfile}>*/}
            {/* Scenario Setting*/}
            {/*</button>)}*/}
        </nav>
        {simulationManage()}
    </div>);
}
export default simulationManagement;