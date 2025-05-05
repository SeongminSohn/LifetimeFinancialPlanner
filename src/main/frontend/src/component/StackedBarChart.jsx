import React, { useState, useEffect, useMemo } from 'react';
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

export default function StackedBarChart({ simulationId }) {
    const [yearsData, setYearsData] = useState([]);
    const [chartData, setChartData] = useState([]);
    const [statistic, setStatistic] = useState('average');
    const [threshold, setThreshold] = useState(0);

    useEffect(() => {
        if (!simulationId) return;
        axios
            .get(`http://localhost:10000/api/charts/simulations/${simulationId}`)
            .then(res => setYearsData(res.data?.simulationYears || []))
            .catch(console.error);
    }, [simulationId]);

    const allKeys = useMemo(() => {
        const s = new Set();
        yearsData.forEach(y => Object.keys(y.assetAllocations || {}).forEach(k => s.add(k)));
        return [...s];
    }, [yearsData]);

    useEffect(() => {
        if (!yearsData.length) return;
        const grouped = {};
        yearsData.forEach(r => {
            if (!grouped[r.year]) grouped[r.year] = [];
            grouped[r.year].push(r);
        });
        const processed = Object.entries(grouped).map(([year, recs]) => {
            const row = { year };
            allKeys.forEach(k => {
                const vals = recs.map(r => (r.assetAllocations?.[k] || 0) * r.totalInvestments);
                const val =
                    statistic === 'median'
                        ? (() => {
                            const srt = vals.slice().sort((a, b) => a - b);
                            const mid = Math.floor(srt.length / 2);
                            return srt.length % 2 ? srt[mid] : (srt[mid - 1] + srt[mid]) / 2;
                        })()
                        : vals.reduce((a, b) => a + b, 0) / vals.length;
                row[k] = +val.toFixed(2);
            });
            return row;
        });
        const lowKeys = allKeys.filter(k => processed.every(d => d[k] < threshold));
        const final = processed.map(d => {
            const row = { year: d.year };
            let other = 0;
            allKeys.forEach(k => {
                if (lowKeys.includes(k)) other += d[k];
                else row[k] = d[k];
            });
            if (other) row.other = +other.toFixed(2);
            return row;
        });
        setChartData(final);
    }, [yearsData, allKeys, statistic, threshold]);

    const orderedKeys = useMemo(() => {
        const ks = allKeys.slice().sort((a, b) => {
            const accA = a.split(' ').pop();
            const accB = b.split(' ').pop();
            return accA === accB ? a.localeCompare(b) : accA.localeCompare(accB);
        });
        if (chartData[0]?.other) ks.push('other');
        return ks;
    }, [allKeys, chartData]);

    const palette = [
        '#1f77b4',
        '#ff7f0e',
        '#2ca02c',
        '#d62728',
        '#9467bd',
        '#8c564b',
        '#e377c2',
        '#7f7f7f',
        '#17becf',
        '#bcbd22',
    ];

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <div style={{ marginBottom: '12px' }}>
                <button onClick={() => setStatistic('average')} disabled={statistic === 'average'}>
                    Average
                </button>
                <button
                    onClick={() => setStatistic('median')}
                    disabled={statistic === 'median'}
                    style={{ marginLeft: 8 }}
                >
                    Median
                </button>
                <label style={{ marginLeft: 16 }}>
                    Threshold:
                    <input
                        type="number"
                        value={threshold}
                        onChange={e => setThreshold(+e.target.value)}
                        style={{ width: 80, marginLeft: 4 }}
                    />
                </label>
            </div>
            <ResponsiveContainer width="100%" height="90%">
                <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" />
                    <YAxis />
                    <Tooltip />
                    <Legend />
                    {orderedKeys.map((k, i) => (
                        <Bar key={k} dataKey={k} stackId="stack" fill={palette[i % palette.length]} />
                    ))}
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}
