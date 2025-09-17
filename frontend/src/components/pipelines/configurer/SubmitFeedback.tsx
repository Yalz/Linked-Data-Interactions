import React from "react";
import { Snackbar, Alert, Box, Typography } from "@mui/material";
import { type AlertColor } from "@mui/material";

type Props = {
  open: boolean;
  message?: string;
  severity?: AlertColor;
  validationErrors?: Record<string, string>[];
  onClose: () => void;
};

export const SubmitFeedback: React.FC<Props> = ({ open, message, severity, validationErrors = [], onClose }) => {
  const isValidationMode = validationErrors.length > 0;

  return (
    <>
      {isValidationMode ? (
        <Box
          sx={{
            mt: 4,
            p: 2,
            border: "1px solid #f44336",
            borderRadius: 2,
            backgroundColor: "#ffebee",
          }}
        >
          <Typography variant="h6" color="error" gutterBottom>
            Validation Errors
          </Typography>
          {validationErrors.map((errorObj, idx) => {
            const [field, message] = Object.entries(errorObj)[0];
            return (
              <Typography key={idx} variant="body2" color="error" sx={{ mb: 0.5 }}>
                {field}: {message}
              </Typography>
            );
          })}
        </Box>
      ) : (
        <Snackbar
          open={open}
          autoHideDuration={4000}
          onClose={onClose}
          anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        >
          <Alert elevation={6} variant="filled" severity={severity} onClose={onClose} sx={{ width: "100%" }}>
            {message}
          </Alert>
        </Snackbar>
      )}
    </>
  );
};
