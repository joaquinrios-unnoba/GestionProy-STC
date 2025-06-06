import React, { useState } from 'react';
import { Container, Row, Col, Button, Card, Form } from 'react-bootstrap';

function DashboardPage() {

    const [archivo, setArchivo] = useState(null);

    const handleLogout = () => {
        // Hacer logout en el backend
        fetch('http://localhost:8080/logout', {//Creo que esto es inecesario, porque que usamos 
            method: 'POST',                    // el loguin de GOOGLE
            credentials: 'include',
        })
        .then(response => {window.location.href = 'http://localhost:3000/login';})// Después de cerrar sesión, redirige al login del frontend
            //.then(() => window.location.href = 'http://localhost:3000/login') (otra opcion)
        .catch(err => console.error('Error al hacer logout:', err));
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

        fetch("http://localhost:8080/api/upload", { //Aqui se hace la peticion (POST) al backend (archivoController)
            method: "POST",
            body: formData,
            credentials: "include", // si usamos cookies para el auth
        })
        .then(res => {
            if (res.ok) {
                alert("Archivo subido con éxito");
                setArchivo(null);
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
                            <p>¡Has iniciado sesión correctamente con Google!</p>

                            {/* Input de archivo */}
                            <Form.Group controlId="formFile" className="mb-3">
                                <Form.Label>Seleccioná un archivo (PDF o imagen)</Form.Label>
                                <Form.Control type="file" onChange={handleFileChange} />
                            </Form.Group>

                            <div className="d-grid gap-2 mt-2">
                                <Button variant="primary" onClick={handleUpload}>
                                    Subir archivo
                                </Button>
                            </div>

                            <div className="d-grid gap-2 mt-4">
                                <Button variant="danger" size="lg" onClick={handleLogout}>
                                    Cerrar Sesión
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