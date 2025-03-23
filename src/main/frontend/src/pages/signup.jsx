import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function signupPage(){

    const [openSide, setSide] = useState(false);
    const [pro, setPro] = useState([{name: '', profile: {profileImage}}]);
    const navPage = useNavigate();
    const [status,setStatus] = useState()
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password1: '',
        password2: ''
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

    function toHome(){
        navPage('/Homepage')
    }

    function toDash(){
        navPage('/TaxM');
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

    };

    // function stateSelection() {
    //     return (<div className="login">
    //             <label htmlFor="ustate"></label>
    //             {/*<input*/}
    //             {/*    list="ustate"*/}
    //             {/*    placeholder="Select your state"*/}
    //             {/*    name="ustate"*/}
    //             {/*    value={formData.ustate}*/}
    //             {/*    onChange={handleChange}*/}
    //             {/*/>*/}
    //             <select name="ustate" id="ustate" value={formData.ustate} onChange={handleChange}>
    //                 <option value="AL">AL</option>
    //                 <option value="AK">AK</option>
    //                 <option value="AZ">AZ</option>
    //                 <option value="AR">AR</option>
    //                 <option value="CA">CA</option>
    //                 <option value="CO">CO</option>
    //                 <option value="CT">CT</option>
    //                 <option value="DE">DE</option>
    //                 <option value="FL">FL</option>
    //                 <option value="GA">GA</option>
    //                 <option value="HI">HI</option>
    //                 <option value="ID">ID</option>
    //                 <option value="IL">IL</option>
    //                 <option value="IN">IN</option>
    //                 <option value="IA">IA</option>
    //                 <option value="KS">KS</option>
    //                 <option value="KY">KY</option>
    //                 <option value="LA">LA</option>
    //                 <option value="ME">ME</option>
    //                 <option value="MD">MD</option>
    //                 <option value="MA">MA</option>
    //                 <option value="MI">MI</option>
    //                 <option value="MN">MN</option>
    //                 <option value="MS">MS</option>
    //                 <option value="MO">MO</option>
    //                 <option value="MT">MT</option>
    //                 <option value="NE">NE</option>
    //                 <option value="NV">NV</option>
    //                 <option value="NH">NH</option>
    //                 <option value="NJ">NJ</option>
    //                 <option value="NM">NM</option>
    //                 <option value="NY">NY</option>
    //                 <option value="NC">NC</option>
    //                 <option value="ND">ND</option>
    //                 <option value="OH">OH</option>
    //                 <option value="OK">OK</option>
    //                 <option value="OR">OR</option>
    //                 <option value="PA">PA</option>
    //                 <option value="RI">RI</option>
    //                 <option value="SC">SC</option>
    //                 <option value="SD">SD</option>
    //                 <option value="TN">TN</option>
    //                 <option value="TX">TX</option>
    //                 <option value="UT">UT</option>
    //                 <option value="VT">VT</option>
    //                 <option value="VA">VA</option>
    //                 <option value="WA">WA</option>
    //                 <option value="WV">WV</option>
    //                 <option value="WI">WI</option>
    //                 <option value="WY">WY</option>
    //
    //             </select>
    //         </div>
    //     );
    // }

    function signinBox(){
        return (<form onSubmit={handleSubmit} className="profileSetting">

            <div className="logoLetter" style={{fontSize: '50px', marginTop: "30px"}} >Create an account</div>
            <div className="login"><label htmlFor="name"></label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Enter your Name"
                /></div>
            <div className="login"><label htmlFor="email"></label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Enter your Email"
                /></div>
                <div className="login"><label htmlFor="password"></label>
                <input
                type="password"
                id="password1"
                name="password1"
                value={formData.password1}
            onChange={handleChange}
            placeholder="Enter your password"
        /></div>
        <div className="login"><label htmlFor="password"></label>
            <input
                type="password"
                id="password2"
                name="password2"
                value={formData.password2}
                onChange={handleChange}
                placeholder="password again"
            /></div>
            <div>
                <button className="submitButton" type="submit" onClick={createAccount}>Create An Account</button></div>
        </form>);
    }

    async function createAccount(){
        //Check the DB first and if ID already exists, then alert else
        //If account already exists, and check user's budget information. If user has budget info, then navigate to Homepage else
        if (formData.password1 !== formData.password2) {
            alert("Password has to be same");
            return;
        }
        try {
            const response = await axios.post("http://localhost:10000/api/users/register", {
                email: formData.email,
                password: formData.password1,
                name: formData.name
            });
            toHome()
        } catch (error) {
            console.error("error:", error);
            alert("Try again");
        }
        //else
        //navigate to put the user info page
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img onClick={toHome} src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
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
export default signupPage;