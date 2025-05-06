import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import SimulationPage from '../../pages/SimulationPage';
import { MemoryRouter } from 'react-router-dom';

// Mock the StackedBarChart component from src/component
jest.mock('../StackedBarChart.jsx', () => () => <div>MockChart</div>);

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));
jest.mock('axios');

describe('SimulationPage Component', () => {
    const simulations = [
        { id: 1, batchId: 1, year: 2025 },
        { id: 2, batchId: 1, year: 2026 },
    ];

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem('token', 'token');
        localStorage.setItem('scenario', '42');

        axios.get.mockResolvedValueOnce({ data: simulations });
    });

    it('shows loading state then batch buttons', async () => {
        render(
            <MemoryRouter>
                <SimulationPage />
            </MemoryRouter>
        );

        // Initial loading
        expect(screen.getByText('Loading simulations...')).toBeInTheDocument();

        // Wait for data and batch button
        const batchBtn = await screen.findByRole('button', { name: /Simulation 1 Count:2/ });
        expect(batchBtn).toBeInTheDocument();
    });

    it('toggles chart display on batch button click', async () => {
        render(
            <MemoryRouter>
                <SimulationPage />
            </MemoryRouter>
        );

        const batchBtn = await screen.findByRole('button', { name: /Simulation 1 Count:2/ });
        fireEvent.click(batchBtn);

        // Should show fallback then mock chart
        expect(screen.getByText('Loading chart...')).toBeInTheDocument();
        await waitFor(() => expect(screen.getByText('MockChart')).toBeInTheDocument());

        // Hide chart
        fireEvent.click(screen.getByText('Hide chart'));
        expect(screen.queryByText('MockChart')).not.toBeInTheDocument();
    });
});
