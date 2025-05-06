import * as ReactRouterDom from 'react-router-dom';

export const useNavigate = jest.fn();

// 나머지 모듈은 실제 react-router-dom의 기능을 그대로 유지
export const {
    BrowserRouter,
    Routes,
    Route,
    Link,
    MemoryRouter,
    useLocation,
    useParams,
    Navigate,
    Outlet,
    useMatch,
    useResolvedPath,
    createBrowserRouter,
    RouterProvider
} = ReactRouterDom;
