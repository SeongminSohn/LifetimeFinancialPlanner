import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import profileImage from '/public/back.jpg';
import { useNavigate } from 'react-router-dom';

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
  const navigate = useNavigate();

  const popupMenu = () => {
    setSide(prevState => !prevState);
  };

  function sideElements(){
    return openSide && (
        <aside className="sidebar">
          <button>current Balance</button>
          <button>Budget Management</button>
          <button>Run simulation</button>
          <button>view simulation result</button>
          <button>setting</button>
        </aside>
    )
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
    navigate('/Profset');
  }

  return (<div>
    <nav className="navBarTop">
      <img src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
      <p className= "logoLetter">Life Time Financial Planner</p>
      <button className="commonButton">About us</button>
    </nav>
    <nav className= "navBarSub">
      <button className="commonButton" onClick={popupMenu}>Menu</button>
      {sideElements()}
      <button className="noShape" onClick={toProfile}>
        <img  className="profile" src={defineProfile()} onError={handleImage} alt="profile"></img>
      </button>
    </nav>
  </div>);
}
export default homePage;
