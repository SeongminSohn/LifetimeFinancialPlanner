import React, { useState, useEffect } from 'react'
import './common.css'
import axios from 'axios'
import { useNavigate, useParams } from 'react-router-dom'

function expensePage() {
    const currentYear = new Date().getFullYear()
    const { id } = useParams()
    const navPage = useNavigate()

    const [openSide, setSide] = useState(false)
    const [loggedIn, setLoggedIn] = useState(false)
    const [formData, setFormData] = useState({
        eventSeriesId: null,
        name: '',
        startYear: { amountOrPercent: 'AMOUNT', distributionType: 'FIXED', value: null, lower: null, upper: null, mean: null, stDev: null },
        duration: { amountOrPercent: 'AMOUNT', distributionType: 'FIXED', value: null, lower: null, upper: null, mean: null, stDev: null },
        initialAmount: '',
        annualChange: { amountOrPercent: 'AMOUNT', distributionType: 'FIXED', value: null, lower: null, upper: null, mean: null, stDev: null },
        inflationAdjustment: 'Y',
        userPercentage: '',
        isDiscretionary: 'Y'
    })

    useEffect(() => {
        const token = localStorage.getItem('token')
        if (token) setLoggedIn(true)
    }, [])

    useEffect(() => {
        if (!id) return
        axios.get(`http://localhost:10000/api/expense-events/${id}`, { withCredentials: true })
            .then(res => {
                const data = res.data
                setFormData({
                    eventSeriesId: data.eventSeriesId,
                    name: data.eventSeries?.name || '',
                    startYear: { ...data.eventSeries.startYear },
                    duration: { ...data.eventSeries.duration },
                    initialAmount: data.initialAmount,
                    annualChange: { ...data.annualChange },
                    inflationAdjustment: data.inflationAdjustment,
                    userPercentage: data.userPercentage,
                    isDiscretionary: data.isDiscretionary
                })
            })
    }, [id])

    const popupMenu = () => setSide(prev => !prev)

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
                    <button onClick={() => navPage('/simulationResult')}>Scenario Simulation</button>
                    <button onClick={() => navPage('/ImportExp')}>Import & Export Data</button>
                </aside>
            )
        );
    }

    const handleChange = e => {
        const { name, value } = e.target
        if (name === 'startYear.amountOrPercent') {
            setFormData(prev => ({
                ...prev,
                startYear: { amountOrPercent: value, distributionType: prev.startYear.distributionType, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (name === 'duration.amountOrPercent') {
            setFormData(prev => ({
                ...prev,
                duration: { amountOrPercent: value, distributionType: prev.duration.distributionType, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (name === 'annualChange.amountOrPercent') {
            setFormData(prev => ({
                ...prev,
                annualChange: { amountOrPercent: value, distributionType: prev.annualChange.distributionType, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (name === 'startYear.value') {
            const raw = parseInt(value, 10)
            const clamped = isNaN(raw) ? currentYear : Math.max(currentYear, raw)
            setFormData(prev => ({ ...prev, startYear: { ...prev.startYear, value: clamped } }))
            return
        }
        if (name === 'startYear.lower') {
            const raw = parseInt(value, 10)
            const clamped = isNaN(raw) ? currentYear : Math.max(currentYear, raw)
            setFormData(prev => ({ ...prev, startYear: { ...prev.startYear, lower: clamped } }))
            return
        }
        if (name === 'startYear.upper') {
            const raw = parseInt(value, 10)
            const minUpper = formData.startYear.lower ?? currentYear
            const clamped = isNaN(raw) ? null : Math.max(raw, minUpper)
            setFormData(prev => ({ ...prev, startYear: { ...prev.startYear, upper: clamped } }))
            return
        }
        if (name === 'startYear.mean') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, startYear: { ...prev.startYear, mean: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'startYear.stDev') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, startYear: { ...prev.startYear, stDev: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'startYear.distributionType') {
            setFormData(prev => ({
                ...prev,
                startYear: { amountOrPercent: prev.startYear.amountOrPercent, distributionType: value, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (name === 'duration.value') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, duration: { ...prev.duration, value: raw } }))
            return
        }
        if (name === 'duration.lower') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, duration: { ...prev.duration, lower: raw } }))
            return
        }
        if (name === 'duration.upper') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, duration: { ...prev.duration, upper: raw } }))
            return
        }
        if (name === 'duration.mean') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, duration: { ...prev.duration, mean: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'duration.stDev') {
            const raw = parseInt(value, 10)
            setFormData(prev => ({ ...prev, duration: { ...prev.duration, stDev: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'duration.distributionType') {
            setFormData(prev => ({
                ...prev,
                duration: { amountOrPercent: prev.duration.amountOrPercent, distributionType: value, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (name === 'annualChange.value') {
            const raw = parseFloat(value)
            setFormData(prev => ({ ...prev, annualChange: { ...prev.annualChange, value: raw } }))
            return
        }
        if (name === 'annualChange.lower') {
            const raw = parseFloat(value)
            setFormData(prev => ({ ...prev, annualChange: { ...prev.annualChange, lower: raw } }))
            return
        }
        if (name === 'annualChange.upper') {
            const raw = parseFloat(value)
            setFormData(prev => ({ ...prev, annualChange: { ...prev.annualChange, upper: raw } }))
            return
        }
        if (name === 'annualChange.mean') {
            const raw = parseFloat(value)
            setFormData(prev => ({ ...prev, annualChange: { ...prev.annualChange, mean: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'annualChange.stDev') {
            const raw = parseFloat(value)
            setFormData(prev => ({ ...prev, annualChange: { ...prev.annualChange, stDev: isNaN(raw) ? null : raw } }))
            return
        }
        if (name === 'annualChange.distributionType') {
            setFormData(prev => ({
                ...prev,
                annualChange: { amountOrPercent: prev.annualChange.amountOrPercent, distributionType: value, value: null, lower: null, upper: null, mean: null, stDev: null }
            }))
            return
        }
        if (['name', 'initialAmount', 'inflationAdjustment', 'userPercentage', 'isDiscretionary'].includes(name)) {
            setFormData(prev => ({ ...prev, [name]: value }))
        }
    }

    const handleSubmit = async e => {
        e.preventDefault()
        const payload = {
            eventSeriesId: formData.eventSeriesId,
            startYear: formData.startYear,
            duration: formData.duration,
            name: formData.name,
            initialAmount: formData.initialAmount,
            annualChange: formData.annualChange,
            inflationAdjustment: formData.inflationAdjustment,
            userPercentage: formData.userPercentage,
            isDiscretionary: formData.isDiscretionary
        }
        await axios.put(
            `http://localhost:10000/api/expense-events/${id}`,
            payload,
            { withCredentials: true, headers: { 'Content-Type': 'application/json' } }
        )
        alert('Updated successfully')
        console.log(payload)
        navPage('/ExpenseSetting')
    }

    function chooseMone() {
        return (
            <div>
                {formData.startYear.distributionType === 'FIXED' && (
                    <input
                        type="number"
                        onWheelCapture={e => e.target.blur()}
                        name="startYear.value"
                        min={currentYear}
                        value={formData.startYear.value || ''}
                        onChange={handleChange}
                        placeholder="value"
                        required
                    />
                )}
                {formData.startYear.distributionType === 'UNIFORM' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="startYear.lower"
                            value={formData.startYear.lower || ''}
                            onChange={handleChange}
                            placeholder="lower"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="startYear.upper"
                            min={formData.startYear.lower || currentYear}
                            value={formData.startYear.upper || ''}
                            onChange={handleChange}
                            placeholder="upper"
                            required
                        />
                    </>
                )}
                {formData.startYear.distributionType === 'NORMAL' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="startYear.mean"
                            value={formData.startYear.mean || ''}
                            onChange={handleChange}
                            placeholder="mean"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="startYear.stDev"
                            value={formData.startYear.stDev || ''}
                            onChange={handleChange}
                            placeholder="stDev"
                            required
                        />
                    </>
                )}
            </div>
        )
    }

    function chooseKone() {
        return (
            <div>
                {formData.duration.distributionType === 'FIXED' && (
                    <input
                        type="number"
                        onWheelCapture={e => e.target.blur()}
                        name="duration.value"
                        value={formData.duration.value || ''}
                        onChange={handleChange}
                        placeholder="value"
                        required
                    />
                )}
                {formData.duration.distributionType === 'UNIFORM' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="duration.lower"
                            value={formData.duration.lower || ''}
                            onChange={handleChange}
                            placeholder="lower"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="duration.upper"
                            value={formData.duration.upper || ''}
                            onChange={handleChange}
                            placeholder="upper"
                            required
                        />
                    </>
                )}
                {formData.duration.distributionType === 'NORMAL' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="duration.mean"
                            value={formData.duration.mean || ''}
                            onChange={handleChange}
                            placeholder="mean"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="duration.stDev"
                            value={formData.duration.stDev || ''}
                            onChange={handleChange}
                            placeholder="stDev"
                            required
                        />
                    </>
                )}
            </div>
        )
    }

    function chooseLone() {
        return (
            <div>
                {formData.annualChange.distributionType === 'FIXED' && (
                    <input
                        type="number"
                        onWheelCapture={e => e.target.blur()}
                        name="annualChange.value"
                        value={formData.annualChange.value || ''}
                        onChange={handleChange}
                        placeholder="value"
                        required
                    />
                )}
                {formData.annualChange.distributionType === 'UNIFORM' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="annualChange.lower"
                            value={formData.annualChange.lower || ''}
                            onChange={handleChange}
                            placeholder="lower"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="annualChange.upper"
                            value={formData.annualChange.upper || ''}
                            onChange={handleChange}
                            placeholder="upper"
                            required
                        />
                    </>
                )}
                {formData.annualChange.distributionType === 'NORMAL' && (
                    <>
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="annualChange.mean"
                            value={formData.annualChange.mean || ''}
                            onChange={handleChange}
                            placeholder="mean"
                            required
                        />
                        <input
                            type="number"
                            onWheelCapture={e => e.target.blur()}
                            name="annualChange.stDev"
                            value={formData.annualChange.stDev || ''}
                            onChange={handleChange}
                            placeholder="stDev"
                            required
                        />
                    </>
                )}
            </div>
        )
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={() => navPage('/Homepage')} src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div />
            </nav>
            <nav className="navBarSub">
                <div/>
                {/*<button className="commonButton" onClick={popupMenu}>Menu</button>*/}
                {/*{sideElements()}*/}
                {loggedIn && <button className="commonButton" onClick={() => navPage('/Profset')}>Profile Setting</button>}
            </nav>
            <form onSubmit={handleSubmit} className="profileSetting">
                <div className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px' }}>Edit Expense Information</div>
                <div className="login">
                    <label htmlFor="name">Name</label>
                    <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} required />
                </div>
                <div className="login">
                    <label htmlFor="startYear.amountOrPercent">Start Year</label>
                    <select name="startYear.amountOrPercent" id="startYear.amountOrPercent" value={formData.startYear.amountOrPercent} onChange={handleChange} required>
                        <option value="AMOUNT">Amount</option>
                        {/*<option value="PERCENT">Percent</option>*/}
                    </select>
                </div>
                <div className="login">
                    <label htmlFor="startYear.distributionType">Distribution Type</label>
                    <select name="startYear.distributionType" id="startYear.distributionType" value={formData.startYear.distributionType} onChange={handleChange} required>
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseMone()}
                <div className="login">
                    <label htmlFor="duration.amountOrPercent">Duration</label>
                    <select name="duration.amountOrPercent" id="duration.amountOrPercent" value={formData.duration.amountOrPercent} onChange={handleChange} required>
                        <option value="AMOUNT">Amount</option>
                        {/*<option value="PERCENT">Percent</option>*/}
                    </select>
                </div>
                <div className="login">
                    <label htmlFor="duration.distributionType">Distribution Type</label>
                    <select name="duration.distributionType" id="duration.distributionType" value={formData.duration.distributionType} onChange={handleChange} required>
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseKone()}
                <div className="login">
                    <label htmlFor="initialAmount">Initial Amount</label>
                    <input type="number" onWheelCapture={e => e.target.blur()} id="initialAmount" name="initialAmount" value={formData.initialAmount} onChange={handleChange} required />
                </div>
                <div className="login">
                    <label htmlFor="annualChange.amountOrPercent">Annual Change</label>
                    <select name="annualChange.amountOrPercent" id="annualChange.amountOrPercent" value={formData.annualChange.amountOrPercent} onChange={handleChange} required>
                        <option value="AMOUNT">Amount</option>
                        <option value="PERCENT">Percent</option>
                    </select>
                </div>
                <div className="login">
                    <label htmlFor="annualChange.distributionType">Distribution Type</label>
                    <select name="annualChange.distributionType" id="annualChange.distributionType" value={formData.annualChange.distributionType} onChange={handleChange} required>
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseLone()}
                <div className="login">
                    <label htmlFor="inflationAdjustment">Inflation Adjustment</label>
                    <select name="inflationAdjustment" id="inflationAdjustment" value={formData.inflationAdjustment} onChange={handleChange} required>
                        <option value="Y">Yes</option>
                        <option value="N">No</option>
                    </select>
                </div>
                <div className="login">
                    <label htmlFor="userPercentage">User Percentage</label>
                    <input type="number" onWheelCapture={e => e.target.blur()} id="userPercentage" name="userPercentage" value={formData.userPercentage} onChange={handleChange} required style={{ width: '140px' }} />
                </div>
                <div className="login">
                    <label htmlFor="isDiscretionary">Discretionary</label>
                    <select name="isDiscretionary" id="isDiscretionary" value={formData.isDiscretionary} onChange={handleChange} required>
                        <option value="Y">Yes</option>
                        <option value="N">No</option>
                    </select>
                </div>
                <button className="submitButton" type="submit">Save Changes</button>
            </form>
        </div>
    )
}

export default expensePage
