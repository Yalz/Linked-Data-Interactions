import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Box,
  Typography,
  Select,
  MenuItem,
  TextField,
  Button,
  InputLabel,
  FormControl,
  Snackbar,
  Alert,
} from "@mui/material";
import { API_BASE } from "../../api.config";

const contentTypes = [
  "text/turtle",
  "application/n-quads",
  "application/ld+json",
  "application/json",
  "application/xml",
];

export const HttpOutput: React.FC = () => {
  const [pipelines, setPipelines] = useState<string[]>([]);
  const [selectedPipeline, setSelectedPipeline] = useState("");
  const [contentType, setContentType] = useState(contentTypes[0]);
  const [payload, setPayload] = useState("");
  const [snackbar, setSnackbar] = useState<{ open: boolean; message: string; severity: "success" | "error" }>({
    open: false,
    message: "",
    severity: "success",
  });

  useEffect(() => {
    axios
      .get(`${API_BASE}`)
      .then((res) => {
        const allPipelines = Object.values(res.data);
        const filtered = allPipelines
          .filter((p: any) => p.input?.name === "Ldio:HttpIn")
          .map((p: any) => p.name);
        setPipelines(filtered);
      })
      .catch((err) => {
        console.error("Failed to fetch pipelines:", err);
        setSnackbar({
          open: true,
          message: "Failed to load pipelines.",
          severity: "error",
        });
      });
  }, []);

  const handleSubmit = async () => {
    if (!selectedPipeline || !payload) return;

    try {
      await axios.post(
        `${API_BASE}/pipeline/${selectedPipeline}`,
        payload,
        {
          headers: {
            "Content-Type": contentType,
          },
        }
      );
      setSnackbar({
        open: true,
        message: "Payload sent successfully!",
        severity: "success",
      });
    } catch (err) {
      console.error("Failed to send payload:", err);
      setSnackbar({
        open: true,
        message: "Failed to send payload.",
        severity: "error",
      });
    }
  };

  return (
    <Box sx={{ width: "90%", mx: "auto", py: 4 }}>
      <Typography variant="h4" gutterBottom>
        HTTP Output Sender
      </Typography>

      <FormControl fullWidth sx={{ mb: 3 }}>
        <InputLabel>Target Pipeline</InputLabel>
        <Select
          value={selectedPipeline}
          label="Target Pipeline"
          onChange={(e) => setSelectedPipeline(e.target.value)}
        >
          {pipelines.map((name) => (
            <MenuItem key={name} value={name}>
              {name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <TextField
        label="Payload"
        multiline
        rows={10}
        fullWidth
        variant="outlined"
        value={payload}
        onChange={(e) => setPayload(e.target.value)}
        sx={{ mb: 3 }}
      />

      <FormControl fullWidth sx={{ mb: 3 }}>
        <InputLabel>Content Type</InputLabel>
        <Select
          value={contentType}
          label="Content Type"
          onChange={(e) => setContentType(e.target.value)}
        >
          {contentTypes.map((type) => (
            <MenuItem key={type} value={type}>
              {type}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <Button
        variant="contained"
        color="primary"
        onClick={handleSubmit}
        disabled={!selectedPipeline || !payload}
      >
        Submit to Pipeline
      </Button>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ ...snackbar, open: false })}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setSnackbar({ ...snackbar, open: false })}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  );
};
