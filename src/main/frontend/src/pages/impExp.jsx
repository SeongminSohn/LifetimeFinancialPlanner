import React, { useState } from 'react';
import './common.css';
import {useEffect} from "react";
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import Axios from "axios"

function importExportPage(){
    //Check logged in or not.
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    const [error, setError] = useState('');
    const [openSide, setSide] = useState(false);
    const navPage = useNavigate();
    const [loggedIn, setLoggedIn] = useState(false);
    const [fileName, setFileName] = useState('');
    const [uploading, setUploading] = useState(false);
    const [file, setFile] = useState(null);
    const [formData, setFormData] = useState({});

    const handleImport = async () => {
        const scenarioId = localStorage.getItem("scenario")
        axios.get(`http://localhost:10000/api/scenarios/${scenarioId}/export`)
            .then(response => {
                const blob = new Blob([response.data], { type: "application/x-yaml" });
                const url  = URL.createObjectURL(blob);
                const link = document.createElement("a");
                link.href = url;
                link.download = "scenario.yaml";
                document.body.appendChild(link);
                link.click();
                link.remove();
                URL.revokeObjectURL(url);
            })
            .catch(() => {});
    };

    const handleSubmit = async () => {
        const picked = document.getElementById('file-upload')?.files?.[0];
        if (!picked) { setError('Choose File'); return; }

        const uploadData = new FormData();
        uploadData.append('file', picked);
        uploadData.append('userId', localStorage.getItem('scenario'));

        try {
            setUploading(true);
            await axios.post(
                'http://localhost:10000/api/scenarios/import',
                uploadData,
                { withCredentials: true }
            );
            console.log('upload OK');
        } catch (e) {
            console.error(e);
            setError('fail to upload');
        } finally {
            setUploading(false);
        }
    };

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;
        const ext = file.name.split('.').pop().toLowerCase();
        if (ext !== 'yaml' && ext !== 'yml') {
            setError("Only YAML file can be uploaded");
            setFileName('');
            return;
        }
        setError('');
        setFile(file);
        setFileName(file.name);
    };

    const popupMenu = () => {
        setSide(prevState => !prevState);
    };

    function sideElements() {
        return (
            openSide && (
                <aside className="sidebar">
                    <button onClick={() => navPage('/Investment')}>View Invest type Status</button>
                    <button onClick={() => navPage('/IncomeSetting')}>View Income Status</button>
                    <button onClick={() => navPage('/ExpenseSetting')}>View Expense Status</button>
                    <button onClick={() => navPage('/ExpenseW')}>Expense Withdrawal Edit</button>
                    <button onClick={() => navPage('/SimulationManagement')}>Invest Event Edit</button>
                    <button onClick={() => navPage('/simulationPage')}>Scenario Simulation</button>
                    <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
                </aside>
            )
        );
    }


    function importSetting(){
        return (<div className="loginBox">
            <p><strong>Upload Your YAML file</strong></p>
            <label htmlFor="file-upload" className="upload-btn">
                Choose File
            </label>
            <input type="file"
                   id="file-upload"
                   accept=".yaml,.yml"
                   placeholder="Please specify the number of simulation runs."
                   onChange={handleFileChange}
                   style={{ display: "none"}}
                   required/>

            {uploading && <p>Uploading...</p>}
            {fileName && !uploading && <p>File: {fileName}</p>}
            {error && <p className="error-text">{error}</p>}


            <button onClick = {handleSubmit}>Submit</button>
            <button onClick = {handleImport}>Get the YAML file</button>
            {/*<a href={`/api/yaml/download/${storedFileName}`} download>*/}
            {/*    {fileName} download*/}
            {/*</a>*/}
        </div>)
    }

    return (<div className="total">
        <nav className="navBarTop">
            <img src ="/public/caffeineOverloadLogo.png" className = "logoSize" onClick={() => navPage('/Homepage')}></img>
            <p className= "logoLetter">Life Time Financial Planner</p>
            <button onClick={() => navPage('/UserGuide')}>User Guide</button>
        </nav>
        <nav className= "navBarSub">
            <button className="commonButton" onClick={popupMenu}>Menu</button>
            {sideElements()}
            {loggedIn === true && (<button className="commonButton" onClick={() => navPage('/')}>
                Scenario Setting
            </button>)}
        </nav>
        {importSetting()}
    </div>);
}
export default importExportPage;