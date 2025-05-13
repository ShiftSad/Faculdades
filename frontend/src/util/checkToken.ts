function checkToken(token: string, username: string): boolean {
    const request = new XMLHttpRequest();
    request.open("POST", "http://127.0.0.1:3000/auth/validate", false);

    request.setRequestHeader("Content-Type", "application/json");
    request.send(JSON.stringify({ token: token }));

    const response = JSON.parse(request.responseText);
    if (response.success) {
        const data = response.data;
        if (data.username.toLowerCase() === username.toLowerCase()) {
            return true;
        } else {
            console.error("Token is valid but username does not match");
            return false;
        }
    }

    console.error("Token is invalid");
    return false;
}

export default checkToken;