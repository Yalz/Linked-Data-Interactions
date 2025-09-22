import {
  Box,
  Typography,
  TextField,
  Card,
  CardContent,
  Select,
  MenuItem,
  InputLabel,
  FormControl,
  Chip,
} from "@mui/material";

export type EtlComponentConfig = {
  name: string;
  config: Record<string, string>;
};

export type AvailableComponent = {
  name: string;
  description: string;
  class: string;
  properties: {
    key: string;
    expectedType: string;
    defaultValue?: string;
    required: boolean;
  }[];
};

type Props = {
  title: string;
  component: EtlComponentConfig;
  onChange: (updated: EtlComponentConfig) => void;
  availableComponents: AvailableComponent[];
};

export const ComponentCard: React.FC<Props> = ({
  title,
  component,
  onChange,
  availableComponents,
}) => {
  const handleNameChange = (name: string) => {
    const selected = availableComponents.find((c) => c.name === name);
    const config: Record<string, string> = {};

    if (selected?.properties != null) {
      selected?.properties.forEach((prop) => {
        config[prop.key] = prop.defaultValue ?? "";
      });
    }

    onChange({ name, config });
  };

  const handleConfigValueChange = (key: string, value: string) => {
    onChange({ ...component, config: { ...component.config, [key]: value } });
  };

  const selectedMeta = availableComponents.find((c) => c.name === component.name);

  return (
    <Card sx={{boxShadow: 2 }}>
      <CardContent>
        <Typography variant="h6" gutterBottom>
          {title}
        </Typography>

        <FormControl fullWidth sx={{ mt: 2 }}>
          <InputLabel>Component Name</InputLabel>
          <Select
            value={component.name}
            label="Component Name"
            onChange={(e) => handleNameChange(e.target.value)}
          >
            {availableComponents.map((comp) => (
              <MenuItem key={comp.name} value={comp.name}>
                {comp.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        {selectedMeta?.properties?.map((prop) => (
          <Box key={prop.key} sx={{ mt: 3 }}>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
              <Chip
                label={prop.required ? "Required" : "Optional"}
                size="small"
                color={prop.required ? "error" : "warning"}
              />
              <Typography variant="body2" color="text.secondary">
                {prop.expectedType}
              </Typography>
            </Box>

            <TextField
              label={prop.key}
              value={component.config[prop.key] ?? ""}
              onChange={(e) => handleConfigValueChange(prop.key, e.target.value)}
              fullWidth
            />
          </Box>
        ))}
      </CardContent>
    </Card>
  );
};
