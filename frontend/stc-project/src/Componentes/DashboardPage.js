import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Card, Form } from 'react-bootstrap';

function DashboardPage() {
    const [archivo, setArchivo] = useState(null);
    const [response, setResponse] = useState(null);
    const [isUploading, setIsUploading] = useState(false);
    const [isExporting, setIsExporting] = useState(false);
    const [calendarTitle, setCalendarTitle] = useState("");
    const [calendarTitleError, setCalendarTitleError] = useState("");
    const [showModal, setShowModal] = useState(false);
    const [csvEvents, setCsvEvents] = useState([]);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        document.title = "Dashboard - STC";

        // Verificar si el usuario está autenticado
        fetch('http://localhost:8080/api/user', {
            method: 'GET',
            credentials: 'include',
        })
        .then((response) => {
            if (response.ok) {
                setIsAuthenticated(true);
            } else {
                setIsAuthenticated(false);
                navigate('/login');
            }
        })
        .catch((err) => {
            console.error("Error al verificar autenticación:", err);
            setIsAuthenticated(false);
            navigate('/login');
        });
    }, [navigate]);

    const handleLogout = () => {
        // Redirigir al endpoint de logout de Spring Security
        window.location.href = "http://localhost:8080/logout";
    };

    const handleFileChange = (e) => {
        setArchivo(e.target.files[0]);
    };

    const handleUpload = () => {
        if (!archivo) {
            alert("Por favor, seleccioná un archivo");
            return;
        }

        const formData = new FormData();
        formData.append("archivo", archivo);
        setIsUploading(true);

        fetch("http://localhost:8080/api/upload", {
            method: "POST",
            body: formData,
            credentials: "include",
        })
        .then(async res => {
            if (res.ok) {
                const data = await res.text();
                setResponse(data);
                const events = parseCsv(data);
                if (events.length > 0) {
                    setCsvEvents(events);
                    setShowModal(true);
                    console.log("Eventos del CSV:", events);
                }
                alert("Archivo subido con éxito");
                setArchivo(null);
                if (fileInputRef.current) {
                    fileInputRef.current.value = "";
                }
                setIsUploading(false);
            } else {
                alert("Error al subir el archivo");
            }
        })
        .catch(err => console.error("Error:", err));
    };

    function parseCsv(text) {
        // El backend devuelve el CSV envuelto en ```csv ... ```
        const csvMatch = text.match(/```csv\s*([\s\S]*?)\s*```/i);
        const csvText = csvMatch ? csvMatch[1].trim() : text.trim();
        const lines = csvText.split('\n').filter(Boolean);
        if (lines.length < 2) return [];
        const headers = lines[0].split(',').map(h => h.trim());
        return lines.slice(1).map(line => {
            const values = line.split(',').map(v => v.trim());
            const obj = {};
            headers.forEach((h, i) => obj[h] = values[i] ?? "");
            return obj;
        });
    }

    return (
        <Container className="mt-5">
            <Row>
                <Col>
                    <Card className="shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Dashboard</h2>
                            <p className="text-center">¡Has iniciado sesión correctamente con Google!</p>

                            <Form.Group controlId="formFile" className="mb-3">
                                <Form.Label>Seleccioná un archivo (PDF o imágen)</Form.Label>
                                <Form.Control
                                    type="file"
                                    onChange={handleFileChange}
                                    ref={fileInputRef}
                                    disabled={isUploading}
                                />
                            </Form.Group>

                            <div className="d-flex flex-column align-items-center mt-2">
                                <Button
                                    variant="primary"
                                    size="sm"
                                    onClick={handleUpload}
                                    style={{ minWidth: '120px', width: 'auto', height: '44px', fontSize: '1rem', padding: '0.5rem 1.2rem' }}
                                    disabled={isUploading}
                                >
                                    {isUploading ? "Subiendo..." : "Subir archivo"}
                                </Button>
                            </div>

                            <div className="d-flex flex-column align-items-center mt-4">
                                <Button
                                    variant="danger"
                                    size="sm"
                                    onClick={handleLogout}
                                    style={{ minWidth: '120px', width: 'auto', height: '44px', fontSize: '1rem', padding: '0.5rem 1.2rem' }}
                                    disabled={isUploading}
                                >
                                    Cerrar sesión
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* Modal superpuesto para mostrar eventos del CSV */}
            {showModal && (
                <div style={{
                    position: 'fixed',
                    top: 0, left: 0, right: 0, bottom: 0,
                    background: 'rgba(0,0,0,0.5)',
                    zIndex: 9999,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}>
                    <div style={{
                        background: '#fff',
                        borderRadius: '8px',
                        padding: '32px',
                        maxWidth: '90vw',
                        maxHeight: '80vh',
                        overflowY: 'auto',
                        boxShadow: '0 4px 32px rgba(0,0,0,0.2)'
                    }}>
                        <h4 className="mb-3">Eventos</h4>
                        <div style={{overflowX: 'auto'}}>
                            <table className="table table-bordered table-sm">
                                <thead>
                                    <tr>
                                        {csvEvents.length > 0 && Object.keys(csvEvents[0]).map((header, idx) => {
                                            let displayHeader = header;
                                            if (header === "Subject") displayHeader = "Título";
                                            else if (header === "Description") displayHeader = "Descripción";
                                            else if (header === "Start Date") displayHeader = "Fecha y hora de inicio";
                                            else if (header === "End Date") displayHeader = "Fecha y hora de fin";
                                            return <th key={idx}>{displayHeader}</th>;
                                        })}
                                    </tr>
                                </thead>
                                <tbody>
                                    {csvEvents.map((event, idx) => (
                                        <tr key={idx}>
                                            {Object.keys(event).map((header, i) => {
                                                let value = event[header];
                                                if (
                                                    (header === "Start Date" || header === "End Date") &&
                                                    /^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(:\d{2})?/.test(value)
                                                ) {
                                                    const date = new Date(value);
                                                    if (!isNaN(date)) {
                                                        const day = String(date.getDate()).padStart(2, '0');
                                                        const month = String(date.getMonth() + 1).padStart(2, '0');
                                                        const year = date.getFullYear();
                                                        const hours = String(date.getHours()).padStart(2, '0');
                                                        const minutes = String(date.getMinutes()).padStart(2, '0');
                                                        value = `${day}-${month}-${year} ${hours}:${minutes}`;
                                                    }
                                                }
                                                return <td key={i}>{value}</td>;
                                            })}
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        <Form className="mt-3">
                            <Form.Group controlId="calendarTitle">
                                <Form.Label>Título del calendario</Form.Label>
                                <Form.Control
                                    type="text"
                                    value={calendarTitle}
                                    onChange={e => {
                                        setCalendarTitle(e.target.value);
                                        if (e.target.value.trim() !== "") setCalendarTitleError("");
                                    }}
                                    isInvalid={!!calendarTitleError}
                                    placeholder="Ingrese el título del calendario"
                                />
                                <Form.Control.Feedback type="invalid">
                                    {calendarTitleError}
                                </Form.Control.Feedback>
                            </Form.Group>
                        </Form>
                        <div className="d-flex justify-content-between mt-3">
                            <div>
                                <Button
                                    variant="success"
                                    className="me-2"
                                    onClick={() => {
                                        if (!csvEvents.length) return;
                                        const headers = Object.keys(csvEvents[0]);
                                        const rows = csvEvents.map(ev => headers.map(h => `"${(ev[h] ?? '').replace(/"/g, '""')}"`).join(','));
                                        const csvContent = [headers.join(','), ...rows].join('\r\n');
                                        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
                                        const url = URL.createObjectURL(blob);
                                        const link = document.createElement('a');
                                        link.href = url;
                                        link.setAttribute('download', 'eventos.csv');
                                        document.body.appendChild(link);
                                        link.click();
                                        document.body.removeChild(link);
                                        URL.revokeObjectURL(url);
                                    }}
                                    disabled={isExporting}
                                >
                                    Descargar CSV
                                </Button>
                                <Button
                                    variant="info"
                                    onClick={async () => {
                                        if (!csvEvents.length) return;
                                        if (!calendarTitle.trim()) {
                                            setCalendarTitleError("El título no puede estar vacío.");
                                            return;
                                        }
                                        const headers = Object.keys(csvEvents[0]);
                                        const rows = csvEvents.map(ev => headers.map(h => `"${(ev[h] ?? '').replace(/"/g, '""')}"`).join(','));
                                        const csvContent = [headers.join(','), ...rows].join('\r\n');
                                        const blob = new Blob([csvContent], { type: 'text/csv' });
                                        const file = new File([blob], 'eventos.csv', { type: 'text/csv' });

                                        const formData = new FormData();
                                        formData.append('file', file);
                                        formData.append('calendarTitle', calendarTitle.trim());
                                        const timeZone = Intl.DateTimeFormat().resolvedOptions().timeZone || "America/Argentina/Buenos_Aires";
                                        formData.append('timeZone', timeZone);

                                        setIsExporting(true);
                                        try {
                                            const res = await fetch('http://localhost:8080/subir-csv', {
                                                method: 'POST',
                                                body: formData,
                                                credentials: 'include'
                                            });
                                            if (res.ok) {
                                                alert('Calendario exportado exitosamente a Google Calendar');
                                            } else {
                                                const text = await res.text();
                                                alert('Error al exportar: ' + text);
                                            }
                                        } catch (err) {
                                            alert('Error al exportar: ' + err);
                                        }
                                        setIsExporting(false);
                                    }}
                                    disabled={isExporting || !calendarTitle.trim()}
                                >
                                    {isExporting ? "Exportando..." : "Exportar a Google Calendar"}
                                </Button>
                            </div>
                            <div>
                                <Button variant="secondary"
                                    onClick={() => {
                                        setShowModal(false);
                                        setCalendarTitle("");
                                        setCalendarTitleError("");
                                    }}
                                    disabled={isExporting}
                                >
                                    Cerrar
                                </Button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </Container>
    );
}

export default DashboardPage;
