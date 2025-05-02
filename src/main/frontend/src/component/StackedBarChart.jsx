// src/component/StackedBarChart.jsx
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

    // 1) simulationId 바뀔 때마다 서버에서 연도별 데이터 불러오기
    useEffect(() => {
        if (!simulationId) return;
        const scenarioId = localStorage.getItem('scenario');
        axios
            .get(
                `http://localhost:10000/api/charts/${simulationId}`
            )
            .then(res => {
                if (Array.isArray(res.data)) setYearsData(res.data);
            })
            .catch(err => console.error(err));
    }, [simulationId]);

    // 2) allocations 비율에 맞춰 totalInvestments 분해 → chartData 생성
    useEffect(() => {
        if (!yearsData.length || !allocations) return;

        const data = yearsData.map(y => {
            const row = { year: y.year };
            Object.entries(allocations).forEach(([key, ratio]) => {
                row[key] = Number((y.totalInvestments * ratio).toFixed(2));
            });
            return row;
        });

        setChartData(data);
    }, [yearsData, allocations]);

    // 3) 같은 accountType(NON-RETIREMENT vs RETIREMENT)끼리 인접 표시
    const keys = Object.keys(allocations).sort((a, b) => {
        const aAcc = a.split(' ').pop();
        const bAcc = b.split(' ').pop();
        if (aAcc === bAcc) return a.localeCompare(b);
        return aAcc.localeCompare(bAcc);
    });

    // 4) 시리즈별 색상
    const colors = [
        '#8884d8',
        '#82ca9d',
        '#ffc658',
        '#ff8042',
        '#8dd1e1',
        '#d0ed57',
        '#a4de6c',
    ];

    return (
        <ResponsiveContainer width="100%" height="100%">
            <BarChart
                data={chartData}
                margin={{ top: 20, right: 30, left: 20, bottom: 5 }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="year" />
                <YAxis />
                <Tooltip />
                <Legend />
                {keys.map((key, idx) => (
                    <Bar
                        key={key}
                        dataKey={key}
                        stackId="stack"
                        name={key}
                        fill={colors[idx % colors.length]}
                    />
                ))}
            </BarChart>
        </ResponsiveContainer>
    );
}
