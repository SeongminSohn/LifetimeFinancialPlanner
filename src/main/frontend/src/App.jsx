import { BrowserRouter, Routes, Route } from "react-router-dom";
import Sample from "./pages/homepage.jsx";
import "./App.css";


function App() {
    return (
        <>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Sample />} />
                    <Route path="/sample" element={<Sample />} />
                </Routes>
            </BrowserRouter>
        </>
    );
}

export default App;
