import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function profileSetting(){
    // useEffect(() => {
    //     const fetchData = async () => {
    //         try {
    //             const planResp = await axios.get("http://localhost:10000/test");
    //             console.log(planResp.data);
    //         } catch (err) {
    //             console.log("inital error");
    //             console.log(err);
    //         }
    //     };
    //
    //     fetchData();}, []);

    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        ugender: 'Male',
        uemail: '',
        uphonenum: '',
        uaddress1: '',
        uaddress2: '',
        ustate: 'AL'
    });

    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    function sideElements(){
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toInvest}>Invest Edit</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Date</button>
            </aside>
        )
    }

    function toIncome(){
        navPage('/IncomePage')
    }

    function toExpense(){
        navPage('/ExpenseEdit');
    }

    function toInvest(){
        navPage('/InvestEdit')
    }

    function toSim(){
        navPage('/Imex')
    }

    const [selectedImage, setSelectedImage] = useState(null);

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setSelectedImage(e.target.files[0]);
        }
    };

    const defineProfile = () => {
        if (selectedImage) {
            console.log(URL.createObjectURL(selectedImage))
            return URL.createObjectURL(selectedImage);
        }
        return profileImage;
    }

    function removeImage(){
        setSelectedImage(null);
    }

    const handleImage = (e) => {
        e.target.onError = null;
        e.target.src = profileImage;
    }

    function toHome(){
        navPage('/Homepage')
    }

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        console.log('Name:', formData.uname);
        console.log('Gender:', formData.ugender);
        console.log('Email:', formData.uemail);
        console.log('phone Number:', formData.uphonenum);
        console.log('address1:', formData.uaddress1);
        console.log('address2:', formData.uaddress2);
        console.log('state:', formData.ustate);
    };

    function stateSelection() {
        return (<div className="login">
                <label htmlFor="ustate"></label>
                <select name="ustate" id="ustate" value={formData.ustate} onChange={handleChange}>
                    <option value="AL">AL</option>
                    <option value="AK">AK</option>
                    <option value="AZ">AZ</option>
                    <option value="AR">AR</option>
                    <option value="CA">CA</option>
                    <option value="CO">CO</option>
                    <option value="CT">CT</option>
                    <option value="DE">DE</option>
                    <option value="FL">FL</option>
                    <option value="GA">GA</option>
                    <option value="HI">HI</option>
                    <option value="ID">ID</option>
                    <option value="IL">IL</option>
                    <option value="IN">IN</option>
                    <option value="IA">IA</option>
                    <option value="KS">KS</option>
                    <option value="KY">KY</option>
                    <option value="LA">LA</option>
                    <option value="ME">ME</option>
                    <option value="MD">MD</option>
                    <option value="MA">MA</option>
                    <option value="MI">MI</option>
                    <option value="MN">MN</option>
                    <option value="MS">MS</option>
                    <option value="MO">MO</option>
                    <option value="MT">MT</option>
                    <option value="NE">NE</option>
                    <option value="NV">NV</option>
                    <option value="NH">NH</option>
                    <option value="NJ">NJ</option>
                    <option value="NM">NM</option>
                    <option value="NY">NY</option>
                    <option value="NC">NC</option>
                    <option value="ND">ND</option>
                    <option value="OH">OH</option>
                    <option value="OK">OK</option>
                    <option value="OR">OR</option>
                    <option value="PA">PA</option>
                    <option value="RI">RI</option>
                    <option value="SC">SC</option>
                    <option value="SD">SD</option>
                    <option value="TN">TN</option>
                    <option value="TX">TX</option>
                    <option value="UT">UT</option>
                    <option value="VT">VT</option>
                    <option value="VA">VA</option>
                    <option value="WA">WA</option>
                    <option value="WV">WV</option>
                    <option value="WI">WI</option>
                    <option value="WY">WY</option>

                </select>
            </div>
        );
    }

    function profileSetup(){
        return (<form onSubmit={handleSubmit} className="profileSetting">
            <div className="login"><label htmlFor="name"></label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Edit your Name"
                /></div>
            <div className="login"><label htmlFor="maritalStatus"></label>
                <select name="ustate" id="ustate" value={formData.ustate} onChange={handleChange}>
                    <option value = "Y">I am married</option>
                    <option value = "N">No, I am not</option>
                </select></div>
            <div className="login"><label htmlFor="uphonenum"></label>
                <input type="tel" name="uphonenum"  id="uphonenum"  placeholder="Enter your phone number" value={formData.uphonenum} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="uaddress1"></label>
                <input placeholder="Address" type="text" id="uaddress1" name="uaddress1" value={formData.uaddress1} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="uaddress2"></label>
                <input placeholder="Address (Optional)" type="text" id="uaddress2" name="uaddress2" value={formData.uaddress2} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="ustate"></label>
                {stateSelection()}</div>
            <button className="submitButton" type="submit" style={{marginBottom:"20px"}}>Save Changes</button>
        </form>);
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
        </nav>
        {profileSetup()}
    </div>);
}
export default profileSetting;