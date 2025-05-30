import React, { useState } from 'react';
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import jwt_decode from 'jwt-decode';

const clientId = "TU_CLIENT_ID_AQUÍ.apps.googleusercontent.com";

function App() {
    const [user, setUser] = useState(null);

    return (
        <GoogleOAuthProvider clientId={clientId}>
            <div style={{ textAlign: 'center', marginTop: '100px' }}>
                <h1>Login con Google (Test)</h1>

                {!user ? (
                    <GoogleLogin
                        onSuccess={(credentialResponse) => {
                            console.log("Token recibido:", credentialResponse);

                            const decoded = jwt_decode(credentialResponse.credential);
                            console.log("Token decodificado:", decoded);
                            setUser(decoded);
                        }}
                        onError={() => {
                            console.log("Falló el login");
                        }}
                    />
                ) : (
                    <div>
                        <h2>Hola, {user.name}</h2>
                        <p>{user.email}</p>
                    </div>
                )}
            </div>
        </GoogleOAuthProvider>
    );
}

