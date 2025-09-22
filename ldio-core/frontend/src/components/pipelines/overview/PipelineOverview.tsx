import React, { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Typography, Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle
} from "@mui/material";
import axios from "axios";

type EtlComponentConfig = {
  name: string;
  config?: Record<string, string>;
};

type PipelineConfig = {
  name: string;
  input: {
    name: string;
    config?: Record<string, string>;
    adapter?: EtlComponentConfig;
  };
  transformers?: EtlComponentConfig[];
  outputs: EtlComponentConfig[];
};

const renderConfig = (config?: Record<string, string>) => {
  if (!config || Object.keys(config).length === 0) return null;

  return (
    <Box sx={{ pl: 2 }}>
      {Object.entries(config).map(([key, value]) => (
        <Typography key={key} variant="caption" sx={{ display: "block" }}>
          • <strong>{key}</strong>: {value}
        </Typography>
      ))}
    </Box>
  );
};

export const PipelineOverview: React.FC = () => {
  const [pipelines, setPipelines] = useState<Record<string, PipelineConfig>>({});
  const [deleteTarget, setDeleteTarget] = useState<string | null>(null);

  useEffect(() => {
    axios.get(`/api`)
      .then((res) => setPipelines(res.data))
      .catch((err) => console.error("Failed to fetch pipelines:", err));
  }, []);

  const onDeletePipeline = () => {
    axios.delete(`/api/${deleteTarget}`)
      .then(() => {
        setPipelines(prev => {
          const updated = { ...prev };
          delete updated[deleteTarget!];
          return updated;
        });
      })
      .catch(err => console.error("Failed to delete pipeline:", err))
      .finally(() => setDeleteTarget(null));
  }

  return (
    <Box sx={{ mx: "auto", width: "80%" }}>
      <TableContainer component={Paper}>
        <Typography variant="h5" sx={{ p: 2 }}>Active Pipelines</Typography>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell><strong>Name</strong></TableCell>
              <TableCell><strong>Input</strong></TableCell>
              <TableCell><strong>Adapter</strong></TableCell>
              <TableCell><strong>Transformers</strong></TableCell>
              <TableCell><strong>Outputs</strong></TableCell>
              <TableCell align="center"><strong>Actions</strong></TableCell>
            </TableRow>
          </TableHead>

          <TableBody>
            {Object.keys(pipelines).length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center">
                  <Box sx={{ textAlign: "center" }}>
                    <Typography variant="h6" gutterBottom>
                      No active pipelines found
                    </Typography>
                    <Typography variant="body2">
                      Your data highways are still waited to be constructed. Once a pipeline is created, it’ll show up here!
                    </Typography>
                  </Box>
                </TableCell>
              </TableRow>
            ) : (
              Object.entries(pipelines).map(([key, pipeline]) => (
                <TableRow key={key}>
                  <TableCell>{pipeline.name}</TableCell>

                  <TableCell>
                    <Typography variant="body2">{pipeline.input.name}</Typography>
                    {renderConfig(pipeline.input.config)}
                  </TableCell>

                  <TableCell>
                    {pipeline.input.adapter ? (
                      <>
                        <Typography variant="body2">{pipeline.input.adapter.name}</Typography>
                        {renderConfig(pipeline.input.adapter.config)}
                      </>
                    ) : "—"}
                  </TableCell>

                  <TableCell>
                    {pipeline.transformers && pipeline.transformers.length > 0 ? (
                      pipeline.transformers.map((t, idx) => (
                        <Box key={idx} sx={{ mb: 1 }}>
                          <Typography variant="body2">{t.name}</Typography>
                          {renderConfig(t.config)}
                        </Box>
                      ))
                    ) : "—"}
                  </TableCell>

                  <TableCell>
                    {pipeline.outputs.map((o, idx) => (
                      <Box key={idx} sx={{ mb: 1 }}>
                        <Typography variant="body2">{o.name}</Typography>
                        {renderConfig(o.config)}
                      </Box>
                    ))}
                  </TableCell>
                  <TableCell align="center">
                    <Button
                      variant="outlined"
                      color="error"
                      size="small"
                      onClick={() => setDeleteTarget(pipeline.name)}
                    >
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              )))}
          </TableBody>
        </Table>
      </TableContainer>
      <Dialog open={!!deleteTarget} onClose={() => setDeleteTarget(null)}>
        <DialogTitle>Confirm Deletion</DialogTitle>
        <DialogContent>
          Are you sure you want to delete pipeline <strong>{deleteTarget}</strong>?
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteTarget(null)}>Cancel</Button>
          <Button
            onClick={onDeletePipeline}
            color="error"
            variant="contained"
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};
