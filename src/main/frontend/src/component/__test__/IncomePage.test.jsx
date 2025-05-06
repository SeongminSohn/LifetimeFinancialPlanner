import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
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
        const { container } = render(
            <MemoryRouter>
                <IncomePage />
            </MemoryRouter>
        );
        const [startDist] = screen.getAllByLabelText(/Distribution Type/i);
        fireEvent.change(startDist, { target: { value: 'UNIFORM' } });
        fireEvent.change(screen.getByPlaceholderText('Lower'), { target: { value: '2025' } });
        fireEvent.change(screen.getByPlaceholderText('Upper'), { target: { value: '2024' } });
        const form = container.querySelector('form');
        fireEvent.submit(form);
        expect(window.alert).toHaveBeenCalledWith(
            'Upper Value has to be greater than lower value for Start Year'
        );
    });

    it('submits form and navigates on success', async () => {
        localStorage.setItem('scenario', '2');
        axios.post.mockResolvedValue({ data: { scenarioId: '2' } });
        const { container } = render(
            <MemoryRouter>
                <IncomePage />
            </MemoryRouter>
        );
        fireEvent.change(screen.getByLabelText(/Name/i), { target: { value: 'Test Income' } });
        fireEvent.change(screen.getByPlaceholderText(/Current Year/i), { target: { value: '2030' } });
        fireEvent.change(screen.getByLabelText(/Initial Amount/i), { target: { value: '1000' } });
        // Fill duration.value
        fireEvent.change(screen.getByPlaceholderText('value'), { target: { value: '10' } });
        // Fill annualChange.value
        const inputs = screen.getAllByPlaceholderText('value');
        fireEvent.change(inputs[1], { target: { value: '100' } });
        fireEvent.change(screen.getByLabelText(/User Percentage/i), { target: { value: '0.5' } });
        const form = container.querySelector('form');
        fireEvent.submit(form);
        await waitFor(() => {
            expect(axios.post).toHaveBeenCalledWith(
                'http://localhost:10000/api/income-events',
                expect.objectContaining({ name: 'Test Income', initialAmount: '1000' }),
                expect.any(Object)
            );
            expect(mockNavigate).toHaveBeenCalledWith('/IncomeSetting');
        });
    });
});
