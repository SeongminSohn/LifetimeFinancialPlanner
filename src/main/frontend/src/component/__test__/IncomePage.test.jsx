import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import IncomePage from '../../pages/incomePage';
import { MemoryRouter } from 'react-router-dom';

jest.mock('axios');
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));

describe('IncomePage Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
    });

    it('renders all form fields', () => {
        render(
            <MemoryRouter>
                <IncomePage />
            </MemoryRouter>
        );

        expect(screen.getByLabelText(/Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Start Year/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Initial Amount/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Annual Change/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/InflationAdjustment/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/User Percentage/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Is SocialSecurity/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Save Changes/i })).toBeInTheDocument();
    });

    it('clamps startYear.value to current year', () => {
        render(
            <MemoryRouter>
                <IncomePage />
            </MemoryRouter>
        );
        const input = screen.getByPlaceholderText(/Current Year/i);
        fireEvent.change(input, { target: { value: '2000' } });
        const currentYear = new Date().getFullYear();
        expect(input.value).toBe(String(currentYear));
    });

    it('alerts if uniform upper ≤ lower for Start Year', () => {
        window.alert = jest.fn();
        render(
            <MemoryRouter>
                <IncomePage />
            </MemoryRouter>
        );
        const [startDist] = screen.getAllByLabelText(/Distribution Type/i);
        fireEvent.change(startDist, { target: { value: 'UNIFORM' } });
        fireEvent.change(screen.getByPlaceholderText('Lower'), { target: { value: '2025' } });
        fireEvent.change(screen.getByPlaceholderText('Upper'), { target: { value: '2024' } });
        const form = document.querySelector('form');
        fireEvent.submit(form);
        expect(window.alert).toHaveBeenCalledWith(
            'Upper Value has to be greater than lower value for Start Year'
        );
    });
});
