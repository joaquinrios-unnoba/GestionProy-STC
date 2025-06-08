import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Button, Card, Form } from 'react-bootstrap';

function DashboardPage() {
    const [archivo, setArchivo] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const fileInputRef = useRef(null);
    const navigate = useNavigate();

    useEffect(() => {
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
        window.location.href = "http://localhost:8080/logout";
    };

    const handleFileChange = (e) => {
        setArchivo(e.target.files[0]);
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
        .then(res => {
            if (res.ok) {
                alert("Archivo subido con éxito");
                setArchivo(null);
                if (fileInputRef.current) {
                    fileInputRef.current.value = "";
                }
            } else {
                alert("Error al subir el archivo");
            }
        })
        .catch(err => console.error("Error:", err));
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
                                    style={{ minWidth: '120px', width: 'auto', height: '44px', fontSize: '1rem', padding: '0.5rem 1.2rem' }}
                                >
                                    Subir archivo
                                </Button>
                            </div>

                            <div className="d-flex flex-column align-items-center mt-4">
                                <Button
                                    variant="danger"
                                    size="sm"
                                    onClick={handleLogout}
                                    style={{ minWidth: '120px', width: 'auto', height: '44px', fontSize: '1rem', padding: '0.5rem 1.2rem' }}
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
