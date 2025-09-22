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
import SentimentSatisfiedAltIcon from '@mui/icons-material/SentimentSatisfiedAlt'; // Fun icon

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
        const res = await axios.get(`/api/sink/messages`);
        setSinkData(res.data);
      } catch (err) {
        console.error("Failed to fetch sink messages:", err);
      }
    };

    fetchMessages();
    const interval = setInterval(fetchMessages, 3000);
    return () => clearInterval(interval);
  }, []);

  const isEmpty = Object.keys(sinkData).length === 0;

  return (
    <Box sx={{ width: '90%', mx: 'auto', py: 4 }}>
      <Typography variant="h4" gutterBottom>
        HTTP Sink Message Dashboard
      </Typography>

      {isEmpty ? (
        <Box
          sx={{
            textAlign: 'center',
            mt: 6,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            animation: 'fadeIn 1s ease-in-out',
            '@keyframes fadeIn': {
              from: { opacity: 0 },
              to: { opacity: 1 },
            },
          }}
        >
          <img
            src="https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExejM3anZueDh1ZmN5MGhqeHgwaG14OW1mMjUwbzhvaTMzZ2FhMXIzbyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/xTka034bGJ8H7wH1io/giphy.gif"
            alt="No messages yet"
            style={{
              width: '60%',
              marginBottom: '1rem',
              borderRadius: '12px',
              boxShadow: '0 4px 20px rgba(0,0,0,0.2)',
            }}
          />
          <Typography variant="h5" gutterBottom>
            All quiet on the sink front...
          </Typography>
          <Typography variant="body1" sx={{ maxWidth: 500 }}>
            No messages have arrived yet — but the moment they do, this dashboard will light up like Times Square!
          </Typography>
        </Box>
      ) : (
        Object.entries(sinkData).map(([folder, messages]) => (
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
                            {msg.fields?.data ?? "—"}
                          </pre>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </Paper>
            </CardContent>
          </Card>
        ))
      )}
    </Box>
  );
};
