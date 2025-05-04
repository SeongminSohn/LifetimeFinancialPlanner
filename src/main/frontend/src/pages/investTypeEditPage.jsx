import React, { useState, useEffect } from 'react';
import './common.css';
import axios from 'axios';
import { useNavigate, useParams } from 'react-router-dom';

function investPage() {
    const { id } = useParams();
    const navPage = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) setLoggedIn(true);
    }, []);

    useEffect(() => {
        if (!id) return;
        axios.get(`http://localhost:10000/api/investment-types/${id}`, { withCredentials: true })
            .then(res => {
                const data = res.data;
                setFormData({
                    scenarioId: data.scenarioId,
                    name: data.name,
                    description: data.description,
                    expectedAnnualReturn: { ...data.expectedAnnualReturn },
                    expenseRatio: data.expenseRatio,
                    expectedAnnualIncome: { ...data.expectedAnnualIncome },
                    taxability: data.taxability
                });
                console.log("The Data: ",data)
            })
            .catch(err => console.error('Fetch error:', err));
    }, [id]);

    const [openSide, setSide] = useState(false);
    const [loggedIn, setLoggedIn] = useState(false);
    const [formData, setFormData] = useState({
        scenarioId: '',
        name: 'CASH',
        description: '',
        expectedAnnualReturn: {
            amountOrPercent: 'AMOUNT',
            distributionType: 'FIXED',
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        expenseRatio: '',
        expectedAnnualIncome: {
            amountOrPercent: 'AMOUNT',
            distributionType: 'FIXED',
            value: null,
            lower: null,
            upper: null,
            mean: null,
            stDev: null
        },
        taxability: 'Y'
    });

    const popupMenu = () => setSide(prev => !prev);

    function sideElements() {
        return openSide && (
            <aside className="sidebar">
                <button onClick={toIncome}>Income Edit</button>
                <button onClick={toExpense}>Expense Edit</button>
                <button onClick={toWithDrawal}>Expense Withdrawal Edit</button>
                <button onClick={toInvestment}>Investment</button>
                <button onClick={toSim}>Scenario Simulation</button>
                <button disabled>Import & Export Data</button>
            </aside>
        );
    }

    function toInvestment() { navPage('/Investment'); }
    function toWithDrawal() { navPage('/ExpenseW'); }
    function toIncome() { navPage('/IncomePage'); }
    function toExpense() { navPage('/ExpenseEdit'); }
    function toSim() { navPage('/simulationPage'); }
    function toHome() { navPage('/Homepage'); }
    function toProfile() { navPage('/Profset'); }

    async function handleSubmit(e) {
        e.preventDefault();
        const { distributionType: distU, lower: lowU, upper: upU } = formData.expectedAnnualReturn;
        if (distU === 'UNIFORM' && Number(upU) <= Number(lowU)) {
            alert('Upper Value has to be greater than lower value for Expected Annual Return');
            return;
        }
        const { distributionType: distI, lower: lowI, upper: upI } = formData.expectedAnnualIncome;
        if (distI === 'UNIFORM' && Number(upI) <= Number(lowI)) {
            alert('Upper Value has to be greater than lower value for Expected Annual Income.');
            return;
        }
        const payload = {
            scenarioId: formData.scenarioId,
            name: formData.name,
            description: formData.description,
            expectedAnnualReturn: formData.expectedAnnualReturn,
            expenseRatio: formData.expenseRatio,
            expectedAnnualIncome: formData.expectedAnnualIncome,
            taxability: formData.taxability
        };
        try {
            await axios.put(
                `http://localhost:10000/api/investment-types/${id}`,
                payload,
                { withCredentials: true, headers: { 'Content-Type': 'application/json' } }
            );
            alert('Updated successfully');
            navPage('/Investment');
        } catch (error) {
            console.error('Update error:', error);
            alert('Try again');
        }
    }

    const handleChange = (e) => {
        const { name, value } = e.target;

        if (name === 'expectedAnnualReturn.amountOrPercent') {
            setFormData(prev => ({
                ...prev,
                expectedAnnualReturn: {
                    ...prev.expectedAnnualReturn,
                    amountOrPercent: value,
                    value: null, lower: null, upper: null, mean: null, stDev: null
                }
            }));
            return;
        }

        if (name === 'expectedAnnualReturn.distributionType') {
            setFormData(prev => ({
                ...prev,
                expectedAnnualReturn: {
                    ...prev.expectedAnnualReturn,
                    distributionType: value,
                    value: null, lower: null, upper: null, mean: null, stDev: null
                }
            }));
            return;
        }

        if (name === 'expectedAnnualIncome.amountOrPercent') {
            setFormData(prev => ({
                ...prev,
                expectedAnnualIncome: {
                    ...prev.expectedAnnualIncome,
                    amountOrPercent: value,
                    value: null, lower: null, upper: null, mean: null, stDev: null
                }
            }));
            return;
        }

        if (name === 'expectedAnnualIncome.distributionType') {
            setFormData(prev => ({
                ...prev,
                expectedAnnualIncome: {
                    ...prev.expectedAnnualIncome,
                    distributionType: value,
                    value: null, lower: null, upper: null, mean: null, stDev: null
                }
            }));
            return;
        }

        if (name === 'name') {
            const taxability = value === 'S&P 500' ? 'Y' :
                value === 'TAX-EXEMPT BONDS' ? 'N' : formData.taxability;
            setFormData(prev => ({ ...prev, name: value, taxability }));
            return;
        }

        if (name === 'taxability') {
            if (
                (formData.name === 'S&P 500' && value === 'N') ||
                (formData.name === 'TAX-EXEMPT BONDS' && value === 'Y')
            ) return;
            setFormData(prev => ({ ...prev, taxability: value }));
            return;
        }

        if (name === 'expenseRatio') {
            let num = parseFloat(value);
            if (isNaN(num)) num = 0;
            num = Math.max(0, Math.min(1, num));
            setFormData(prev => ({ ...prev, expenseRatio: num }));
            return;
        }

        if (name.includes('.') && formData[name.split('.')[0]].amountOrPercent === 'PERCENT') {
            const [parent, child] = name.split('.');
            let num = parseFloat(value);
            num = isNaN(num) ? '' : Math.max(0, Math.min(1, num));
            setFormData(prev => ({
                ...prev,
                [parent]: { ...prev[parent], [child]: num }
            }));
            return;
        }

        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData(prev => ({
                ...prev,
                [parent]: { ...prev[parent], [child]: value }
            }));
        } else {
            setFormData(prev => ({ ...prev, [name]: value }));
        }
    };

    function chooseMone() {
        return (
            <div>
                {formData.expectedAnnualReturn.distributionType === 'FIXED' && (
                    <input
                        type="number"
                        name="expectedAnnualReturn.value"
                        placeholder="value"
                        value={formData.expectedAnnualReturn.value ?? ''}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.expectedAnnualReturn.distributionType === 'UNIFORM' && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualReturn.lower"
                            placeholder="Lower"
                            value={formData.expectedAnnualReturn.lower ?? ''}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualReturn.upper"
                            placeholder="Upper"
                            value={formData.expectedAnnualReturn.upper ?? ''}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.expectedAnnualReturn.distributionType === 'NORMAL' && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualReturn.mean"
                            placeholder="mean"
                            value={formData.expectedAnnualReturn.mean ?? ''}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualReturn.stDev"
                            placeholder="standard deviation"
                            value={formData.expectedAnnualReturn.stDev ?? ''}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function chooseKone() {
        return (
            <div>
                {formData.expectedAnnualIncome.distributionType === 'FIXED' && (
                    <input
                        type="number"
                        name="expectedAnnualIncome.value"
                        placeholder="value"
                        value={formData.expectedAnnualIncome.value ?? ''}
                        onChange={handleChange}
                        required
                    />
                )}
                {formData.expectedAnnualIncome.distributionType === 'UNIFORM' && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualIncome.lower"
                            placeholder="Lower"
                            value={formData.expectedAnnualIncome.lower ?? ''}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualIncome.upper"
                            placeholder="Upper"
                            value={formData.expectedAnnualIncome.upper ?? ''}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
                {formData.expectedAnnualIncome.distributionType === 'NORMAL' && (
                    <div>
                        <input
                            type="number"
                            name="expectedAnnualIncome.mean"
                            placeholder="mean"
                            value={formData.expectedAnnualIncome.mean ?? ''}
                            onChange={handleChange}
                            required
                        />
                        <input
                            type="number"
                            name="expectedAnnualIncome.stDev"
                            placeholder="standard deviation"
                            value={formData.expectedAnnualIncome.stDev ?? ''}
                            onChange={handleChange}
                            required
                        />
                    </div>
                )}
            </div>
        );
    }

    function investManage() {
        return (
            <form onSubmit={handleSubmit} className="profileSetting">
                <div className="logoLetter" style={{ color: 'black', fontSize: '5vh', marginTop: '30px' }}>Edit Invest Information</div>
                <div className="login"><label htmlFor="name">name </label>
                    <select name="name" id="name" value={formData.name} onChange={handleChange} required>
                        <option value="CASH">Cash</option>
                        <option value="S&P 500">S&P 500</option>
                        <option value="TAX-EXEMPT BONDS">Tax-exempt Bonds</option>
                    </select>
                </div>
                <div className="login"><label htmlFor="description">Description </label>
                    <input
                        type="text"
                        id="description"
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        placeholder="Description"
                        required
                    />
                </div>
                <div className="login"><label htmlFor="expectedAnnualReturn.amountOrPercent">Expected Annual Return</label>
                    <select
                        name="expectedAnnualReturn.amountOrPercent"
                        id="expectedAnnualReturn.amountOrPercent"
                        value={formData.expectedAnnualReturn.amountOrPercent}
                        onChange={handleChange} required>
                        <option value="AMOUNT">Amount</option>
                        <option value="PERCENT">Percent</option>
                    </select>
                </div>
                <div className="login"><label htmlFor="expectedAnnualReturndistributionType">Distribution Type </label>
                    <select name="expectedAnnualReturn.distributionType" id="expectedAnnualReturndistributionType" value={formData.expectedAnnualReturn.distributionType} onChange={handleChange} required>
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseMone()}
                <div className="login"><label htmlFor="expenseRatio">Expense Ratio </label>
                    <input
                        type="number"
                        id="expenseRatio"
                        name="expenseRatio"
                        value={formData.expenseRatio}
                        onChange={handleChange}
                        placeholder="Expense Ratio"
                        style={{ width: '140px' }}
                        required
                    />
                </div>
                <div className="login"><label htmlFor="expectedAnnualIncome.amountOrPercent">Expected Annual Income</label>
                    <select
                        name="expectedAnnualIncome.amountOrPercent"
                        id="expectedAnnualIncome.amountOrPercent"
                        value={formData.expectedAnnualIncome.amountOrPercent}
                        onChange={handleChange} required>
                        <option value="AMOUNT">Amount</option>
                        <option value="PERCENT">Percent</option>
                    </select>
                </div>
                <div className="login"><label htmlFor="expectedAnnualIncomedistributionType">Distribution Type </label>
                    <select name="expectedAnnualIncome.distributionType" id="expectedAnnualIncome.distributionType" value={formData.expectedAnnualIncome.distributionType} onChange={handleChange} required>
                        <option value="FIXED">FIXED</option>
                        <option value="UNIFORM">UNIFORM</option>
                        <option value="NORMAL">NORMAL</option>
                    </select>
                </div>
                {chooseKone()}
                <div className="login"><label htmlFor="taxability">Tax Ability </label>
                    <select
                        name="taxability"
                        id="taxability"
                        value={formData.taxability}
                        onChange={handleChange} required>
                        <option value="Y">Yes</option>
                        <option value="N">No</option>
                    </select>
                </div>
                <div>
                    <button onClick={toProfile} className="submitButton" type="button" style={{ marginBottom: '20px' }}>Back</button>
                    <button className="submitButton" type="submit">Save Changes</button>
                </div>
            </form>
        );
    }

    return (
        <div className="total">
            <nav className="navBarTop">
                <img onClick={toHome} src="/public/caffeineOverloadLogo.png" className="logoSize" alt="logo" />
                <p className="logoLetter">Life Time Financial Planner</p>
                <div></div>
            </nav>
            <nav className="navBarSub">
                <div />
                {/*<button className="commonButton" onClick={popupMenu}>Menu</button>*/}
                {/*{sideElements()}*/}
                {loggedIn && (
                    <button className="commonButton" onClick={toProfile}>
                        Scenario Setting
                    </button>
                )}
            </nav>
            {investManage()}
        </div>
    );
}

export default investPage;
