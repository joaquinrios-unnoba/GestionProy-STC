import React from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';

function DashboardPage() {
    const handleLogout = () => {
<<<<<<< HEAD
        // Redirige al endpoint de logout de Spring Security
        window.location.href = 'http://localhost:8080/logout';
=======
        // Hacer logout en el backend
        fetch('http://localhost:8080/logout', {
            method: 'POST',
            credentials: 'include',
        })
        .then(response => {
            // Después de cerrar sesión, redirige al login del frontend
            window.location.href = 'http://localhost:3000/login';
        })
        .catch(err => console.error('Error al hacer logout:', err));
>>>>>>> origin/T002-1/T003-1
    };

    return (
        <Container className="mt-5">
            <Row>
                <Col>
                    <Card className="shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Dashboard</h2>
                            <p>¡Has iniciado sesión correctamente con Google!</p>
                            <p>Este es tu dashboard.</p>
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

<<<<<<< HEAD
export default DashboardPage;
=======
export default DashboardPage;
>>>>>>> origin/T002-1/T003-1
