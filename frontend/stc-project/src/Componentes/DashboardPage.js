import React from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';

function DashboardPage() {
    const handleLogout = () => {
        // Redirige al endpoint de logout de Spring Security
        window.location.href = 'http://localhost:8080/logout';
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

export default DashboardPage;