import React, { useMemo, useState } from 'react';
import {
    ResponsiveContainer, BarChart, Bar,
    XAxis, YAxis, CartesianGrid, Tooltip, Legend,
} from 'recharts';

export default function StackedBarChart({ simulations }) {
    const [stat, setStat] = useState('average'); // 'average' | 'median'
    const [threshold, setThreshold] = useState(0);

    const flatYears = useMemo(
        () => simulations.flatMap(s => s.simulationYears),
        [simulations]
    );

    const allKeys = useMemo(() => {
        const set = new Set();
        flatYears.forEach(y =>
            Object.keys(y.assetAllocations || {}).forEach(k => set.add(k))
        );
        return [...set];
    }, [flatYears]);

    const chartData = useMemo(() => {
        if (!flatYears.length) return [];
        const byYear = flatYears.reduce((acc, rec) => {
            if (!acc[rec.year]) acc[rec.year] = [];
            acc[rec.year].push(rec);
            return acc;
        }, {});
        const rows = Object.entries(byYear).map(([year, recs]) => {
            const row = { year };
            allKeys.forEach(k => {
                const vals = recs.map(r => (r.assetAllocations?.[k] || 0) * r.totalInvestments);
                const v = stat === 'median'
                    ? (() => {
                        const s = vals.slice().sort((a, b) => a - b);
                        const m = Math.floor(s.length / 2);
                        return s.length % 2 ? s[m] : (s[m - 1] + s[m]) / 2;
                    })()
                    : vals.reduce((a, b) => a + b, 0) / vals.length;
                row[k] = +v.toFixed(2);
            });
            return row;
        });
        const low = allKeys.filter(k => rows.every(r => r[k] < threshold));
        return rows.map(r => {
            const obj = { year: r.year };
            let other = 0;
            allKeys.forEach(k => {
                if (low.includes(k)) other += r[k];
                else obj[k] = r[k];
            });
            if (other) obj.other = +other.toFixed(2);
            return obj;
        });
    }, [flatYears, allKeys, stat, threshold]);

    const keys = useMemo(() => {
        const k = allKeys.slice().sort();
        if (chartData[0]?.other) k.push('other');
        return k;
    }, [allKeys, chartData]);

    const palette = ['#1f77b4','#ff7f0e','#2ca02c','#d62728','#9467bd','#8c564b','#e377c2','#7f7f7f','#17becf','#bcbd22'];

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <div style={{ marginBottom: 12 }}>
                <button onClick={() => setStat('average')} disabled={stat==='average'}>Average</button>
                <button onClick={() => setStat('median')}  disabled={stat==='median'}  style={{ marginLeft: 8 }}>Median</button>
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
                    {keys.map((k, i) => (
                        <Bar key={k} dataKey={k} stackId="stack" fill={palette[i % palette.length]} />
                    ))}
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}
