import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';


function loginPage(){


    // const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        id: '',
        password: ''
    });

    // const popupMenu = () => {
    //     setSide(prevState => !prevState);
    // };
    //
    // function sideElements(){
    //     return openSide && (
    //         <aside className="sidebar">
    //             <button onClick={toIncome}>Income Edit</button>
    //             <button onClick={toExpense}>Expense Edit</button>
    //             <button onClick={toInvest}>Invest Edit</button>
    //             <button onClick={toSim}>Scenario Simulation</button>
    //             <button>Reports & Logs</button>
    //             <button>Import & Export Date</button>
    //         </aside>
    //     )
    // }

    function toHome(){
        navPage('/HomePage')
    }

    // function toIncome(){
    //     navPage('/IncomePage')
    // }
    //
    // function toExpense(){
    //     navPage('/ExpenseEdit');
    // }
    //
    // function toInvest(){
    //     navPage('/InvestEdit')
    // }
    //
    // function toSim(){
    //     navPage('/simulationPage')
    // }


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
            <div className="logoLetter" style={{color: 'black',fontSize: '50px'}} >Sign in</div>
            <div className="login"><label htmlFor="id"></label>
                <input type="text" id="id" name="id" value={formData.id} onChange={handleChange} placeholder="ID"/></div>
            <div className="login"><label htmlFor="password"></label>
                <input type="password" id="password" name="password" value={formData.password} onChange={handleChange} placeholder="Password"/></div>
            <div><button className="submitButton" type="submit" onClick={toSignin}>Sign in</button>
                <button className="submitButton" type="button" onClick={toSignUp}>Sign up</button></div>
        </form>);
    }

    function toSignUp(){
        navPage("/Signup")
    }

    async function toSignin(){
        try {
            const response = await axios.post("http://localhost:10000/api/users/login", {
                email: formData.id,
                password: formData.password
            });
            localStorage.setItem("token", response.data.id);
            navPage("/Homepage");
        } catch (error) {
            console.error("log in Error:", error);
            alert("Fail to log in. Please check your email or password");
        }
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <div></div>
        </nav>
        <nav className= "navBarSub">
            {/*<button className="commonButton" onClick={popupMenu}>Menu</button>*/}
            {/*{sideElements()}*/}
        </nav>
        {signinBox()}
    </div>);
}
export default loginPage;