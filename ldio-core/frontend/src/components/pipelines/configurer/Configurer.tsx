import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  TextField,
} from "@mui/material";
import { type AlertColor } from '@mui/material/Alert';

import {
  ComponentCard,
  type EtlComponentConfig,
  type AvailableComponent,
} from "./ComponentCard";
import { ComponentGroup } from "./ComponentGroup";
import { SubmitFeedback } from "./SubmitFeedback";
import axios from "axios";

type PipelineConfig = {
  name: string;
  input: {
    name: string;
    config: Record<string, string>;
    adapter?: EtlComponentConfig;
  };
  transformers: EtlComponentConfig[];
  outputs: EtlComponentConfig[];
};

export const Configurer: React.FC = () => {
  const [pipelineName, setPipelineName] = useState("");
  const [input, setInput] = useState<PipelineConfig["input"]>({
    name: "",
    config: {},
  });
  const [showAdapter, setShowAdapter] = useState(false);
  const [transformers, setTransformers] = useState<EtlComponentConfig[]>([]);
  const [outputs, setOutputs] = useState<EtlComponentConfig[]>([
    { name: "", config: {} },
  ]);

  const [availableInputs, setAvailableInputs] = useState<AvailableComponent[]>([]);
  const [availableAdapters, setAvailableAdapters] = useState<AvailableComponent[]>([]);
  const [availableTransformers, setAvailableTransformers] = useState<AvailableComponent[]>([]);
  const [availableOutputs, setAvailableOutputs] = useState<AvailableComponent[]>([]);

  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState<AlertColor>("success");

  const [validationErrors, setValidationErrors] = useState<Record<string, string>[]>([]);

  const payload: PipelineConfig = {
    name: pipelineName,
    input: showAdapter ? { ...input, adapter: input.adapter } : input,
    transformers,
    outputs,
  };

  useEffect(() => {
    axios
      .get(`/api/config`)
      .then((response) => {
        setAvailableInputs(response.data.inputs);
        setAvailableAdapters(response.data.adapters);
        setAvailableTransformers(response.data.transformers);
        setAvailableOutputs(response.data.outputs);
      })
      .catch((err) => {
        console.error("Failed to fetch config:", err);
      });
  }, []);

  const handleSubmit = () => {
    setValidationErrors([]);

    const payload: PipelineConfig = {
      name: pipelineName,
      input: showAdapter ? { ...input, adapter: input.adapter } : input,
      transformers,
      outputs,
    };

    axios.post(`/api`, payload)
      .then(() => {
        setSnackbarMessage(`Pipeline "${pipelineName}" successfully created`);
        setSnackbarSeverity("success");
        setSnackbarOpen(true);
      })
      .catch((err) => {
        const rawReport = err.response?.data?.message;

        try {
          const parsedReport = typeof rawReport === "string" ? JSON.parse(rawReport) : rawReport;

          if (Array.isArray(parsedReport["Validation failed"])) {
            setValidationErrors(parsedReport["Validation failed"]);
            setSnackbarOpen(false); // Don't show snackbar for validation errors
          } else {
            setValidationErrors([{ general: "Invalid validation report format" }]);
          }
        } catch (parseError) {
          setValidationErrors([{ general: "Failed to parse validation report" }]);
        }
      });
  };


  return (
    <Box sx={{ width: '80%', mx: 'auto' }}>
      <Card sx={{ mx: "auto", boxShadow: 3 }}>
        <CardContent>
          <Typography variant="h4" gutterBottom>
            Pipeline Configuration
          </Typography>

          <Box sx={{ display: "flex", flexDirection: "column", gap: 4 }}>
            <TextField
              label="Pipeline Name"
              value={pipelineName}
              onChange={(e) => setPipelineName(e.target.value)}
              fullWidth
            />

            <ComponentCard
              title="Input Component"
              component={input}
              onChange={(updated) => setInput(updated)}
              availableComponents={availableInputs}
            />

            <Button
              variant="outlined"
              color="secondary"
              onClick={() => {
                if (showAdapter) {
                  const { adapter, ...rest } = input;
                  setInput(rest);
                } else {
                  setInput({ ...input, adapter: { name: "", config: {} } });
                }
                setShowAdapter(!showAdapter);
              }}
            >
              {showAdapter ? "Remove Adapter" : "Add Adapter"}
            </Button>

            {showAdapter && input.adapter && (
              <ComponentCard
                title="Adapter"
                component={input.adapter}
                onChange={(updated) => setInput({ ...input, adapter: updated })}
                availableComponents={availableAdapters}
              />
            )}

            <ComponentGroup title="Transformer" components={transformers} setComponents={setTransformers} available={availableTransformers} />
            <ComponentGroup title="Output" components={outputs} setComponents={setOutputs} available={availableOutputs} />

            <Box
              sx={{
                maxHeight: 200,
                overflowY: "auto",
                backgroundColor: "#f5f5f5",
                border: "1px solid #ddd",
                borderRadius: 2,
                padding: 2,
                fontSize: "0.85rem",
                fontFamily: "monospace",
                whiteSpace: "pre-wrap",
                wordBreak: "break-word",
              }}
            >
              {JSON.stringify(payload, null, 2)}
            </Box>

            <Button
              variant="contained"
              color="success"
              onClick={handleSubmit}
              sx={{ mt: 4 }}
            >
              Submit Pipeline
            </Button>
          </Box>
        </CardContent>
      </Card>
      <SubmitFeedback
        open={snackbarOpen}
        message={snackbarMessage}
        severity={snackbarSeverity}
        validationErrors={validationErrors}
        onClose={() => {
          setSnackbarOpen(false);
          setValidationErrors([]);
        }}
      />

    </Box>
  );
};
