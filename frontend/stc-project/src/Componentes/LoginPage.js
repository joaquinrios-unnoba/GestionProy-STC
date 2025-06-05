import React from 'react';
import { Container, Row, Col, Button, Card } from 'react-bootstrap';
<<<<<<< HEAD
//import './../Estilos/LoginPage.module.css';
=======
import '../Estilos/LoginPage.module.css';
>>>>>>> origin/T002-1/T003-1

function LoginPage() {
    const handleGoogleLogin = () => {
        // Redirige al endpoint de Spring Security que inicia el flujo de OAuth2 de Google
        window.location.href = 'http://localhost:8080/oauth2/authorization/google';
    };

    return (
<<<<<<< HEAD
        <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '100vh' }}>
            <Row>
                <Col md={8} lg={6} xl={5}>
                    <Card className="shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Bienvenido a tu STC</h2>
                            <p className="text-center">Por favor, inicia sesión para continuar.</p>
                            <div className="d-grid gap-2">
=======
        <Container className="login-container d-flex justify-content-center align-items-center" style={{ minHeight: "100vh" }}>
            <Row className="w-100 justify-content-center">
                <Col md={8} lg={6} xl={5}>
                    <Card className="login-card shadow-lg p-4">
                        <Card.Body>
                            <h2 className="text-center mb-4">Bienvenido a tu STC</h2>
                            <p className="text-center mb-4">Por favor, inicia sesión para continuar.</p>
                            <div className="d-grid gap-3">
>>>>>>> origin/T002-1/T003-1
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

<<<<<<< HEAD
export default LoginPage;
=======
export default LoginPage;
>>>>>>> origin/T002-1/T003-1
