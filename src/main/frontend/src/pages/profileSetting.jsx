import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';


function profileSetting(){
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
    const navPage = useNavigate();
    const [formData, setFormData] = useState({
        uprofile: '',
        uname: '',
        ugender: '',
        uemail: '',
        uphonenum: '',
        uaddress1: '',
        uaddress2: '',
        ustate: ''
    });

    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    function sideElements(){
        return openSide && (
            <aside className="sidebar">
                <button onClick={toDash}>Tax Management</button>
                <button>Financial Planning</button>
                <button>Event Management</button>
                <button>Scenario Simulation</button>
                <button>Reports & Logs</button>
                <button>Import & Export Date</button>
            </aside>
        )
    }

    function toDash(){
        navPage('/Homepage');
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
                <input
                    list="ustate"
                    placeholder="Select your state"
                    name="ustate"
                    value={formData.ustate}
                    onChange={handleChange}
                />
                <datalist id="ustate">
                    <option value="AL"/>
                    <option value="AK"/>
                    <option value="AZ"/>
                    <option value="AR"/>
                    <option value="CA"/>
                    <option value="CO"/>
                    <option value="CT"/>
                    <option value="DE"/>
                    <option value="FL"/>
                    <option value="GA"/>
                    <option value="HI"/>
                    <option value="ID"/>
                    <option value="IL"/>
                    <option value="IN"/>
                    <option value="IA"/>
                    <option value="KS"/>
                    <option value="KY"/>
                    <option value="LA"/>
                    <option value="ME"/>
                    <option value="MD"/>
                    <option value="MA"/>
                    <option value="MI"/>
                    <option value="MN"/>
                    <option value="MS"/>
                    <option value="MO"/>
                    <option value="MT"/>
                    <option value="NE"/>
                    <option value="NV"/>
                    <option value="NH"/>
                    <option value="NJ"/>
                    <option value="NM"/>
                    <option value="NY"/>
                    <option value="NC"/>
                    <option value="ND"/>
                    <option value="OH"/>
                    <option value="OK"/>
                    <option value="OR"/>
                    <option value="PA"/>
                    <option value="RI"/>
                    <option value="SC"/>
                    <option value="SD"/>
                    <option value="TN"/>
                    <option value="TX"/>
                    <option value="UT"/>
                    <option value="VT"/>
                    <option value="VA"/>
                    <option value="WA"/>
                    <option value="WV"/>
                    <option value="WI"/>
                    <option value="WY"/>
                </datalist>
            </div>
        );
    }

    function profileSetup(){
        return (<form onSubmit={handleSubmit} className="profileSetting">
            <div className="logoLetter" style={{fontSize: '50px', marginTop: "30px"}} >Profile Setting</div>
            <img className="profileImageSet" src={defineProfile()} onError={handleImage} alt="profile"></img>
            <label htmlFor="fileButton" className='IMAGEBUTTON'>Choose new image</label>
            <input type="file" id = "fileButton" style = {{display: 'none'}} onChange={handleFileChange}/>
            <button type = 'button' className='REMOVEIMAGE' onClick={removeImage}>Remove image</button>
            <div className="login"><label htmlFor="uname"></label>
                <input placeholder="User Name" type="text" id="uname" name="uname" value={formData.uname} onChange={handleChange}/></div>
            <div className="login">
                <label htmlFor="ugender"></label>
                <input
                    list="ugender"
                    placeholder="Gender"
                    name="ugender"
                    value={formData.ugender}
                    onChange={handleChange}
                />
                <datalist id="ugender">
                    <option value="Male" />
                    <option value="female" />
                    <option value="not want to say" />
                </datalist>
            </div>
            <div className="login"><label htmlFor="uemail"></label>
                <input placeholder="Email" type="text" id="uemail" name="uemail" value={formData.uemail} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="uphonenum"></label>
                <input type="tel" name="uphonenum"  id="uphonenum"  placeholder="Enter your phone number" value={formData.uphonenum} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="uaddress1"></label>
                <input placeholder="Address" type="text" id="uaddress1" name="uaddress1" value={formData.uaddress1} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="uaddress2"></label>
                <input placeholder="Address (Optional)" type="text" id="uaddress2" name="uaddress2" value={formData.uaddress2} onChange={handleChange}/></div>
            <div className="login"><label htmlFor="ustate"></label>
                {stateSelection()}</div>
            <button className="" type="submit" style={{marginBottom:"20px"}}>Save Changes</button>
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
        {profileSetup()}
    </div>);
}
export default profileSetting;