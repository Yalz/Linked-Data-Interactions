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
} from "@mui/material";
import {
    ExpandMore as ExpandMoreIcon,
    ShoppingBag as ShoppingBagIcon,
    MenuBook as MenuBookIcon,
    DesignServices as DesignServicesIcon,
    AccountCircle as AccountCircleIcon,
    Settings as SettingsIcon,
    PowerSettingsNew as PowerIcon,
    Send as SendIcon,
    SettingsSuggest as SettingsSuggestIcon,
    Http as HttpIcon,
    Outbox,
    MoveToInbox
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
                <Accordion expanded={open === 2} onChange={() => handleOpen(2)}>
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

                <Accordion expanded={open === 1} onChange={() => handleOpen(1)}>
                    <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                        <ListItemIcon>
                            <SettingsSuggestIcon />
                        </ListItemIcon>
                        <Typography>Components</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <List disablePadding>
                            <ListItem component={Link} to="components/catalog">
                                <ListItemIcon>
                                    <ShoppingBagIcon />
                                </ListItemIcon>
                                <ListItemText primary="Catalog" />
                            </ListItem>
                            <ListItem
                                component="a"
                                href="https://github.com/Yalz/Linked-Data-Interactions/issues?q=label%3A%22New%20Component%22"
                                target="_blank"
                                rel="noopener noreferrer">
                                <ListItemIcon>
                                    <GitHubIcon />
                                </ListItemIcon>
                                <ListItemText primary="Suggest new component" />
                            </ListItem>
                        </List>
                    </AccordionDetails>
                </Accordion>

                <Divider sx={{ my: 2 }} />

                <Accordion expanded={open === 3} onChange={() => handleOpen(3)}>
                    <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                        <ListItemIcon>
                            <HttpIcon />
                        </ListItemIcon>
                        <Typography>Http Debug</Typography>
                    </AccordionSummary>
                    <AccordionDetails>
                        <List disablePadding>
                            <ListItem component={Link} to="http-debug/sink">
                                <ListItemIcon>
                                    <MoveToInbox />
                                </ListItemIcon>
                                <ListItemText primary="Http Sink" />
                            </ListItem>
                            <ListItem component={Link} to="http-debug/send">
                                <ListItemIcon>
                                    <Outbox />
                                </ListItemIcon>
                                <ListItemText primary="Http Send" />
                            </ListItem>
                        </List>
                    </AccordionDetails>
                </Accordion>

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
            </List>
        </Paper>
    );
}
