import React from "react";
import { Box, Typography, Button, IconButton } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { ComponentCard, type EtlComponentConfig, type AvailableComponent } from "./ComponentCard";

type Props = {
  title: string;
  components: EtlComponentConfig[];
  setComponents: React.Dispatch<React.SetStateAction<EtlComponentConfig[]>>;
  available: AvailableComponent[];
};

export const ComponentGroup: React.FC<Props> = ({ title, components, setComponents, available }) => (
  <>
    <Typography variant="h6">{title}</Typography>
    {components.map((comp, idx) => (
      <Box key={idx} sx={{ position: "relative" }}>
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
            position: "absolute",
            top: 8,
            right: 8,
            zIndex: 1,
            backgroundColor: "rgba(255,255,255,0.8)",
            "&:hover": { backgroundColor: "rgba(255,255,255,1)" },
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
