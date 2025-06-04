import React from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';
//import './../Estilos/LoginPage.module.css';

function LoginPage() {
    const handleGoogleLogin = () => {
        // Redirige al endpoint de Spring Security que inicia el flujo de OAuth2 de Google
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    };

    return (
        <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
            <Row>
                <Col md={8} lg={6} xl={5}>
                    <Card className="shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Bienvenido a tu STC</h2>
                            <p className="text-center">Por favor, inicia sesión para continuar.</p>
                            <div className="d-grid gap-2">
                                <Button variant="primary" size="lg" onClick={handleGoogleLogin}>
                                    Login con Google
                                </Button>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default LoginPage;