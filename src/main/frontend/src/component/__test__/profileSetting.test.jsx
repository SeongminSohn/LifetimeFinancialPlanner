import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import ProfileSetting from '../../pages/profileSetting';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { jest } from '@jest/globals';

const mock = new MockAdapter(axios);

function DummyInvestment() {
    return <div>Investment Page</div>;
}

function DummyHome() {
    return <div>Homepage</div>;
}

describe('ProfileSetting Component', () => {
    beforeEach(() => {
        localStorage.clear();
        mock.reset();
        jest.clearAllMocks();
    });

    test('should load initial scenario data from API and populate the form when scenario exists in localStorage', async () => {
        localStorage.setItem('token', 'dummy-token');
        localStorage.setItem('scenario', 'scenario-001');

        const dummyScenarioData = {
            userId: 'dummy-token',
            name: 'Test Scenario',
            maritalStatus: 'Y',
            birthYearUser: '1980',
            birthYearSpouse: '1985',
            financialGoal: '1000',
            afterTaxContributionLimit: '500',
            stateOfResidence: 'CA',
            lifeExpectancyUser: {
                amountOrPercent: 'AMOUNT',
                distributionType: 'FIXED',
                value: '75',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            },
            lifeExpectancySpouse: {
                amountOrPercent: 'AMOUNT',
                distributionType: 'FIXED',
                value: '70',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            },
            inflationAssumption: {
                amountOrPercent: 'PERCENT',
                distributionType: 'FIXED',
                value: '2',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            }
        };

        mock.onGet('http://localhost:10000/api/scenarios/scenario-001').reply(200, dummyScenarioData);

        render(
            <MemoryRouter initialEntries={['/']}>
                <Routes>
                    <Route path="/" element={<ProfileSetting />} />
                </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByLabelText('Scenario Name')).toHaveValue('Test Scenario');
        });
    });

    test('should send a PUT request and navigate to Investment page when updating an existing scenario', async () => {
        localStorage.setItem('token', 'dummy-token');
        localStorage.setItem('scenario', 'scenario-001');

        const dummyScenarioData = {
            userId: 'dummy-token',
            name: 'Test Scenario',
            maritalStatus: 'Y',
            birthYearUser: '1980',
            birthYearSpouse: '1985',
            financialGoal: '1000',
            afterTaxContributionLimit: '500',
            stateOfResidence: 'CA',
            lifeExpectancyUser: {
                amountOrPercent: 'AMOUNT',
                distributionType: 'FIXED',
                value: '75',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            },
            lifeExpectancySpouse: {
                amountOrPercent: 'AMOUNT',
                distributionType: 'FIXED',
                value: '70',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            },
            inflationAssumption: {
                amountOrPercent: 'PERCENT',
                distributionType: 'FIXED',
                value: '2',
                lower: null,
                upper: null,
                mean: null,
                stDev: null
            }
        };

        mock.onGet('http://localhost:10000/api/scenarios/scenario-001').reply(200, dummyScenarioData);
        mock.onPut('http://localhost:10000/api/scenarios/scenario-001').reply(200, { success: true });

        render(
            <MemoryRouter initialEntries={['/']}>
                <Routes>
                    <Route path="/" element={<ProfileSetting />} />
                    <Route path="/Investment" element={<DummyInvestment />} />
                </Routes>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByLabelText('Scenario Name')).toHaveValue('Test Scenario');
        });

        fireEvent.change(screen.getByLabelText('Scenario Name'), { target: { value: 'Updated Scenario' } });
        fireEvent.submit(screen.getByRole('button', { name: 'Save Changes' }));

        await waitFor(() => {
            expect(mock.history.put.length).toBeGreaterThan(0);
            expect(mock.history.put[0].url).toBe('http://localhost:10000/api/scenarios/scenario-001');
        });

        await waitFor(() => {
            expect(screen.getByText('Investment Page')).toBeInTheDocument();
        });
    });

    test('should send a POST request, store the new scenario ID, and navigate to Investment page when no scenario exists', async () => {
        localStorage.setItem('token', 'dummy-token');

        mock.onPost('http://localhost:10000/api/scenarios').reply(200, { scenarioId: 'scenario-NEW' });

        render(
            <MemoryRouter initialEntries={['/']}>
                <Routes>
                    <Route path="/" element={<ProfileSetting />} />
                    <Route path="/Investment" element={<DummyInvestment />} />
                </Routes>
            </MemoryRouter>
        );

        fireEvent.change(screen.getByLabelText('Scenario Name'), { target: { value: 'New Scenario' } });
        fireEvent.change(screen.getByPlaceholderText('YYYY'), { target: { value: '1990' } });
        fireEvent.change(screen.getByLabelText('Financial Goal'), { target: { value: '2000' } });
        fireEvent.change(screen.getByLabelText('after TaxContribution Limit'), { target: { value: '300' } });

        fireEvent.submit(screen.getByRole('button', { name: 'Save Changes' }));

        await waitFor(() => {
            expect(mock.history.post.length).toBeGreaterThan(0);
            expect(mock.history.post[0].url).toBe('http://localhost:10000/api/scenarios');
        });

        await waitFor(() => {
            expect(localStorage.getItem('scenario')).toBe('scenario-NEW');
        });

        await waitFor(() => {
            expect(screen.getByText('Investment Page')).toBeInTheDocument();
        });
    });

    test('should navigate to Homepage when Back button is clicked', async () => {
        localStorage.setItem('token', 'dummy-token');

        render(
            <MemoryRouter initialEntries={['/']}>
                <Routes>
                    <Route path="/" element={<ProfileSetting />} />
                    <Route path="/Homepage" element={<DummyHome />} />
                </Routes>
            </MemoryRouter>
        );

        fireEvent.click(screen.getByRole('button', { name: 'Back' }));

        await waitFor(() => {
            expect(screen.getByText('Homepage')).toBeInTheDocument();
        });
    });
});
