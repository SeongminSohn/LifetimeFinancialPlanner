import React, { useState, useEffect } from 'react';
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

// 4.3 요구사항에 맞춘 스택형 바 차트 구현
export default function StackedBarChart() {
    // 투자 카테고리 및 세제 상태 정의
    const categories = [
        { key: 'cash', name: 'Cash', taxability: 'Taxable' },
        { key: 'sp500', name: 'S&P 500', taxability: 'Non-Taxable' },
        { key: 'bonds', name: 'Bonds', taxability: 'Taxable' },
        { key: 'tech', name: 'Tech Fund', taxability: 'Non-Taxable' },
    ];

    // 더미 시뮬레이션 데이터 생성 (50회 시뮬레이션, 2020~2030)
    const simulationsCount = 50;
    const years = Array.from({ length: 11 }, (_, i) => 2020 + i);

    const rawSimulations = Array.from({ length: simulationsCount }, () => {
        const simData = {};
        years.forEach(year => {
            simData[year] = {};
            categories.forEach(cat => {
                // 예시: 랜덤 값 생성
                simData[year][cat.key] = Math.random() * 1000 + 500;
            });
        });
        return simData;
    });

    // 상태: 평균/중앙값, 임계치, 가공된 데이터
    const [statistic, setStatistic] = useState('average');
    const [threshold, setThreshold] = useState(0);
    const [data, setData] = useState([]);

    // 데이터 가공: 연도별 카테고리별 평균 또는 중앙값 계산 후 임계치 이하 카테고리 'Other'로 합치기
    useEffect(() => {
        const processed = years.map(year => {
            const entry = { year: String(year) };
            categories.forEach(cat => {
                const vals = rawSimulations.map(sim => sim[year][cat.key]);
                const value = statistic === 'average'
                    ? vals.reduce((a, b) => a + b, 0) / vals.length
                    : (() => {
                        const sorted = vals.slice().sort((a, b) => a - b);
                        const mid = Math.floor(sorted.length / 2);
                        return sorted.length % 2 === 0
                            ? (sorted[mid - 1] + sorted[mid]) / 2
                            : sorted[mid];
                    })();
                entry[cat.key] = Number(value.toFixed(2));
            });
            return entry;
        });

        // 임계치 이하 카테고리 목록
        const lowCats = categories
            .filter(cat => processed.every(d => d[cat.key] < threshold))
            .map(cat => cat.key);

        // 최종 데이터: 'other' 포함
        const finalData = processed.map(d => {
            const obj = { year: d.year };
            let otherSum = 0;
            categories.forEach(cat => {
                if (lowCats.includes(cat.key)) otherSum += d[cat.key];
                else obj[cat.key] = d[cat.key];
            });
            if (lowCats.length) obj.other = Number(otherSum.toFixed(2));
            return obj;
        });

        setData(finalData);
    }, [statistic, threshold]);

    // 차트 시리즈 키: 세제 상태별 그룹화된 순서로
    const seriesKeys = [];
    categories
        .sort((a, b) => a.taxability.localeCompare(b.taxability))
        .forEach(cat => seriesKeys.push(cat.key));
    if (data[0] && data[0].other != null) seriesKeys.push('other');

    // 커스텀 툴팁: 총합 + 세그먼트별 값 표시
    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload) {
            const total = payload.reduce((sum, { value }) => sum + value, 0);
            return (
                <div style={{ background: '#fff', padding: 10, border: '1px solid #ccc' }}>
                    <strong>Year: {label}</strong>
                    <div>Total: {total.toFixed(2)}</div>
                    {payload.map((entry) => (
                        <div key={entry.name}>
                            {entry.name}: {entry.value}
                        </div>
                    ))}
                </div>
            );
        }
        return null;
    };

    // 시리즈별 색상
    const colors = ['#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#d0ed57'];

    return (
        <div style={{ width: '100%', maxWidth: 1200, margin: '0px auto', overflowX: 'auto', overflowY: 'auto', }}>
            <p><strong>Simulation Result</strong></p>
            <div style={{ marginBottom: 20, display: 'flex', alignItems: 'center' }}>
                <label>
                    Statistic:{' '}
                    <select value={statistic} onChange={e => setStatistic(e.target.value)}>
                        <option value="average">Average</option>
                        <option value="median">Median</option>
                    </select>
                </label>
                <label style={{ marginLeft: 20 }}>
                    Threshold:{' '}
                    <input
                        type="number"
                        value={threshold}
                        onChange={e => setThreshold(Number(e.target.value))}
                        placeholder="0"
                        style={{ width: 80 }}
                    />
                </label>
            </div>
            <div style={{ width: "50vw", height: "50vh" }}>
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="year" type="category" interval={0} />
                    <YAxis />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend />
                    {seriesKeys.map((key, idx) => (
                        <Bar
                            key={key}
                            dataKey={key}
                            stackId="stack"
                            name={categories.find(c => c.key === key)?.name || 'Other'}
                            fill={colors[idx % colors.length]}
                        />
                    ))}
                </BarChart>
            </ResponsiveContainer>
            </div>
        </div>
    );
}