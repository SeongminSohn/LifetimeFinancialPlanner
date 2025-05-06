import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import axios from 'axios';
import { MemoryRouter } from 'react-router-dom';

// Mock lazy-loaded chart component
jest.mock('../../component/StackedBarChart.jsx', () => () => <div>MockChart</div>);

// Mock useNavigate hook from react-router-dom
const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockNavigate,
}));

// Mock axios requests
jest.mock('axios');

// Import SimulationPage component under test
import SimulationPage from '../../pages/SimulationPage';

describe('SimulationPage Component', () => {
    // Mock simulation data returned from axios
    const mockSimulations = [
        { id: 1, batchId: 1, year: 2025 },
        { id: 2, batchId: 1, year: 2026 },
    ];

    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        localStorage.setItem('token', 'test-token');
        localStorage.setItem('scenario', '42');
    });

    test('displays loading state and then renders batch buttons after API response', async () => {
        // Set axios mock to resolve after a slight delay
        axios.get.mockImplementation(() =>
            new Promise(resolve => setTimeout(() => resolve({ data: mockSimulations }), 200))
        );

        render(
            <MemoryRouter>
                <SimulationPage />
            </MemoryRouter>
        );

        // Fill input field for simulation count
        fireEvent.change(
            screen.getByPlaceholderText(/Please specify the number of simulation runs./i),
            { target: { value: '2' } }
        );

        // Click submit button to trigger API request
        fireEvent.click(screen.getByRole('button', { name: 'Submit' }));

        // Immediately check for loading indicator presence (it should appear instantly after button click)
        expect(await screen.findByText(/Loading simulations\.\.\./i)).toBeInTheDocument();

        // After API resolves, the loading indicator should disappear and batch button should appear
        const batchButton = await screen.findByRole('button', {
            name: /Simulation\s*1\s*Count:\s*2/i,
        });

        expect(batchButton).toBeInTheDocument();
        expect(screen.queryByText(/Loading simulations\.\.\./i)).toBeNull();
    });

    test('renders chart on batch button click and toggles its visibility', async () => {
        axios.get.mockResolvedValue({ data: mockSimulations });

        render(
            <MemoryRouter>
                <SimulationPage />
            </MemoryRouter>
        );

        // Input simulation count
        fireEvent.change(
            screen.getByPlaceholderText(/Please specify the number of simulation runs./i),
            { target: { value: '2' } }
        );

        // Trigger the fetch simulations action
        fireEvent.click(screen.getByRole('button', { name: 'Submit' }));

        // Wait for batch button to appear after axios call
        const batchButton = await screen.findByRole('button', {
            name: /Simulation\s*1\s*Count:\s*2/i,
        });
        fireEvent.click(batchButton);

        // Confirm chart loading indicator appears
        expect(await screen.findByText(/Loading chart\.\.\./i)).toBeInTheDocument();

        // Confirm chart component is rendered after loading completes
        expect(await screen.findByText('MockChart')).toBeInTheDocument();

        // Assuming you have a 'Hide chart' button to toggle the chart visibility
        fireEvent.click(screen.getByRole('button', { name: /Hide chart/i }));

        // Verify chart component is no longer visible
        await waitFor(() => {
            expect(screen.queryByText('MockChart')).toBeNull();
        });
    });

    test('handles sidebar menu toggling and navigation correctly', () => {
        render(
            <MemoryRouter>
                <SimulationPage />
            </MemoryRouter>
        );

        // Toggle sidebar open
        fireEvent.click(screen.getByRole('button', { name: 'Menu' }));
        expect(screen.getByRole('button', { name: 'View Invest type Status' })).toBeInTheDocument();

        // Toggle sidebar closed
        fireEvent.click(screen.getByRole('button', { name: 'Menu' }));
        expect(screen.queryByRole('button', { name: 'View Invest type Status' })).toBeNull();

        // Test navigation with logo button click
        fireEvent.click(screen.getByAltText('Logo'));
        expect(mockNavigate).toHaveBeenCalledWith('/Homepage');

        // Test navigation with User Guide button click
        fireEvent.click(screen.getByRole('button', { name: 'User Guide' }));
        expect(mockNavigate).toHaveBeenCalledWith('/UserGuide');
    });
});
