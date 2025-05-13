import 'bootstrap/dist/css/bootstrap.min.css';
import '@fortawesome/fontawesome-free/css/all.min.css'; // Add Font Awesome

const loginForm = document.getElementById("loginForm") as HTMLFormElement;
const loginButton = document.getElementById("loginButton") as HTMLButtonElement;
const loginButtonText = document.getElementById("loginButtonText") as HTMLSpanElement;
const loginSpinner = document.getElementById("loginSpinner") as HTMLSpanElement;
const loginMessage = document.getElementById("loginMessage") as HTMLDivElement;
const togglePasswordButton = document.getElementById(
  "togglePassword"
) as HTMLButtonElement;
const passwordInput = document.getElementById("password") as HTMLInputElement;
const usernameInput = document.getElementById("username") as HTMLInputElement;

togglePasswordButton?.addEventListener("click", () => {
  const type = passwordInput.type === "password" ? "text" : "password";
  passwordInput.type = type;
  togglePasswordButton.querySelector("i")?.classList.toggle("fa-eye");
  togglePasswordButton.querySelector("i")?.classList.toggle("fa-eye-slash");
});

loginForm?.addEventListener("submit", async (e) => {
  e.preventDefault();
  
  let isValid = true;
  
  if (!usernameInput.value.trim()) {
    usernameInput.classList.add("is-invalid");
    isValid = false;
  } else {
    usernameInput.classList.remove("is-invalid");
    usernameInput.classList.add("is-valid");
  }
  
  if (!passwordInput.value.trim()) {
    passwordInput.classList.add("is-invalid");
    isValid = false;
  } else {
    passwordInput.classList.remove("is-invalid");
    passwordInput.classList.add("is-valid");
  }
  
  if (!isValid) return;
  
  loginButton.disabled = true;
  loginButtonText.textContent = "Entrando...";
  loginSpinner.classList.remove("d-none");
  
  try {
    await new Promise((resolve) => setTimeout(resolve, 1500));
    
    loginMessage.innerHTML = `
      <div class="alert alert-success" role="alert">
        <i class="fas fa-check-circle me-2"></i>Login bem-sucedido! // Traduzido
      </div>
    `;

    // window.location.href = '/dashboard';
    
  } catch (error) {
    loginMessage.innerHTML = `
      <div class="alert alert-danger" role="alert">
        <i class="fas fa-exclamation-circle me-2"></i>Falha no login. Verifique suas credenciais. // Traduzido
      </div>
    `;
  } finally {
    loginButton.disabled = false;
    loginButtonText.textContent = "Login";
    loginSpinner.classList.add("d-none");
  }
});

[usernameInput, passwordInput].forEach((input) => {
  input.addEventListener("input", () => {
    input.classList.remove("is-invalid");
  });
});
