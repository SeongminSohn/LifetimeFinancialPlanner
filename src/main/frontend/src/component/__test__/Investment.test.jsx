import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import Investment from '../../pages/investment';
import { MemoryRouter } from 'react-router-dom';

jest.mock('axios');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));

describe('Investment Component', () => {
    const existingInvestments = [];
    const investmentTypes = [
        {
            id: 1,
            name: 'Stocks',
            description: 'Equity investments',
            expectedAnnualReturn: { value: 5, lower: null, upper: null, mean: null, stDev: null },
            expenseRatio: 0.01,
            expectedAnnualIncome: { value: 2, lower: null, upper: null, mean: null, stDev: null },
            taxability: 'T',
        },
    ];

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem('scenario', '1');
        axios.get
            .mockResolvedValueOnce({ data: existingInvestments })
            .mockResolvedValueOnce({ data: investmentTypes });
    });

    it('fetches and displays investment types', async () => {
        render(
            <MemoryRouter>
                <Investment />
            </MemoryRouter>
        );

        // 버튼 텍스트로 Stocks 표시 확인
        const stockButton = await screen.findByText('Stocks');
        expect(stockButton).toBeInTheDocument();
    });

    it('shows details when clicking VIEW INVESTMENT TYPE', async () => {
        render(
            <MemoryRouter>
                <Investment />
            </MemoryRouter>
        );

        const viewBtn = await screen.findByText('VIEW INVESTMENT TYPE');
        fireEvent.click(viewBtn);
        expect(screen.getByText('Equity investments')).toBeInTheDocument();
    });

    it('navigates to add and save routes', async () => {
        render(
            <MemoryRouter>
                <Investment />
            </MemoryRouter>
        );

        const addBtn = await screen.findByText('Add Investment Type');
        fireEvent.click(addBtn);
        expect(mockNavigate).toHaveBeenCalledWith('/InvestEdit');

        const saveBtn = screen.getByText('Save');
        fireEvent.click(saveBtn);
        expect(mockNavigate).toHaveBeenCalledWith('/InvestEvent');
    });
});
