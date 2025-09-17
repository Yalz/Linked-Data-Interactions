import React from "react";
import {
    Box,
    Paper,
    Typography,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    Divider,
    Chip,
    IconButton,
} from "@mui/material";
import {
    ExpandMore as ExpandMoreIcon,
    BarChart as BarChartIcon,
    ShoppingBag as ShoppingBagIcon,
    Inbox as InboxIcon,
    Upcoming as UpcomingIcon,
    MenuBook as MenuBookIcon,
    DesignServices as DesignServicesIcon,
    AccountCircle as AccountCircleIcon,
    Settings as SettingsIcon,
    PowerSettingsNew as PowerIcon,
    ChevronRight as ChevronRightIcon,
    Send as SendIcon
} from "@mui/icons-material";

import GitHubIcon from '@mui/icons-material/GitHub';
import { Link } from "react-router-dom";

export function Sidebar() {
    const [open, setOpen] = React.useState<number | false>(false);

    const handleOpen = (panel: number) => {
        setOpen(open === panel ? false : panel);
    };

    return (
        <Paper
            elevation={4}
            sx={{
                height: "calc(100vh - 2rem)",
                width: "100%",
                maxWidth: "20rem",
                p: 2,
                boxShadow: "0 4px 20px rgba(0,0,0,0.1)",
            }}
        >
            <Box display="flex" alignItems="center" gap={2} mb={2}>
                <img src="/ldio.png" alt="brand" style={{ height: 32, width: 32 }} />
                <Typography variant="h6" component={Link} to="/" sx={{ textDecoration: "none", color: "inherit" }}>
                    LDIO
                </Typography>
            </Box>

            <List>
                {/* Pipelines Accordion */}
                <Accordion expanded={open === 1} onChange={() => handleOpen(1)}>
                    <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                        <ListItemIcon>
                            <SendIcon />
                        </ListItemIcon>
                        <Typography>Pipelines</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <List disablePadding>
                            <ListItem component={Link} to="pipelines/overview">
                                <ListItemIcon>
                                    <MenuBookIcon />
                                </ListItemIcon>
                                <ListItemText primary="Overview" />
                            </ListItem>
                            <ListItem component={Link} to="pipelines/configure">
                                <ListItemIcon>
                                    <DesignServicesIcon />
                                </ListItemIcon>
                                <ListItemText primary="Configure a new pipeline" />
                            </ListItem>
                        </List>
                    </AccordionDetails>
                </Accordion>

                {/* Static Links */}
                <ListItem component={Link} to="catalog">
                    <ListItemIcon>
                        <ShoppingBagIcon />
                    </ListItemIcon>
                    <ListItemText primary="Catalog" />
                </ListItem>

                <Divider sx={{ my: 2 }} />

                <ListItem component={Link} to="http-sink">
                    <ListItemIcon>
                        <UpcomingIcon />
                    </ListItemIcon>
                    <ListItemText primary="Http Sink" />
                </ListItem>

                <Divider sx={{ my: 2 }} />

                <ListItem
                    component="a"
                    href="https://github.com/Yalz/Linked-Data-Interactions"
                    target="_blank"
                    rel="noopener noreferrer">
                    <ListItemIcon>
                        <GitHubIcon />
                    </ListItemIcon>
                    <ListItemText primary="Contribute/Report Issue" />
                </ListItem>
                <ListItem>
                    <ListItemIcon>
                        <AccountCircleIcon />
                    </ListItemIcon>
                    <ListItemText primary="Profile" />
                </ListItem>
                <ListItem>
                    <ListItemIcon>
                        <SettingsIcon />
                    </ListItemIcon>
                    <ListItemText primary="Settings" />
                </ListItem>
                <ListItem>
                    <ListItemIcon>
                        <PowerIcon />
                    </ListItemIcon>
                    <ListItemText primary="Log Out" />
                </ListItem>
            </List>
        </Paper>
    );
}
