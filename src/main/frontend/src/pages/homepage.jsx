import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";

function FinancialPlanner() {
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
    fetchData();
  }, []);
}
export default FinancialPlanner;
