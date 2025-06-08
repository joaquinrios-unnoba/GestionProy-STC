import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Card, Form } from 'react-bootstrap';

function DashboardPage() {
    const [archivo, setArchivo] = useState(null);
    const [csvGenerado, setCsvGenerado] = useState(null);
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
        fetch('http://localhost:8080/api/user', {
            method: 'GET',
            credentials: 'include',
        })
        .then((response) => {
            if (!response.ok) {
                navigate('/login');
            }
        })
        .catch(() => navigate('/login'));
    }, [navigate]);

    const handleLogout = () => {
        window.location.href = "http://localhost:8080/logout";
    };

    const handleFileChange = (e) => {
        setArchivo(e.target.files[0]);
        setCsvGenerado(null); // Reinicia si seleccionás nuevo archivo
    };

    const handleUpload = () => {
        if (!archivo) {
            alert("Por favor, seleccioná un archivo.");
            return;
        }

        const formData = new FormData();
        formData.append("archivo", archivo);

        fetch("http://localhost:8080/api/upload", {
            method: "POST",
            body: formData,
            credentials: "include",
        })
        .then(res => res.text())
        .then(csv => {
            setCsvGenerado(csv);
            alert("Archivo procesado con éxito.");
        })
        .catch(err => {
            console.error("Error:", err);
            alert("Error al procesar el archivo.");
        });
    };

    const handleDescargarCSV = () => {
        const blob = new Blob([csvGenerado], { type: 'text/csv' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'respuesta.csv';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    const handleEnviarCSV = () => {
        const blob = new Blob([csvGenerado], { type: 'text/csv' });
        const file = new File([blob], "respuesta.csv", { type: "text/csv" });

        const formData = new FormData();
        formData.append("file", file);

        fetch("http://localhost:8080/subir-csv", {
            method: "POST",
            body: formData,
            credentials: "include"
        })
        .then(res => {
            if (res.ok) {
                alert("CSV enviado al calendario con éxito.");
            } else {
                alert("Error al enviar CSV al calendario.");
            }
        })
        .catch(err => {
            console.error("Error:", err);
            alert("Error de conexión al enviar CSV.");
        });
    };

    return (
        <Container className="mt-5">
            <Row>
                <Col>
                    <Card className="shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Dashboard</h2>
                            <p className="text-center">¡Has iniciado sesión correctamente con Google!</p>

                            <Form.Group controlId="formFile" className="mb-3">
                                <Form.Label>Seleccioná un archivo (PDF o imagen)</Form.Label>
                                <Form.Control
                                    type="file"
                                    onChange={handleFileChange}
                                    ref={fileInputRef}
                                />
                            </Form.Group>

                            <div className="d-flex flex-column align-items-center mt-2">
                                <Button
                                    variant="primary"
                                    size="sm"
                                    onClick={handleUpload}
                                    style={{ minWidth: '120px', height: '44px' }}
                                >
                                    Subir archivo
                                </Button>
                            </div>

                            {csvGenerado && (
                                <>
                                    <div className="d-flex flex-column align-items-center mt-4">
                                        <Button
                                            variant="success"
                                            size="sm"
                                            onClick={handleDescargarCSV}
                                            style={{ minWidth: '120px', height: '44px' }}
                                        >
                                            Descargar CSV
                                        </Button>
                                    </div>

                                    <div className="d-flex flex-column align-items-center mt-2">
                                        <Button
                                            variant="info"
                                            size="sm"
                                            onClick={handleEnviarCSV}
                                            style={{ minWidth: '120px', height: '44px' }}
                                        >
                                            Enviar a Google Calendar
                                        </Button>
                                    </div>
                                </>
                            )}

                            <div className="d-flex flex-column align-items-center mt-4">
                                <Button
                                    variant="danger"
                                    size="sm"
                                    onClick={handleLogout}
                                    style={{ minWidth: '120px', height: '44px' }}
                                >
                                    Cerrar sesión
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default DashboardPage;
