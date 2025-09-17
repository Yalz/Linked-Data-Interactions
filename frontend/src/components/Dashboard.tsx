import { Button, Box, TextField, Typography } from "@mui/material";

export const Dashboard = () => {
  return (
    <Box className="w-full h-full">
      <Typography variant="h4" gutterBottom>
        MUI Probe
      </Typography>
      <TextField label="Email" fullWidth margin="normal" />
      <Button variant="contained" color="primary">
        Submit
      </Button>
    </Box>
  );
};
