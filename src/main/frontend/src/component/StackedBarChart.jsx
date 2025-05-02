import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    ResponsiveContainer,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
} from 'recharts';

export default function StackedBarChart({ simulationId, allocations }) {
    const [yearsData, setYearsData] = useState([]);
    const [chartData, setChartData] = useState([]);
    const [statistic, setStatistic] = useState('average');
    const [threshold, setThreshold] = useState(0);

    // Fetch simulationYears for the given simulationId
    useEffect(() => {
        if (!simulationId) return;
        axios
            .get(`http://localhost:10000/api/charts/simulations/${simulationId}`)
            .then(res => {
                const sims = res.data.simulationYears;
                if (Array.isArray(sims)) setYearsData(sims);
            })
            .catch(err => console.error(err));
    }, [simulationId]);

    // Process data: group by year, compute average or median, apply threshold aggregation
    useEffect(() => {
        if (!yearsData.length || !allocations) return;

        // 1. Group records by year
        const groups = yearsData.reduce((acc, y) => {
            if (!acc[y.year]) acc[y.year] = [];
            acc[y.year].push(y);
            return acc;
        }, {});

        // 2 Compute statistic per year and allocation key
        const processed = Object.entries(groups).map(([year, records]) => {
            const row = { year };
            Object.entries(allocations).forEach(([key, ratio]) => {
                const values = records.map(r => r.totalInvestments * ratio);
                let val;
                if (statistic === 'median') {
                    const sorted = values.slice().sort((a, b) => a - b);
                    const mid = Math.floor(sorted.length / 2);
                    val = sorted.length % 2 === 0 ? (sorted[mid - 1] + sorted[mid]) / 2 : sorted[mid];
                } else {
                    const sum = values.reduce((a, b) => a + b, 0);
                    val = sum / values.length;
                }
                row[key] = Number(val.toFixed(2));
            });
            return row;
        });

        // 3 Identify keys below threshold in every year
        const lowKeys = Object.keys(allocations).filter(key =>
            processed.every(d => d[key] < threshold)
        );

        // 4 Aggregate low keys into 'other'
        const finalData = processed.map(d => {
            const row = { year: d.year };
            let other = 0;
            Object.keys(allocations).forEach(key => {
                if (lowKeys.includes(key)) other += d[key];
                else row[key] = d[key];
            });
            if (other > 0) row.other = Number(other.toFixed(2));
            return row;
        });

        setChartData(finalData);
    }, [yearsData, allocations, statistic, threshold]);

    // Sort keys so retirement status groups adjacent, include 'other' if present
    const keys = Object.keys(allocations)
        .sort((a, b) => {
            const aAcc = a.split(' ').pop();
            const bAcc = b.split(' ').pop();
            if (aAcc === bAcc) return a.localeCompare(b);
            return aAcc.localeCompare(bAcc);
        })
        .concat(chartData[0]?.other ? ['other'] : []);

    const colors = ['#1f77b4', '#ff7f0e', '#2ca02c', '#d62728', '#9467bd', '#8c564b', '#e377c2', '#7f7f7f', '#17becf'];




    return (
        <div style={{ width: '100%', height: '100%' }}>
            {/* Controls for statistic and threshold */}
            <div style={{ marginBottom: '12px' }}>
                <button onClick={() => setStatistic('average')} disabled={statistic === 'average'}>
                    Average
                </button>
                <button onClick={() => setStatistic('median')} disabled={statistic === 'median'} style={{ marginLeft: '8px' }}>
                    Median
                </button>
                <label style={{ marginLeft: '16px' }}>
                    Threshold:
                    <input
                        type="number"
                        value={threshold}
                        onChange={e => setThreshold(Number(e.target.value))}
                        style={{ width: '80px', marginLeft: '4px' }}
                    />
                </label>
            </div>

            {/* Chart container */}
            <ResponsiveContainer width="100%" height="90%">
                <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {keys.map((key, idx) => (
                        <Bar key={key} dataKey={key} stackId="stack" name={key} fill={colors[idx % colors.length]} />
                    ))}
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}
