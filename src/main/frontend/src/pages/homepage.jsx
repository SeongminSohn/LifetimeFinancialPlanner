import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';
import Axios from "axios"

function homePage(){
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
  const [loggedIn, setLoggedIn] = useState(false)

  const popupMenu = () => {
    setSide(prevState => !prevState);
  };

  function sideElements(){
    return openSide && (
        <aside className="sidebar">
          <button onClick={toTax}>Tax Management</button>
          <button onClick={toFin}>Financial Planning</button>
          <button onClick={toEve}>Event Management</button>
          <button onClick={toSim}>Scenario Simulation</button>
          <button>Reports & Logs</button>
          <button>Import & Export Date</button>
        </aside>
    )
  }

  // function toDash(){
  //   navPage('/Homepage');
  // }

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

  function toProfile(){
    navPage('/Profset');
  }

  function toLogin(){
    navPage('/Loginpage');
  }

  function homeManage(){
    return(<div className="loginBox">
      <></>
    </div>)
  }

  function statusCheck(){
    //if DB got the data, then
    setLoggedIn(true)
    //else
    //setLoggedIn(false)
  }

  return (<div className="total">
    <nav className="navBarTop">
      <img src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
      <p className= "logoLetter">Life Time Financial Planner</p>
      {!loggedIn && (
          <button className="commonButton" onClick={toLogin}>
            Sign-In
          </button>
      )}
    </nav>
    <nav className= "navBarSub">
      <button className="commonButton" onClick={popupMenu}>Menu</button>
      {sideElements()}
      <button className="noShape" onClick={toProfile}>
        <img  className="profile" src={defineProfile()} onError={handleImage} alt="profile"></img>
      </button>
    </nav>
    {homeManage()}
  </div>);
}
export default homePage;
