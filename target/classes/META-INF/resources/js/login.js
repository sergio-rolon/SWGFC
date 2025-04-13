let url = "/api/login";
let urlLogged = "/api/usuarios/logged";

const contenedor = document.getElementById("contenedor");
const loginError = document.getElementById("loginError");
const correoInput = document.getElementById("correo-ipt");
const contrasenaInput = document.getElementById("password-ipt");
const btnLogIn = document.getElementById("btnLogIn");
const loader = document.getElementById("loader");

// Events
document.getElementById("btnLogIn").addEventListener("click", function (event) {
  event.preventDefault();
  cleanError();
  if (!validateNull()) {
    // Oculta el botón y muestra el mensaje de validación
    btnLogIn.style.visibility = "hidden";
    loader.style.display = "block";
    // Asegúrate de que grecaptcha se ha cargado antes de ejecutarlo
    if (typeof grecaptcha !== "undefined") {
      grecaptcha.ready(function () {
        grecaptcha
          .execute("6LeOkTAqAAAAAF8FEldq-RzmmB4OReSioONKtPRt", {
            action: "submit",
          })
          .then(function (token) {
            // Aquí podrías enviar el token al backend
            let email = correoInput.value;
            let contrasena = contrasenaInput.value;
            sendDataLogin(token, email, contrasena);
          });
      });
    } else {
      loginError.textContent = "reCAPTCHA no está cargado correctamente.";
      // Si hay un error, volvemos a mostrar el botón
      btnLogIn.style.visibility = "visible";
      loader.style.display = "none";
    }
  }
});

// Functions
function sendDataLogin(token, email, contrasena) {
  const myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const raw = JSON.stringify({
    token: token,
    email: email,
    contrasena: contrasena,
  });

  const requestOptions = {
    method: "POST",
    headers: myHeaders,
    body: raw,
    redirect: "follow",
  };

  fetch(url, requestOptions)
    .then((response) => {
      return response.json().then((result) => {
        if (response.ok) {
          return result;
        } else if (response.status === 400) {
          loginError.textContent = result.error;
          btnLogIn.style.visibility = "visible";
          loader.style.display = "none";
          throw new Error("Error");
        }
      });
    })
    .then((result) => {
      sessionStorage.setItem("token", result.accessToken);
      btnLogIn.style.visibility = "visible";
      loader.style.display = "none";
      window.location.href = "/index.html";
    })
    .catch((error) => {
      let errorMsg = error;
    });
}

function validateNull() {
  let flag = false;
  let msgError = "";
  if (!correoInput.value || correoInput.value.trim() === "") {
    msgError += "Email no puede ser nulo. ";
    correoInput.classList.add("borde-rojo");
    flag = true;
  }
  if (!contrasenaInput.value || contrasenaInput.value.trim() === "") {
    msgError += "Contraseña no puede ser nula. ";
    contrasenaInput.classList.add("borde-rojo");
    flag = true;
  }
  loginError.textContent = msgError;
  return flag;
}

function cleanError() {
  correoInput.textContent = "";
  correoInput.classList.remove("borde-rojo");
  contrasenaInput.textContent = "";
  contrasenaInput.classList.remove("borde-rojo");
  loginError.textContent = "";
}
