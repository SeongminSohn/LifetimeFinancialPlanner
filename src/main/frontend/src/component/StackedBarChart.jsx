import React, { useMemo, useState } from 'react';
import {
    ResponsiveContainer, BarChart, Bar,
    XAxis, YAxis, CartesianGrid, Tooltip, Legend,
} from 'recharts';

export default function StackedBarChart({ simulations }) {
    const [chartType, setChartType] = useState('investments');
    const [stat, setStat] = useState('average');
    const [threshold, setThreshold] = useState();
    const [tmp, setTmp] = useState('0');

    const targetBatchId = simulations[0]?.batchId;
    const filtered = useMemo(
        () => simulations.filter(s => s.batchId === targetBatchId),
        [simulations, targetBatchId]
    );

    const flatYears = useMemo(
        () => filtered.flatMap(s => s.simulationYears),
        [filtered]
    );

    const allKeys = useMemo(() => {
        const set = new Set();
        flatYears.forEach(y => {
            if (chartType === 'income') {
                set.add('Income');
                set.add('SocialSecurity');
            } else if (chartType === 'investments') {
                (y.assetAllocations || []).forEach(a => set.add(a.investmentKey));
            } else {
                (y.expenseBreakdowns || []).forEach(e => set.add(e.name));
                set.add('Taxes');
            }
        });
        return [...set];
    }, [flatYears, chartType]);

    const chartData = useMemo(() => {
        if (!flatYears.length) return [];
        const byYear = flatYears.reduce((acc, rec) => {
            acc[rec.year] ? acc[rec.year].push(rec) : (acc[rec.year] = [rec]);
            return acc;
        }, {});
        const rows = Object.entries(byYear).map(([year, recs]) => {
            const row = { year };
            allKeys.forEach(k => {
                const vals = recs.map(r => {
                    if (chartType === 'income') {
                        if (k === 'Income') return r.curYearIncome;
                        if (k === 'SocialSecurity') return r.curYearSS;
                        return 0;
                    } else if (chartType === 'investments') {
                        const found = (r.assetAllocations || []).find(a => a.investmentKey === k);
                        return found ? found.ratio * r.totalInvestments : 0;
                    } else {
                        if (k === 'Taxes') return r.totalTax || 0;
                        const exp = (r.expenseBreakdowns || []).find(e => e.name === k);
                        return exp ? exp.amount : 0;
                    }
                });
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

        return rows.map(r => {
            const obj = { year: r.year };
            let other = 0;
            allKeys.forEach(k => {
                if (r[k] < threshold) other += r[k];
                else obj[k] = r[k];
            });
            if (other) obj.other = +other.toFixed(2);
            return obj;
        });
    }, [flatYears, allKeys, stat, threshold, chartType]);

    const keys = useMemo(() => {
        const base = allKeys.slice();
        if (chartType === 'investments') {
            base.sort((a, b) => {
                const getTax = s => s.split(' ').slice(-1)[0];
                const ta = getTax(a), tb = getTax(b);
                return ta === tb ? a.localeCompare(b) : ta.localeCompare(tb);
            });
        } else {
            base.sort();
        }
        if (chartData[0]?.other) base.push('other');
        return base;
    }, [allKeys, chartData, chartType]);

    const palette = ['#1f77b4','#ff7f0e','#2ca02c','#d62728','#9467bd','#8c564b','#e377c2','#7f7f7f','#17becf','#bcbd22'];

    return (
        <div style={{ width: '100%', height: '100%' }}>
            <div style={{ marginBottom: 12 }}>
                <button onClick={() => setChartType('income')}      disabled={chartType === 'income'}      style={{ marginLeft: 8 }}>Income</button>
                <button onClick={() => setChartType('investments')} disabled={chartType === 'investments'} style={{ marginLeft: 8 }}>Investments</button>
                <button onClick={() => setChartType('expenses')}    disabled={chartType === 'expenses'}    style={{ marginLeft: 8 }}>Expenses</button>
                <span style={{ marginLeft: 16 }}>|</span>
                <button onClick={() => setStat('average')} disabled={stat === 'average'} style={{ marginLeft: 16 }}>Average</button>
                <button onClick={() => setStat('median')}  disabled={stat === 'median'}  style={{ marginLeft: 8 }}>Median</button>
                <label style={{ marginLeft: 16 }}>
                    Threshold:
                    <input
                        type="number"
                        value={tmp}
                        onChange={e => setTmp(e.target.value)}
                        onBlur={() => setThreshold(Number(tmp) || 0)}
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
