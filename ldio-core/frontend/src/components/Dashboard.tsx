import { Box, Typography, Paper, List, ListItem, ListItemText, Link } from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';

export const Dashboard = () => {
  return (
    <Box className="w-full h-full" display="flex" justifyContent="center" alignItems="center" p={2}>
      <Paper elevation={6} sx={{ padding: 4, maxWidth: "100%" }}>
        <Typography variant="h4" gutterBottom>
          Welcome to the Linked Data Interactions Orchestrator
        </Typography>

        <Typography variant="body1" paragraph>
          The Linked Data Interactions Orchestrator (LDIO) serves as a workbench for any Linked Data tinkerer. <br />
          Whether you're reading, transforming, exporting, or storing Linked Data, LDIO provides a unified platform to manage it all.
        </Typography>

        <Typography variant="h6" gutterBottom>
          Explore LDIO Features
        </Typography>

        <List>
          <ListItem>
            <ListItemText
              primary={
                <Link component={RouterLink} to="/components/catalog" underline="hover">
                  Explore Components
                </Link>
              }
              secondary="Browse available components for building pipelines."
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary={
                <Link component={RouterLink} to="/pipelines/overview" underline="hover">
                  Monitor Pipelines
                </Link>
              }
              secondary="View all active pipelines and their status."
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary={
                <Link component={RouterLink} to="/pipelines/configure" underline="hover">
                  Build Pipelines
                </Link>
              }
              secondary="Design and configure Linked Data pipelines tailored to your needs."
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary={
                <Link component={RouterLink} to="/http-debug/send" underline="hover">
                  Send Data to Pipelines
                </Link>
              }
              secondary="Write directly to pipelines that include a Ldio:HttpIn component."
            />
          </ListItem>
          <ListItem>
            <ListItemText
              primary={
                <Link component={RouterLink} to="/http-debug/sink" underline="hover">
                  Debug Sink Messages
                </Link>
              }
              secondary="Inspect messages sent to pipelines with a Ldio:SinkOut component."
            />
          </ListItem>
          
        </List>

        <Typography variant="body2" mt={2}>
          Use the sidebar to navigate or click the links above to get started.
        </Typography>
      </Paper>
    </Box>
  );
};
