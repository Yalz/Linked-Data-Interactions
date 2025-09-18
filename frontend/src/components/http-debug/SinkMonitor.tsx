import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Box,
  Typography,
  Card,
  CardContent,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Paper,
} from "@mui/material";

type SinkMessage = {
  id: string;
  timestamp?: string;
  [key: string]: any;
};

type SinkData = Record<string, SinkMessage[]>;

export const SinkMonitor: React.FC = () => {
  const [sinkData, setSinkData] = useState<SinkData>({});

  useEffect(() => {
    const fetchMessages = async () => {
      try {
        const res = await axios.get("http://localhost:8080/sink/messages");
        setSinkData(res.data);
      } catch (err) {
        console.error("Failed to fetch sink messages:", err);
      }
    };

    fetchMessages();
    const interval = setInterval(fetchMessages, 3000);
    return () => clearInterval(interval);
  }, []);

  return (
    <Box sx={{ width: '90%', mx: 'auto', py: 4 }}>
      <Typography variant="h4" gutterBottom>
        HTTP Sink Message Dashboard
      </Typography>

      {Object.entries(sinkData).map(([folder, messages]) => (
        <Card key={folder} sx={{ mb: 4 }}>
          <CardContent>
            <Typography variant="h6">{folder}</Typography>
            <Divider sx={{ my: 2 }} />
            <Paper sx={{ overflowX: "auto" }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell sx={{ fontWeight: 'bold' }}>Timestamp</TableCell>
                    <TableCell sx={{ fontWeight: 'bold' }}>Data</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {messages.map((msg, idx) => (
                    <TableRow key={idx}>
                      <TableCell sx={{ whiteSpace: 'nowrap' }}>
                        {msg.timestamp ?? "—"}
                      </TableCell>
                      <TableCell>
                        <pre style={{ margin: 0, fontSize: "0.8rem" }}>
                          {msg.fields.data}
                        </pre>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </Paper>
          </CardContent>
        </Card>
      ))}
    </Box>
  );
};
