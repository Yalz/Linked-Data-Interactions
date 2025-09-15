import React, { useEffect, useState } from "react";
import {
  Box,
  Typography,
  Button,
  Card,
  CardContent,
  TextField,
  IconButton,
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import {
  ComponentCard,
  type EtlComponentConfig,
  type AvailableComponent,
} from "./ComponentCard";
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

  const payload: PipelineConfig = {
    name: pipelineName,
    input: showAdapter ? { ...input, adapter: input.adapter } : input,
    transformers,
    outputs,
  };

  useEffect(() => {
    axios
      .get("http://localhost:8080/config")
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
    const payload: PipelineConfig = {
      name: pipelineName,
      input: showAdapter ? { ...input, adapter: input.adapter } : input,
      transformers,
      outputs,
    };
    axios.post("http://localhost:8080/", payload)
  };

  const renderComponentGroup = (
    title: string,
    components: EtlComponentConfig[],
    setComponents: React.Dispatch<React.SetStateAction<EtlComponentConfig[]>>,
    available: AvailableComponent[]
  ) => (
    <>
      <Typography variant="h6">{title}</Typography>
      {components.map((comp, idx) => (
        <Box key={idx} sx={{ position: 'relative' }}>
          <ComponentCard
            title={`${title} ${idx + 1}`}
            component={comp}
            onChange={(updated) => {
              const updatedList = [...components];
              updatedList[idx] = updated;
              setComponents(updatedList);
            }}
            availableComponents={available}
          />
          <IconButton
            size="small"
            onClick={() => {
              const updatedList = components.filter((_, i) => i !== idx);
              setComponents(updatedList);
            }}
            sx={{
              position: 'absolute',
              top: 8,
              right: 8,
              zIndex: 1,
              backgroundColor: 'rgba(255,255,255,0.8)',
              '&:hover': { backgroundColor: 'rgba(255,255,255,1)' },
            }}
          >
            <CloseIcon fontSize="small" />
          </IconButton>
        </Box>
      ))}
      <Button
        variant="contained"
        color="primary"
        onClick={() => setComponents([...components, { name: "", config: {} }])}
      >
        Add {title}
      </Button>
    </>
  );

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
                setShowAdapter(!showAdapter);
                if (!input.adapter) {
                  setInput({ ...input, adapter: { name: "", config: {} } });
                }
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

            {renderComponentGroup("Transformer", transformers, setTransformers, availableTransformers)}
            {renderComponentGroup("Output", outputs, setOutputs, availableOutputs)}

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
    </Box>
  );
};
