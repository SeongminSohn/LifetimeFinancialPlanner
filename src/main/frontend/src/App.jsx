import { BrowserRouter, Routes, Route } from "react-router-dom";
import Homepage from "./pages/homepage.jsx";
import Profset from "./pages/profileSetting.jsx"
import "./App.css";


function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Homepage />} />
                    <Route path="/Homepage" element={<Homepage />} />
                    <Route path="/Profset" element={<Profset />} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
