import { Outlet, Route, Routes } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Dashboard } from './Dashboard';
import { LdioComponentList } from './ldio-component/LdioComponentList';
import { Box } from '@mui/material';
import { Configurer } from './configurer/Configurer';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Dashboard />} />
        <Route path="catalog" element={<LdioComponentList />} />
        <Route path="configure" element={<Configurer />} />
        <Route path="*" element={<div>404 - Not Found</div>} />
      </Route>
    </Routes>
  );
}

export const Layout = () => (
  <Box
    sx={{
      display: 'flex',
      justifyContent: 'center',
      height: '100vh',
      width: '100vw',
    }}
  >
    {/* Outer container with max width and outer margin */}
    <Box
      sx={{
        display: 'flex',
        width: '100%',
        maxWidth: 1400,       // total layout width
        paddingX: 4,           // outer margin (left/right)
        gap: 2,                // mini-margin between sidebar and content
      }}
    >
      {/* Sidebar: 20% of layout width */}
      <Box
        sx={{ width: '20%'}}
      >
        <Sidebar />
      </Box>

      {/* Content: 80% of layout width */}
      <Box
        sx={{
          width: '80%',
          padding: 4,
          overflow: 'auto',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
        }}
      >
        <Outlet />
      </Box>
    </Box>
  </Box>
);



export default App;
