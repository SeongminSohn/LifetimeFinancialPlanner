import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function loginPage(){
    useEffect(() => {
        const fetchData = async () => {
            try {
                const planResp = await axios.get("http://localhost:10000/test");
                console.log(planResp.data);
            } catch (err) {
                console.log("inital error");
                console.log(err);
            }
        };

        fetchData();}, []);

    const [openSide, setSide] = useState(false);
    const [pro, setPro] = useState([{name: '', profile: {profileImage}}]);
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        id: '',
        password: ''
    });


    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    function sideElements(){
        return openSide && (
            <aside className="sidebar">
                <button onClick={toDash}>Tax Management</button>
                <button onClick={toFin}>Financial Planning</button>
                <button onClick={toEve}>Event Management</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Date</button>
            </aside>
        )
    }

    function toDash(){
        navPage('/Homepage');
    }

    function toFin(){
        navPage('/FinP');
    }

    function toTax(){
        navPage('/Taxm')
    }

    function toEve(){
        navPage('/EveM')
    }

    function toSim(){
        navPage('/Imex')
    }



    function defineProfile(){
        if(pro[0].profile === null || pro[0].profile === undefined){
            return profileImage;
        }else{
            return pro[0].profile;
        }
    }

    const handleImage = (e) => {
        e.target.onError = null;
        e.target.src = profileImage;
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('ID:', formData.id);
        console.log('Password:', formData.password);
    };

    function signinBox(){
        return (<form onSubmit={handleSubmit} className="loginBox">
            <div className="logoLetter" style={{fontSize: '50px'}} >Sign in</div>
            <div className="login"><label htmlFor="id">ID: </label>
                <input
                    type="text"
                    id="id"
                    name="id"
                    value={formData.id}
                    onChange={handleChange}
                /></div>
            <div className="login"><label htmlFor="password">Password: </label>
                <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                /></div>
            <button className="commonButton" type="submit" >Sign in</button>
        </form>);
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <button className="commonButton">About us</button>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
            <button className="noShape">
                <img  className="profile" src={defineProfile()} onError={handleImage} alt="profile"></img>
            </button>
        </nav>
        {signinBox()}
    </div>);
}
export default loginPage;