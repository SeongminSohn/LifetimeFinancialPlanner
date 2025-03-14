import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";

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
  return (<div>
    <nav className="navBarTop">
      <img src ="/public/caffeineOverloadLogo.png" className = "logoSize"></img>
      <p className= "logoLetter">Life Time Financial Planner</p>
      <button className="commonButton">About us</button>
    </nav>
    <nav className= "navBarSub">
      <button className="commonButton" onClick={popupMenu}>Menu</button>
      {sideElements()}
    </nav>
  </div>);
}
export default homePage;
