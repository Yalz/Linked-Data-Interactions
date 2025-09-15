import {
  TableCell,
  TableRow,
  Typography,
  Box,
  Chip,
  Table,
  TableHead,
  TableBody,
  Paper,
} from "@mui/material";

interface PropertyListProps {
  component: Component;
  type: "INPUT" | "ADAPTER" | "TRANSFORMER" | "OUTPUT";
}

interface Component {
  name: string;
  class: string;
  description: string;
  properties: any[];
}

export const LdioComponent: React.FC<PropertyListProps> = ({ component, type }) => {
  return (
    <TableRow>
      {/* Name */}
      <TableCell sx={{ width: '15%', verticalAlign: 'top' }}>
        <Box display="flex" flexDirection="column" alignItems="center">
          <Box
            sx={{
              height: 32,
              width: 32,
              borderRadius: "50%",
              backgroundColor: "teal",
              color: "white",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: 12,
              fontWeight: "bold",
            }}
          >
            {component.name.split(":")[0].toUpperCase()}
          </Box>
          <Typography variant="body2" mt={1}>
            {component.name}
          </Typography>
        </Box>
      </TableCell>

      {/* Type */}
      <TableCell sx={{ width: '15%', verticalAlign: 'top' }}>
        <Box textAlign="center">
          <Typography variant="subtitle2">{component.name.split(":")[0]}</Typography>
          <Typography variant="caption" color="text.secondary">
            {type}
          </Typography>
        </Box>
      </TableCell>

      {/* Description */}
      <TableCell sx={{ width: '30%', verticalAlign: 'top' }}>
        <Typography variant="body2" color="text.secondary">
          {component.description}
        </Typography>
      </TableCell>

      {/* Properties */}
      <TableCell sx={{ width: '40%', verticalAlign: 'top' }}>
        <Box maxHeight={256} overflow="auto">
          <Table size="small" component={Paper}>
            <TableHead>
              <TableRow>
                <TableCell>Key</TableCell>
                <TableCell>Type</TableCell>
                <TableCell>Default</TableCell>
                <TableCell>Required</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {component.properties?.length ? (
                component.properties.map((prop, index) => (
                  <TableRow key={index}>
                    <TableCell sx={{ wordBreak: 'break-word', whiteSpace: 'normal' }}>{prop.key}</TableCell>
                    <TableCell sx={{ wordBreak: 'break-word', whiteSpace: 'normal' }}>{prop.expectedType}</TableCell>
                    <TableCell sx={{ wordBreak: 'break-word', whiteSpace: 'normal' }}>{prop.defaultValue}</TableCell>
                    <TableCell>
                      <Chip
                        label={prop.required ? "Required" : "Optional"}
                        size="small"
                        color={prop.required ? "error" : "warning"}
                        variant="outlined"
                      />
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={4}>
                    <Typography variant="body2" color="text.disabled" fontStyle="italic">
                      No properties defined
                    </Typography>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </Box>
      </TableCell>
    </TableRow>
  );
};
