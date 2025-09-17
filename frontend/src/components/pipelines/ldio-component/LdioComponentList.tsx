import { useEffect, useState } from "react";
import axios from "axios";
import {
  Box,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";
import { LdioComponent } from "./LdioComponent";

export const LdioComponentList: React.FC = () => {
  const [inputs, setInputs] = useState<any[]>([]);
  const [adapters, setAdapters] = useState<any[]>([]);
  const [transformers, setTransformers] = useState<any[]>([]);
  const [outputs, setOutputs] = useState<any[]>([]);

  useEffect(() => {
    axios
      .get("http://localhost:8080/config")
      .then((response) => {
        setInputs(response.data.inputs);
        setAdapters(response.data.adapters);
        setTransformers(response.data.transformers);
        setOutputs(response.data.outputs);
      })
      .catch((err) => {
        console.error("Failed to fetch config:", err);
      });
  }, []);

  const renderGroup = (
    title: string,
    components: any[],
    type: "INPUT" | "ADAPTER" | "TRANSFORMER" | "OUTPUT"
  ) => (
    <>
      <TableRow sx={{ backgroundColor: "#f1f5f9" }}>
        <TableCell colSpan={4}>
          <Typography variant="subtitle1" fontWeight="bold" color="text.secondary">
            {title}
          </Typography>
        </TableCell>
      </TableRow>
      {components.length > 0 ? (
        components.map((component) => (
          <LdioComponent key={component.name} component={component} type={type} />
        ))
      ) : (
        <TableRow>
          <TableCell colSpan={4}>
            <Typography variant="body2" color="text.disabled" fontStyle="italic">
              No {title.toLowerCase()} configured
            </Typography>
          </TableCell>
        </TableRow>
      )}
    </>
  );

  return (
    <Box sx={{ mx: "auto", width:"80%" }}>
      <Typography
        variant="h4"
        gutterBottom
        sx={{
          textAlign: "center",
          background: "linear-gradient(to right, #1e293b, #64748b)",
          WebkitBackgroundClip: "text",
          WebkitTextFillColor: "transparent",
          fontWeight: "bold",
        }}
      >
        Available Components
      </Typography>

      <TableContainer component={Paper} sx={{ mt: 4, maxWidth: '100%', overflowX: 'auto' }}>
        <Table size="small" sx={{ tableLayout: 'fixed', width: '100%' }}>
          <TableHead sx={{ backgroundColor: "#f8fafc" }}>
            <TableRow>
              <TableCell sx={{ width: '15%' }}>Component</TableCell>
              <TableCell sx={{ width: '10%' }}>Type</TableCell>
              <TableCell sx={{ width: '30%' }}>Description</TableCell>
              <TableCell sx={{ width: '45%' }}>Properties</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {renderGroup("Inputs", inputs, "INPUT")}
            {renderGroup("Adapters", adapters, "ADAPTER")}
            {renderGroup("Transformers", transformers, "TRANSFORMER")}
            {renderGroup("Outputs", outputs, "OUTPUT")}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};
