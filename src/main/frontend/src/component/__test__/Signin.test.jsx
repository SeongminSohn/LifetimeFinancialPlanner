import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import Signin from '../../pages/logInpage.jsx';
import { jest } from '@jest/globals';

const mock = new MockAdapter(axios);

function DummyHomepage() {
    return <div>Homepage</div>;
}

describe('Signin Component', () => {
    afterEach(() => {
        mock.reset();
        localStorage.clear();
        jest.clearAllMocks();
    });

    test('If Log in is well processed, then go to Homepage', async () => {
        mock.onPost("http://localhost:10000/api/users/login").reply(200, { id: "fake-token-123" });

        render(
            <MemoryRouter initialEntries={["/"]}>
                <Routes>
                    <Route path="/" element={<Signin />} />
                    <Route path="/Homepage" element={<DummyHomepage />} />
                </Routes>
            </MemoryRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/id/i), { target: { value: 'test@example.com' } });
        fireEvent.change(screen.getByPlaceholderText(/password/i), { target: { value: 'password123' } });

        fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

        await waitFor(() => {
            expect(mock.history.post[0].url).toBe("http://localhost:10000/api/users/login");
            expect(localStorage.getItem("token")).toBe("fake-token-123");
            expect(screen.getByText("Homepage")).toBeInTheDocument();
        });
    });

    test('If log in is failed, then return', async () => {
        mock.onPost("http://localhost:10000/api/users/login").reply(401, { message: "Invalid credentials" });

        window.alert = jest.fn();

        render(
            <MemoryRouter initialEntries={["/"]}>
                <Routes>
                    <Route path="/" element={<Signin />} />
                    <Route path="/Homepage" element={<DummyHomepage />} />
                </Routes>
            </MemoryRouter>
        );

        fireEvent.change(screen.getByPlaceholderText(/id/i), { target: { value: 'wrong@example.com' } });
        fireEvent.change(screen.getByPlaceholderText(/password/i), { target: { value: 'wrongpassword' } });

        fireEvent.click(screen.getByRole('button', { name: /sign in/i }));

        await waitFor(() => {
            expect(mock.history.post[0].url).toBe("http://localhost:10000/api/users/login");
            expect(window.alert).toHaveBeenCalledWith("Fail to log in. Please check your email or password");
            expect(localStorage.getItem("token")).toBeNull();
            expect(screen.queryByText("Homepage")).not.toBeInTheDocument();
        });
    });
});
